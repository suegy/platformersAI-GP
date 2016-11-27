/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package competition.gic2010.turing.Gaudl.gp;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jgap.*;
import org.jgap.gp.IGPFitnessEvaluator;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.INaturalGPSelector;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.GPPopulation;
import org.jgap.impl.Pool;
import org.jgap.util.*;


/**
 * A basic implementation of NaturalSelector that models a roulette wheel.
 * When a Chromosome is added, it gets a number of "slots" on the wheel equal
 * to its fitness value. When the select method is invoked, the wheel is
 * "spun" and the Chromosome occupying the spot on which it lands is selected.
 * Then the wheel is spun again and again until the requested number of
 * Chromosomes have been selected. Since Chromosomes with higher fitness
 * values get more slots on the wheel, there's a higher statistical probability
 * that they'll be chosen, but it's not guaranteed.
 *
 * @author Neil Rotstan
 * @author Klaus Meffert
 * @since 1.0
 */
public class WeightedGPRouletteSelector implements INaturalGPSelector, Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4713228930142743204L;

	/** String containing the CVS revision. Read out via reflection!*/
	private final static String CVS_REVISION = "$Revision: 1.44 $";

	//delta for distinguishing whether a value is to be interpreted as zero
	private static final double DELTA = 0.000001d;

	private static final BigDecimal ZERO_BIG_DECIMAL = new BigDecimal(0.0d);

	/**
	 * Represents the "roulette wheel." Each key in the Map is a Chromosome
	 * and each value is an instance of the SlotCounter inner class, which
	 * keeps track of how many slots on the wheel each Chromosome is occupying.
	 */
	private Map<IGPProgram,SlotCounter> m_wheel;

	/**
	 * Keeps track of the total number of slots that are in use on the
	 * roulette wheel. This is equal to the combined fitness values of
	 * all Chromosome instances that have been added to this wheel.
	 */
	private double m_totalNumberOfUsedSlots;

	/**
	 * An internal pool in which discarded SlotCounter instances can be stored
	 * so that they can be reused over and over again, thus saving memory
	 * and the overhead of constructing new ones each time.
	 */
	private transient Pool m_counterPool;
	
	/**
	 * Counter which monitors the current generation and tracks when a wheel needs to updated. As select is called 
	 * multiple times per generation is seemed a good idea to init the wheel only for a whole generation.
	 * @author Swen Gaudl
	 */
	private long m_generationCounter;
	private int m_activeGenotypeID;

	private WeightedGPRouletteSelConfig m_config;
	private GPConfiguration gpConfig;

    private double initial_newChromPercentage;

    
    
	public WeightedGPRouletteSelector(GPConfiguration config)
			throws InvalidConfigurationException {
		m_wheel = Collections.synchronizedMap(new HashMap<IGPProgram,SlotCounter>());
		m_counterPool = new Pool();
		m_config = new WeightedGPRouletteSelConfig();
		gpConfig = config;
		m_generationCounter = 0;
		m_config.m_doublettesAllowed = false;
		initial_mutationRate = gpConfig.getMutationProb();
        initial_newChromPercentage = gpConfig.getNewChromsPercent();
	}

	/**
	 * Add an igpprogram instance to this selector's working pool of chromosomes.
	 *
	 * @param a_prog the ipgprogram to add to the pool
	 *
	 * @author Swen Gaudl
	 * @author Neil Rotstan
	 * @author Klaus Meffert
	 * @since 1.0
	 */
	protected synchronized void add(final IGPProgram a_prog) {
		// The "roulette wheel" is represented by a Map. Each key is a
		// Chromosome and each value is an instance of the SlotCounter inner
		// class. The counter keeps track of the total number of slots that
		// each chromosome is occupying on the wheel (which is equal to the
		// combined total of their fitness values). If the Chromosome is
		// already in the Map, then we just increment its number of slots
		// by its fitness value. Otherwise we add it to the Map.
		// -----------------------------------------------------------------
		SlotCounter counter = (SlotCounter) m_wheel.get(a_prog);
		if (counter != null) {
			// The Chromosome is already in the map.
			// -------------------------------------
			counter.increment();
		}
		else {
			// We're going to need a SlotCounter. See if we can get one
			// from the pool. If not, construct a new one.
			// --------------------------------------------------------
			counter = (SlotCounter) m_counterPool.acquirePooledObject();
			if (counter == null) {
				counter = new SlotCounter();
			}
			counter.reset(a_prog.getFitnessValue());
			m_wheel.put(a_prog, counter);
		}
	}
	
	
	

	protected synchronized void addAll(final IGPProgram[] a_progs) {
		
		m_wheel.clear();
		
		for (IGPProgram igpProgram : a_progs) {
			add(igpProgram);
		}
	}
	
	/**
	 * Select a Chromosome from the pool that will move on
	 * to the next generation population. This selection should be guided by
	 * the fitness values, but fitness should be treated as a statistical
	 * probability of survival, not as the sole determining factor. In other
	 * words, Chromosome with higher fitness values should be more likely to
	 * be selected than those with lower fitness values, but it should not be
	 * guaranteed.
	 *
	 * @param a_howManyToSelect the number of Chromosomes to select
	 * @param a_to_pop the population the Chromosomes will be added to
	 *
	 * @author Swen Gaudl
	 * @since 1.0
	 */
	@Override
	public IGPProgram select(GPGenotype a_genotype) {
		RandomGenerator generator = a_genotype.getGPConfiguration().getRandomGenerator();
		
		// this should only be called once per generation to set up a new roulette wheel
		if (this.m_generationCounter <= a_genotype.getGPPopulation().getGPConfiguration().getGenerationNr() && this.m_activeGenotypeID != a_genotype.hashCode()){
			GPPopulation pop = a_genotype.getGPPopulation();
			this.m_activeGenotypeID = a_genotype.hashCode();
			this.empty();
			addAll(pop.getGPPrograms());
			System.out.println(String.format("num of chromosomes: %d",pop.getGPPrograms().length));
			scaleFitnessValues();
			this.m_generationCounter++;
		}
		
		// Build three arrays from the key/value pairs in the wheel map: one
		// that contains the fitness values for each igpprogram, one that
		// contains the total number of occupied slots on the wheel for each
		// igpprogram, and one that contains the igpprogram themselves. The
		// array indices are used to associate the values of the three arrays
		// together (eg, if a igpprogram is at index 5, then its fitness value
		// and counter values are also at index 5 of their respective arrays).
		// -------------------------------------------------------------------

		Vector<Double> fitnessValues = new Vector<Double>();
		Vector<Double> counterValues = new Vector<Double>();
		Vector<IGPProgram> programs = new Vector<IGPProgram>();
		m_totalNumberOfUsedSlots = 0.0d;

		for (Map.Entry<IGPProgram, SlotCounter> prog : m_wheel.entrySet()) {
			fitnessValues.add(prog.getValue().getFitnessValue());
			counterValues.add(prog.getValue().getFitnessValue() * prog.getValue().getCounterValue());
			programs.add(prog.getKey());
			// We're also keeping track of the total number of slots,
			// which is the sum of all the counter values.
			// ------------------------------------------------------
			m_totalNumberOfUsedSlots +=counterValues.lastElement();
		}
		// To select each igpprogram, we just "spin" the wheel and grab
		// whichever igpprogram it lands on.
		// ------------------------------------------------------------
		IGPProgram selectedIGPprog = spinWheel(generator, fitnessValues, counterValues,
				programs);

		return selectedIGPprog;
	}


	
	

	/**
	 * This method "spins" the wheel and returns the Chromosome that is
	 * "landed upon." Each time a chromosome is selected, one instance of it
	 * is removed from the wheel so that it cannot be selected again.
	 *
	 * @param a_generator the random number generator to be used during the
	 * spinning process
	 * @param a_fitnessValues an array of fitness values of the respective
	 * Chromosomes
	 * @param a_counterValues an array of total counter values of the
	 * respective Chromosomes
	 * @param a_chromosomes the respective Chromosome instances from which
	 * selection is to occur
	 * @return selected Chromosome from the roulette wheel
	 *
	 * @author Neil Rotstan
	 * @author Klaus Meffert
	 * @since 1.0
	 */
	private IGPProgram spinWheel(final RandomGenerator a_generator,
			final Vector<Double> a_fitnessValues,
			Vector<Double> a_counterValues,
			final Vector<IGPProgram> a_programs) {
		// Randomly choose a slot on the wheel.
		// ------------------------------------
		double selectedSlot =
				a_generator.nextDouble() * m_totalNumberOfUsedSlots;
		if (selectedSlot > m_totalNumberOfUsedSlots) {
			selectedSlot = m_totalNumberOfUsedSlots;
		}
		// Loop through the wheel until we find our selected slot. Here's
		// how this works: we have three arrays, one with the fitness values
		// of the chromosomes, one with the total number of slots on the
		// wheel that each chromosome occupies (its counter value), and
		// one with the chromosomes themselves. The array indices associate
		// each of the three together (eg, if a chromosome is at index 5,
		// then its fitness value and counter value are also at index 5 of
		// their respective arrays).
		//
		// We've already chosen a random slot number on the wheel from which
		// we want to select the Chromosome. We loop through each of the
		// array indices and, for each one, we add the number of occupied slots
		// (the counter value) to an ongoing total until that total
		// reaches or exceeds the chosen slot number. When that happens,
		// we've found the chromosome sitting in that slot and we return it.
		// --------------------------------------------------------------------
		double currentSlot = 0.0d;
		IGPFitnessEvaluator evaluator = gpConfig.getGPFitnessEvaluator();
		boolean isFitter2_1 = evaluator.isFitter(2, 1);
		for (int i = 0; i < a_counterValues.size(); i++) {
			// Increment our ongoing total and see if we've landed on the
			// selected slot.
			// ----------------------------------------------------------
			boolean found;
			if (isFitter2_1) {
				// Introduced DELTA to fix bug 1449651
				found = selectedSlot - currentSlot <= DELTA;
			}
			else {
				// Introduced DELTA to fix bug 1449651
				found = Math.abs(currentSlot - selectedSlot) <= DELTA;
			}
			if (found) {
				// Remove one instance of the chromosome from the wheel by
				// decrementing the slot counter by the fitness value resp.
				// resetting the counter if doublette chromosomes are not
				// allowed.
				// -------------------------------------------------------
				//TODO: check why the wheel is modified during selection what is the benefit of the modification
				if (!getDoubletteChromosomesAllowed()) {
					m_totalNumberOfUsedSlots -= a_counterValues.get(i);
					a_counterValues.set(i, 0d);
				}
				else {
					// commented because if doublettes are allowed reducing the fitness alters the selection process
					//a_counterValues.set(i, a_counterValues.get(i)- a_fitnessValues.get(i));
					//m_totalNumberOfUsedSlots -= a_fitnessValues.get(i);
				}
				// Introduced DELTA to fix bug 1449651
				if (Math.abs(m_totalNumberOfUsedSlots) < DELTA) {
					m_totalNumberOfUsedSlots = 0.0d;
				}
				// Now return our selected Chromosome.
				// -----------------------------------
				return a_programs.get(i);
			}
			else {
				currentSlot += a_counterValues.get(i);
			}
		}
		// We have reached here because there were rounding errors when
		// computing with doubles or because the last entry is the right one.
		// ------------------------------------------------------------------
		return a_programs.get(a_counterValues.size() -1);
	}

	/**
	 * Empty out the working pool of Chromosomes.
	 *
	 * @author Neil Rotstan
	 * @since 1.0
	 */
	public synchronized void empty() {
		// Put all of the old SlotCounters into the pool so that we can
		// reuse them later instead of constructing new ones.
		// ------------------------------------------------------------
		m_counterPool.releaseAllObjects(m_wheel.values());
		// Now clear the wheel and reset the internal state.
		// -------------------------------------------------
		m_wheel.clear();
		m_totalNumberOfUsedSlots = 0;
	}

	private double prev_largest = 0;
	private long prev_largest_gen = 0;
	private double initial_mutationRate = 0;
	
	private void scaleFitnessValues() {
		// First, add up all the fitness values. While we're doing this,
		// keep track of the largest fitness value we encounter.
		// -------------------------------------------------------------
		double largestFitnessValue = 0.0;
		BigDecimal totalFitness = ZERO_BIG_DECIMAL;
		for (SlotCounter counter : m_wheel.values()){
			
			if (counter.getFitnessValue() > largestFitnessValue) {
				largestFitnessValue = counter.getFitnessValue();
			}
            try {
                BigDecimal counterFitness = new BigDecimal(counter.getFitnessValue());
                totalFitness = totalFitness.add(counterFitness.multiply(
                        new BigDecimal(counter.getCounterValue())));
            }
			catch (NumberFormatException n){
                System.err.println(n);
                System.out.println("---err-wrong-fitness--");
                System.out.println("--- "+ counter.getFitnessValue() +" --");
                System.out.println("---err-wrong-fitness--");
                Logger.getLogger("genotype.log").log(Level.ALL, "-err-wrong-fitness-" + counter.getCounterValue());
                totalFitness = totalFitness.add(new BigDecimal(0.00001f).multiply(new BigDecimal(counter.getCounterValue())));
            }
		}
		/* storing the previous best fitness and its generation */
		if (largestFitnessValue > prev_largest){
			prev_largest = largestFitnessValue;
			prev_largest_gen = m_generationCounter;
		} 
		
		long unChangedFitness = m_generationCounter - prev_largest_gen;
		if (m_generationCounter >= 50 && unChangedFitness <= 100) {
			this.gpConfig.setMutationProb((float) initial_mutationRate);
			this.gpConfig.setNewChromsPercent(initial_newChromPercentage);
			//prog.getGPConfiguration().setCrossoverProb(0.9f);
			//prog.getGPConfiguration().setReproductionProb(0.1f);
		}
		/* if the crossover did not bring any useful results for the previous 100 generations try to 
		 * increase the mutation probability to bring a greater varierty of chromosomes into the pool
		 * there is a danger of mutating too much if no good solution is found within the next 50 generations*/
		
		if (unChangedFitness > 100 && this.gpConfig.getMutationProb() < this.initial_mutationRate) {
			double prob = this.gpConfig.getMutationProb();
			System.out.println(String.format("==mutation: %05f=============",prob));
			this.gpConfig.setMutationProb((float)(prob+prob*.01f));
			prob = this.gpConfig.getNewChromsPercent();
			System.out.println(String.format("==new chrom: %05f=============",prob));
			this.gpConfig.setNewChromsPercent((float)(prob+prob*.01f));
		}

		// Now divide the total fitness by the largest fitness value to
		// compute the scaling factor.
		// ------------------------------------------------------------
		if (largestFitnessValue > 0.000000d
				&& totalFitness.floatValue() > 0.0000001d) {
			double scalingFactor =
					totalFitness.divide(new BigDecimal(largestFitnessValue),
							BigDecimal.ROUND_HALF_UP).doubleValue();
			// Divide each of the fitness values by the scaling factor to
			// scale them down.
			// ----------------------------------------------------------
			System.out.println(String.format("==gen: %05d=============",m_generationCounter));
			System.out.println("scaling factor: "+scalingFactor);
			System.out.println("total fitness: "+totalFitness.floatValue());
			System.out.println(String.format("largest fitness: %.6f  since gen: %d", largestFitnessValue, prev_largest_gen));
			System.out.println("avg fitness: "+totalFitness.floatValue()/m_wheel.size());
			System.out.println("=========================");
			
			for (SlotCounter counter : m_wheel.values()) 
				counter.scaleFitnessValue(scalingFactor);

		}
	}

	/**
	 * @return always false as some Chromosome's could be returnd multiple times
	 *
	 * @author Klaus Meffert
	 * @since 2.0
	 */
	public boolean returnsUniqueChromosomes() {
		return false;
	}

	/**
	 * Not supported by this selector! Please do not use it.
	 *
	 * @param a_doublettesAllowed do not use
	 *
	 * @author Klaus Meffert
	 * @since 2.0
	 */
	public void setDoubletteChromosomesAllowed(final boolean a_doublettesAllowed) {
		throw new IllegalStateException("Weighted roulette selector does not"
				+" support this parameter,"
				+" please do not use it!");
	}

	/**
	 * @return TRUE: doublette chromosomes allowed to be added by the selector
	 *
	 * @author Klaus Meffert
	 * @since 2.0
	 */
	public boolean getDoubletteChromosomesAllowed() {
		return true;
	}

	/**
	 * @return deep clone of this instance
	 *
	 * @author Klaus Meffert
	 * @since 3.2
	 */
	public Object clone() {
		try {
			WeightedGPRouletteSelector result = new WeightedGPRouletteSelector(gpConfig);
			HashMap<IGPProgram, SlotCounter> wheel = new HashMap<IGPProgram,SlotCounter>();
			for (Map.Entry<IGPProgram, SlotCounter> it_elem : m_wheel.entrySet()) {
				wheel.put((IGPProgram)it_elem.getKey().clone(), (SlotCounter)it_elem.getValue().clone());
			}
			result.m_wheel = wheel;
			result.m_generationCounter = this.m_generationCounter;
			result.m_config = new WeightedGPRouletteSelConfig();
			result.m_config.m_doublettesAllowed = m_config.m_doublettesAllowed;
			return result;
		} catch (InvalidConfigurationException iex) {
			throw new CloneException(iex);
		}
	}

	public boolean equals(Object o) {
		WeightedGPRouletteSelector other = (WeightedGPRouletteSelector) o;
		if (other == null) {
			return false;
		}
		if (m_totalNumberOfUsedSlots != other.m_totalNumberOfUsedSlots) {
			return false;
		}
		if (other.m_config == null) {
			return false;
		}
		if (m_config.m_doublettesAllowed != other.m_config.m_doublettesAllowed) {
			return false;
		}
		if (other.m_counterPool == null) {
			return false;
		}
		if (!m_wheel.equals(other.m_wheel)) {
			return false;
		}
		return true;
	}

	class WeightedGPRouletteSelConfig
	implements Serializable {
		/**
		 * 
		 */
		 private static final long serialVersionUID = 4753584152286633988L;
		 /**
		  * Allows or disallows doublette chromosomes to be added to the selector
		  */
		 public boolean m_doublettesAllowed;
	}


}
/**
 * Implements a counter that is used to keep track of the total number of
 * slots that a single Chromosome is occupying in the roulette wheel. Since
 * all equal copies of a chromosome have the same fitness value, the increment
 * method always adds the fitness value of the chromosome. Following
 * construction of this class, the reset() method must be invoked to provide
 * the initial fitness value of the Chromosome for which this SlotCounter is
 * to be associated. The reset() method may be reinvoked to begin counting
 * slots for a new Chromosome.
 *
 * @author Neil Rotstan
 * @since 1.0
 */
class SlotCounter implements Cloneable{
	/**
	 * The fitness value of the Chromosome for which we are keeping count of
	 * roulette wheel slots. Although this value is constant for a Chromosome,
	 * it's not declared final here so that the slots can be reset and later
	 * reused for other Chromosomes, thus saving some memory and the overhead
	 * of constructing them from scratch.
	 */
	private double m_fitnessValue;

	/**
	 * The current number of Chromosomes represented by this counter.
	 */
	private int m_count;

	/**
	 * Resets the internal state of this SlotCounter instance so that it can
	 * be used to count slots for a new Chromosome.
	 *
	 * @param a_initialFitness the fitness value of the Chromosome for which
	 * this instance is acting as a counter
	 *
	 * @author Neil Rotstan
	 * @since 1.0
	 */
	public void reset(final double a_initialFitness) {
		m_fitnessValue = a_initialFitness;
		m_count = 1;
	}
	
	/**
	 * Allows SlotCounter to be cloned and used in deep copies of the roulette wheel
	 * @author Swen Gaudl
	 */
	public Object clone(){
		SlotCounter out = new SlotCounter();
		out.m_fitnessValue = this.m_fitnessValue;
		out.m_count = this.m_count;
		return out;
	}

	/**
	 * Retrieves the fitness value of the chromosome for which this instance
	 * is acting as a counter.
	 *
	 * @return the fitness value that was passed in at reset time
	 *
	 * @author Neil Rotstan
	 * @since 1.0
	 */
	public double getFitnessValue() {
		return m_fitnessValue;
	}

	/**
	 * Increments the value of this counter by the fitness value that was
	 * passed in at reset time.
	 *
	 * @author Neil Rotstan
	 * @since 1.0
	 */
	public void increment() {
		m_count++;
	}

	/**
	 * Retrieves the current value of this counter: ie, the number of slots
	 * on the roulette wheel that are currently occupied by the Chromosome
	 * associated with this SlotCounter instance.
	 *
	 * @return the current value of this counter
	 */
	public int getCounterValue() {
		return m_count;
	}

	/**
	 * Scales this SlotCounter's fitness value by the given scaling factor.
	 *
	 * @param a_scalingFactor the factor by which the fitness value is to be
	 * scaled
	 *
	 * @author Neil Rotstan
	 * @since 1.0
	 */
	public void scaleFitnessValue(final double a_scalingFactor) {
		m_fitnessValue /= a_scalingFactor;
	}
}

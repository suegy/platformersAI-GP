package competition.gic2010.turing.Gaudl;

import gamalyzer.data.input.Domains;
import gamalyzer.data.input.Trace;
import gamalyzer.data.input.Traces;

import java.io.File;
import java.io.IOException;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;
import clojure.lang.IPersistentVector;
import clojure.lang.LazySeq;
import competition.gic2010.turing.Gaudl.Genes.MarioData;


/*
 * This metric is utilizing the Gamalyzer done by Joseph Osborn
 */
public class GamalyzerFitness extends GameplayMetricFitness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8750632855093986122L;
	
	private Traces referenceTraces;
	private int simulationTime;
	//private Trace refTrace;

	public GamalyzerFitness(BasicTask task,MarioAIOptions options){
		super(task, options);
		num_lvls = 1;
		//File f = new File("human-ld1-lvl1.act");
		File [] hTraces = new File[2];
		hTraces[0] = new File("dataset"+File.separator+"players-test2-lvl-0-time-200-difficulty-0-trial-1.act");
		hTraces[1] = new File("dataset"+File.separator+"players-test2-lvl-1-time-200-difficulty-0-trial-1.act");
		bestFit = 1.0;
				
		referenceTraces = gamalyzer.read.Mario.readLogs(hTraces);
	}
	
	private int getTraceLength(Trace trace){
		int length = 0;
		if (trace.inputs instanceof IPersistentVector)		
		 length = ((IPersistentVector)trace.inputs).length();
		else if (trace.inputs instanceof LazySeq)
			length = ((LazySeq)trace.inputs).count();
		
		
		return length;
		
	}
	
	/*
	 * compared current trace of an agent to the one specified by the ref_num which 
	 * is read during instantiation 
	 */
	private float CompareTrace(Trace current,int ref_num,Domains domains,int time){
		IPersistentVector t = (IPersistentVector)referenceTraces.traces;
		Trace refTrace = (Trace)t.entryAt(ref_num).getValue();
		// System.out.println(gamalyzer.cmp.tt.stringify(current));
		// System.out.println("vs");
		// System.out.println(gamalyzer.cmp.tt.stringify(refTrace));
		// System.out.println("---");
		int length = getTraceLength(refTrace);
		int currLength = getTraceLength(current);
		refTrace = gamalyzer.data.util.trim(refTrace,simulationTime);
		if (length < currLength)
			current = gamalyzer.data.util.trim(current,refTrace.values().size());
		
		double dissimilarity = 1.0f;
		try {
		dissimilarity = gamalyzer.cmp.tt.distance(current,refTrace,domains,20);
		} 
		catch (clojure.lang.ArityException e) {
			System.out.println(e);
			dissimilarity = 1.0f;
		}
		System.out.print(dissimilarity+"-");
		
		return (float)dissimilarity;
	}

	protected double runFitness(IGPProgram prog) {
		double error = 0.0f;
		distance = new int[num_lvls];
		MarioData data = new MarioData();
		// Initialize local stores.
		// ------------------------
		//prog.getGPConfiguration().clearStack();
		//prog.getGPConfiguration().clearMemory();
		prog.setApplicationData(data);
		simulationTime = 0;
		int num_lvls = this.num_lvls;
		try {
			// Execute the program.
			// --------------------
			if (prog.getGPConfiguration().getGenerationNr() < 20){
				
				distance = new int[num_lvls];
				simulationTime = 2;
			} else 
				if (bestFit < 50d){
					simulationTime = 50;	
				} else if (bestFit < 100){
					simulationTime = 100;
				} else {
					simulationTime = 200;
				}
			for (int lvl=0;lvl < num_lvls;lvl++){
				runMarioTask(prog,data,simulationTime,lvl);
				distance[lvl]=MarioData.getEnvironment().getEvaluationInfo().distancePassedCells;
				error += calculateFitness(MarioData.getEnvironment().getEvaluationInfo());
			}
			// Determine success of individual in #lvls by averaging over all played levels
			// --------------------------------
			error = error/num_lvls;
			//System.out.print(error+": "+distance[0]+"; ");
			// Check if the action the agent chose is close to the trace action.
			// -------------------------------------------

			//boolean[] actions = data.getActions(); // agent actions;

			//use dissimilarity here to calculate the deviation from right path

			//error = "Joes Metric".length();
			/*try {
				ProgramChromosome chrom= new ProgramChromosome(prog.getGPConfiguration());
				String chromRepresentation = prog.getChromosome(0).getPersistentRepresentation();
				chrom.setValueFromPersistentRepresentation(chromRepresentation);
				//prog.getGPConfiguration().getChromosomePool().releaseChromosome((IChromosome)chrom);
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedRepresentationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			if (prog.getGPConfiguration().stackSize() > 0) {
				error = GPFitnessFunction.MAX_FITNESS_VALUE;
			}
			if (error < 0.000001) {
				error = 0.0d;
			}
			else if (error < GPFitnessFunction.MAX_FITNESS_VALUE) {
				// Add penalty 
				// ------------------------------

				
					
			}
		} catch (IllegalStateException iex) {
			error = GPFitnessFunction.MAX_FITNESS_VALUE;
			System.out.println(iex);
		}

		// if we are using delta distance we need to use "<" because we care for smaller errors
		if (error > bestFit ){
			System.out.println("reached a good solution");

			try {
				String distArray = "";
				for (int len : distance) {
					distArray += len+" ";
				}
				writer.append("gen: "+ prog.getGPConfiguration().getGenerationNr()  +" fit:"+error+" dist: "+distArray+" Prog: "+prog.toStringNorm(0)+"\n");
				writer.append("pers:"+prog.getPersistentRepresentation());
				
				writer.flush();

				//a.write();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bestFit = error;
		}
			

		return error;
	}

	
	
	protected double calculateFitness(EvaluationInfo env){
		Traces currentRaw = gamalyzer.read.Mario.readActions(referenceTraces, MarioData.getActionTrace());
		IPersistentVector t = (IPersistentVector)currentRaw.traces;
		Trace current = (Trace) t.entryAt(0).getValue();
		double weight = CompareTrace(current, 0, (Domains)currentRaw.domains,simulationTime);
		System.out.print(MarioData.getEnvironment().getEvaluationInfo().distancePassedCells+";");
		return MarioData.getEnvironment().getEvaluationInfo().distancePassedCells * (1.01d-weight);
	}

}

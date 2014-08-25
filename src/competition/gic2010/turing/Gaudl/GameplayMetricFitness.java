package competition.gic2010.turing.Gaudl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.UnsupportedRepresentationException;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPProgram;
import org.jgap.gp.impl.ProgramChromosome;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;
import competition.gic2010.turing.Gaudl.gp.MarioData;

public class GameplayMetricFitness extends GPFitnessFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8750632855093986122L;
	BasicTask m_task;
	MarioAIOptions m_options;
	private int gen;
	protected double bestFit;
	protected BufferedWriter writer;
	protected int num_lvls;
	protected int[] distance;
	

	public GameplayMetricFitness(BasicTask task,MarioAIOptions options){
		m_task = task;
		m_options = options;
		gen = 0;
		bestFit = 40d;
		num_lvls = 2; //should be the number of different levels we have data on
		
		try {
			int counter = 0;
			File output = new File(String.format("solution"+File.separator+"solutions-%s.txt", counter));
			while(output.exists()) {
				output = new File(String.format("solution"+File.separator+"solutions-%s.txt", counter++));
			}
			writer = new BufferedWriter(new FileWriter(output));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected double evaluate(IGPProgram arg0) {
		return runFitness(arg0);
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
		int time = 0;
		int num_lvls = this.num_lvls;
		try {
			// Execute the program.
			// --------------------
			if (prog.getGPConfiguration().getGenerationNr() < 20){
				num_lvls = 20;
				distance = new int[num_lvls];
				time = 2;
			} else 
				if (bestFit < 50d){
					time = 50;	
				} else if (bestFit < 100){
					time = 100;
				} else {
					time = 200;
				}
			for (int lvl=0;lvl < num_lvls;lvl++){
				runMarioTask(prog,data,time,lvl);
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
				//writer.append("pers:"+prog.getPersistentRepresentation());
				
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

	protected boolean  runMarioTask(IGPProgram prog,MarioData data,int time,int lvl) {
		Object[] noargs = new Object[0];
		Mario_GPAgent mario = new Mario_GPAgent(prog, noargs, data);
		mario.setName("gp-"+prog.getGPConfiguration().getId());
		m_options.setRecordFile("gp-"+prog.getGPConfiguration().getId());
		m_options.setTimeLimit(time);
		m_options.setLevelRandSeed(lvl);
		
		//m_options.setLevelRandSeed(151079);
		m_options.setAgent(mario);
		m_task.setOptionsAndReset(m_options);
		
		return m_task.runSingleEpisode(1);
	}


	protected double calculateFitness(EvaluationInfo env){
		double wfit = (env.distancePassedCells);
		//wfit = wfit /env.timeSpent;
		double additional= (env.killsTotal + env.coinsGained + env.marioMode)*.2f;
		wfit -= env.collisionsWithCreatures;
		if (additional <  wfit/2 || wfit <= 0)
			wfit = wfit + additional;
		else {
			additional = (additional * (0.2 / wfit));
			wfit += (wfit*0.2f)*additional;
		}
		if (env.distancePassedCells > 50 && env.marioStatus == Mario.STATUS_DEAD)
			wfit = wfit*0.75f;
		if (env.distancePassedCells == env.levelLength && env.marioStatus == Mario.STATUS_WIN){
			System.out.print("Solved Level");
			wfit = wfit*1.1f;
		}
		return wfit;
	}

}

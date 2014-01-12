package competition.gic2010.turing.Gaudl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;

import bsh.Console;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;
import competition.gic2010.turing.Gaudl.Genes.MarioData;
import gamalyzer.data.input.Traces;

public class GamealyzerFitness extends GPFitnessFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8750632855093986122L;
	BasicTask m_task;
	MarioAIOptions m_options;
	private int gen;
	private double bestFit;
	private BufferedWriter writer;

	public GamealyzerFitness(BasicTask task,MarioAIOptions options){
		m_task = task;
		m_options = options;
		gen = 0;
		bestFit = 40d;
		gamalyzer.read.Mario m = new gamalyzer.read.Mario();
		Traces traces = m.readLogs(new File[] {new File("human-ld1-lvl1.zip"),});
		try {
			writer = new BufferedWriter(new FileWriter("solutions.txt"));
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
		MarioData data = new MarioData();
		// Initialize local stores.
		// ------------------------
		prog.getGPConfiguration().clearStack();
		prog.getGPConfiguration().clearMemory();
		prog.setApplicationData(data);
		try {
			// Execute the program.
			// --------------------
			int time =  prog.getGPConfiguration().getGenerationNr() / 5;
			if (prog.getGPConfiguration().getGenerationNr() > 5)
				time =  prog.getGPConfiguration().getGenerationNr() ;
			if (prog.getGPConfiguration().getGenerationNr() > 10)
				time *= 1.2  ;
			if (prog.getGPConfiguration().getGenerationNr() == 30) {
				prog.getGPConfiguration().setMutationProb(0.01f);
				prog.getGPConfiguration().setNewChromsPercent(0.3f);
				prog.getGPConfiguration().setCrossoverProb(0.9f);
				prog.getGPConfiguration().setReproductionProb(0.1f);
			}
			if (time >= 200)
				time = 200;
			time = 10+ time;
			runMarioTask(prog,data,time);
			// Determine success of individual.
			// --------------------------------
			error = calculateFitness(MarioData.getEnvironment().getEvaluationInfo());
			// Check if the action the agent chose is close to the trace action.
			// -------------------------------------------

			//boolean[] actions = data.getActions(); // agent actions;

			//use dissimilarity here to calculate the deviation from right path

			//error = "Joes Metric".length();

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
		if (prog.getGPConfiguration().getGenerationNr() > gen) {
    		if (error > bestFit ){
				System.out.println("reached a good solution");
				FileWriter a;
				try {
					if (prog.getGPConfiguration().getGenerationNr() > gen) {
			    		writer.append("gen: "+ gen  +" fit:"+error+" dist: "+MarioData.getEnvironment().getEvaluationInfo().distancePassedCells+" Prog: "+prog.toStringNorm(0)+"\n");
			    		writer.flush();
					}
			    	
					//a.write();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bestFit = error;
			}
    		System.out.println("gen: "+ gen++);
		}
		return error;
	}

	private boolean  runMarioTask(IGPProgram prog,MarioData data,int time) {
		Object[] noargs = new Object[0];
		Mario_GPAgent mario = new Mario_GPAgent(prog, noargs, data);
		mario.setName("gp-"+prog.getGPConfiguration().getId());
		m_options.setRecordFile("gp-"+prog.getGPConfiguration().getId());
		m_options.setTimeLimit(time);
		if (time > 50){
			int randSeed = time % 5;
			m_options.setLevelRandSeed(randSeed);
		}
		//m_options.setLevelRandSeed(151079);
		m_options.setAgent(mario);
		m_task.setOptionsAndReset(m_options);
		
		return m_task.runSingleEpisode(1);
	}


	private float calculateFitness(EvaluationInfo env){
		float wfit = (env.distancePassedCells);
		//wfit = wfit /env.timeSpent;
		float additional= (env.killsTotal + env.coinsGained + env.marioMode)*.2f;
		wfit -= env.collisionsWithCreatures;
		if (additional <  wfit/2)
			wfit = wfit + additional;
		else {
			additional = (float) (additional * (0.2 / wfit));
			wfit += (wfit*0.2f)*additional;
		}
		if (env.distancePassedCells > 50 && env.marioStatus == Mario.STATUS_DEAD)
			wfit = wfit*0.75f;
		if (env.distancePassedCells == env.levelLength && env.marioStatus == Mario.STATUS_WIN){
			System.out.print("Solved Level");
		}
		//wfit = 1f/wfit;

/*		if (wfit > 0.000000001f)
			wfit = (wfit*10);
		else
			wfit = (float) GPFitnessFunction.MAX_FITNESS_VALUE;
*/
		return wfit;
	}

}

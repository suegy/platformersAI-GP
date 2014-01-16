package competition.gic2010.turing.Gaudl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;

import competition.gic2010.turing.Gaudl.Genes.MarioData;

public class GameplayMetricFitness extends GPFitnessFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8750632855093986122L;
	BasicTask m_task;
	MarioAIOptions m_options;
	private int gen;
	protected double bestFit;
	private BufferedWriter writer;
	protected int num_lvls;
	protected int[] distance;
	

	public GameplayMetricFitness(BasicTask task,MarioAIOptions options){
		m_task = task;
		m_options = options;
		gen = 0;
		bestFit = 40d;
		num_lvls = 5;
		
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
		distance = new int[num_lvls];
		MarioData data = new MarioData();
		// Initialize local stores.
		// ------------------------
		prog.getGPConfiguration().clearStack();
		prog.getGPConfiguration().clearMemory();
		prog.setApplicationData(data);
		int time = 0;
		try {
			// Execute the program.
			// --------------------
			time = 200;
/*			time =  prog.getGPConfiguration().getGenerationNr() / 5;
			if (prog.getGPConfiguration().getGenerationNr() > 5)
				time =  prog.getGPConfiguration().getGenerationNr() ;
			if (prog.getGPConfiguration().getGenerationNr() > 10)
				time = 30;
				*/
			if (prog.getGPConfiguration().getGenerationNr() == 30) {
				prog.getGPConfiguration().setMutationProb(0.01f);
				prog.getGPConfiguration().setNewChromsPercent(0.3f);
				prog.getGPConfiguration().setCrossoverProb(0.9f);
				prog.getGPConfiguration().setReproductionProb(0.1f);
			}
/*
			if (prog.getGPConfiguration().getGenerationNr() > 40)
				time = 50;
			if (prog.getGPConfiguration().getGenerationNr() > 100)
				time = 100;
			if (prog.getGPConfiguration().getGenerationNr() > 200)
				time = 125;
			if (prog.getGPConfiguration().getGenerationNr() > 250)
				time = 150;
			if (prog.getGPConfiguration().getGenerationNr() > 300)
				time = 175;
			if (prog.getGPConfiguration().getGenerationNr() > 350)
				time = 200;
			time = 10+ time;*/
			for (int lvl=0;lvl < num_lvls;lvl++){
				runMarioTask(prog,data,time,lvl);
				distance[lvl]=MarioData.getEnvironment().getEvaluationInfo().distancePassedCells;
				error += calculateFitness(MarioData.getEnvironment().getEvaluationInfo());
			}
			// Determine success of individual in #lvls by averaging over all played levels
			// --------------------------------
			error = error/num_lvls;
			System.out.println(error+" ");
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
			System.out.println("\n gen: "+ gen++);
		}
		// if we are using delta distance we need to use "<" because we care for smaller errors
		if (error > bestFit ){
			System.out.println("reached a good solution");
			FileWriter a;
			try {
				String distArray = "";
				for (int len : distance) {
					distArray += len+" ";
				}
				writer.append("gen: "+ gen  +" fit:"+error+" dist: "+distArray+" Prog: "+prog.toStringNorm(0)+"\n");
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


	protected float calculateFitness(EvaluationInfo env){
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
		return wfit;
	}

}

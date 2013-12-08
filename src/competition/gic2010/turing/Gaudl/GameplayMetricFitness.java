package competition.gic2010.turing.Gaudl;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;

import ch.idsia.benchmark.mario.environments.Environment;
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

	public GameplayMetricFitness(BasicTask task,MarioAIOptions options){
		m_task = task;
		m_options = options;
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
			int time =  prog.getGPConfiguration().getGenerationNr() / 10;
			
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
		}
		System.out.println("gen: "+prog.getGPConfiguration().getGenerationNr()+" id: "+prog.getGPConfiguration().getId()+" fit: "+error+"; ");
		return error;
	}

	private boolean  runMarioTask(IGPProgram prog,MarioData data,int time) {
		Object[] noargs = new Object[0];
		Mario_GPAgent mario = new Mario_GPAgent(prog, noargs, data);
		mario.setName("gp-"+prog.getGPConfiguration().getId());
		m_options.setRecordFile("gp-"+prog.getGPConfiguration().getId());
		m_options.setTimeLimit(time);
		m_options.setAgent(mario);
		m_task.setOptionsAndReset(m_options);

		return m_task.runSingleEpisode(1);
	}


	private float calculateFitness(EvaluationInfo env){
		float wfit = (env.computeDistancePassed());
		wfit = wfit /env.timeSpent;
		wfit += env.killsTotal + env.coinsGained + env.marioMode;
		wfit -= env.collisionsWithCreatures;

/*		if (wfit > 0.000000001f)
			wfit = (wfit*10);
		else
			wfit = (float) GPFitnessFunction.MAX_FITNESS_VALUE;
*/
		return wfit;
	}

}

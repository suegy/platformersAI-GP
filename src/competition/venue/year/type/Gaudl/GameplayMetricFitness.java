package competition.venue.year.type.Gaudl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.reflect.VisibilityFilter;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;

import competition.venue.year.type.Gaudl.gp.MarioData;
import org.platformer.benchmark.platform.engine.sprites.Plumber;
import org.platformer.benchmark.tasks.BasicTask;
import org.platformer.benchmark.tasks.Task;
import org.platformer.tools.EvaluationInfo;
import org.platformer.tools.PlatformerAIOptions;

public class GameplayMetricFitness extends GPFitnessFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8750632855093986122L;
	Task m_task;
	PlatformerAIOptions m_options;
	private int gen;
	protected double bestFit;
	protected BufferedWriter writer;

    protected String[] mariologFiles;
	protected int num_lvls;
	protected int levelDifficulty;
	protected int[] distance;
	protected Genson jsonSerialiser;

	public GameplayMetricFitness(Task task,PlatformerAIOptions options){
		m_task = task;
		m_options = options;
		gen = 0;
		bestFit = 40d;
		num_lvls = 1; //should be the number of different levels we have data on
		levelDifficulty = 1;
		mariologFiles = new String[0];

		try {
			int counter = 0;
			File output = new File(String.format("solution"+File.separator+"solutions-%s.txt", counter));
			while(output.exists()) {
				output = new File(String.format("solution"+File.separator+"solutions-%s.txt", counter++));
			}
			writer = new BufferedWriter(new FileWriter(output));
			jsonSerialiser = new GensonBuilder()
					.useClassMetadata(true)
					.useMethods(false)
					.setSkipNull(true)
					.useFields(true, new VisibilityFilter(Modifier.TRANSIENT,Modifier.STATIC))
					.useClassMetadataWithStaticType(false)
					.create();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected double evaluate(Object arg0) {
		return runFitness((IGPProgram) arg0);
	}

	protected double runFitness(IGPProgram prog) {
		double error = 0.0f;
		distance = new int[num_lvls*3];
		MarioData data = new MarioData();
		// Initialize local stores.
		// ------------------------
		//prog.getGPConfiguration().clearStack();
		//prog.getGPConfiguration().clearMemory();
		prog.setApplicationData(data);
		int time = 200;
		boolean solvedAll = true;
		mariologFiles = new String[num_lvls*3];
		try {
			// Execute the program.
			// --------------------
//			if (prog.getGPConfiguration().getGenerationNr() < 50 || prog.getGPConfiguration().getGenerationNr() > 2000){
//				num_lvls = 10;
//				distance = new int[num_lvls];
//			} 

			String fitnessOutput = "";

            int offset = (num_lvls-1)*3;

            for (int lvl=0;lvl < 3;lvl++){
			    runMarioTask(prog,data,time,lvl+offset);
				distance[lvl+offset]=((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells;
				if (solvedAll && ((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().marioStatus != Plumber.STATUS_WIN)
					solvedAll = false;
				double fit = calculateFitness(((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo(), prog);
				fitnessOutput += fit+":"+distance[lvl+offset]+" ";
				error +=fit;
			}
			if (solvedAll) {
                this.num_lvls++;
                bestFit = 0;

            }
			prog.setAdditionalFitnessInfo(String.format(fitnessOutput.trim()));
			
			// Determine success of individual in #lvls by averaging over all played levels
			// --------------------------------
			error = error/3;
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
		if (error > bestFit){
			System.out.println("reached a good solution");
            logBest(error, prog);

			bestFit = error;
		}
			

		return error;
	}

    protected void logBest(double error, IGPProgram prog){
        if (prog.getGPConfiguration().getGenerationNr() < 1)
            return;
        try {
            String distArray = "";
            for (int len : distance) distArray += len + " ";

			String jsonRepresentation = jsonSerialiser.serialize(prog);

            //writer.append("gen: "+ prog.getGPConfiguration().getGenerationNr()  +" fit:"+error+" dist: "+distArray+" Prog: "+prog.toStringNorm(0)+"\n");
			writer.append("gen: "+ prog.getGPConfiguration().getGenerationNr()  +" fit:"+error+" dist: "+distArray+" Prog: "+jsonRepresentation+"\n");
			String json = jsonSerialiser.serialize(prog);

			//writer.append("pers:"+prog.getPersistentRepresentation());
            for (int i = 0;i< mariologFiles.length;i++){
            	File mariolog = new File(mariologFiles[i]+".zip");
            	if (mariolog.exists())
            		Files.copy(mariolog.toPath(),new File("solution"+File.separator+mariologFiles[i]+"-"+prog.getGPConfiguration().getGenerationNr()+"-fit"+error+".zip").toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            writer.flush();
            
            //a.write();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	protected boolean  runMarioTask(IGPProgram prog,MarioData data,int time,int level) {
		Object[] noargs = new Object[0];
		Mario_GPAgent mario = new Mario_GPAgent(prog, noargs, data);

        /**
         * changing what lvl to play to increase difficulty as well
         */
		int lvl = level % 30;
		int diff = levelDifficulty + level / 30;
        mariologFiles[lvl] = String.format("gp-lvl%d-diff%d",lvl,diff);
		mario.setName(mariologFiles[lvl]);
		m_options.setRecordFile(mariologFiles[lvl]);
		m_options.setTimeLimit(time);
		m_options.setLevelRandSeed(lvl);
		
		//m_options.setLevelRandSeed(151079);
		m_options.setLevelDifficulty(diff);
		m_options.setAgent(mario);
		m_task.setOptionsAndReset(m_options);
		
		return ((BasicTask)m_task).runSingleEpisode(1);
	}


	protected double calculateFitness(EvaluationInfo env, IGPProgram prog){
		double wfit = (env.distancePassedCells);
		double importance = 0.25d;
		//wfit = wfit /env.timeSpent;
		double additional= (env.killsTotal + env.coinsGained + env.marioMode-env.collisionsWithCreatures)*importance;
		
		if (additional <  wfit*importance || wfit <= 0)
			wfit = wfit + additional;
		else {
			wfit = wfit+additional*importance;
		}
		if (env.distancePassedCells > env.levelLength*importance && env.marioStatus == Plumber.STATUS_DEAD)
			wfit = wfit*0.75f;
		if (env.distancePassedCells == env.levelLength && env.marioStatus == Plumber.STATUS_WIN){
			System.out.print("Solved Level");
			wfit = wfit*1.1f;
		}
		System.out.print(wfit+";");
		return wfit;
	}
}

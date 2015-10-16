package competition.gic2010.turing.Gaudl;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import org.jgap.gp.IGPProgram;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.GPMirrorTask;
import ch.idsia.benchmark.tasks.ReplayTask;
import ch.idsia.benchmark.tasks.Task;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;
import clojure.lang.IPersistentVector;
import clojure.lang.LazySeq;
import competition.gic2010.turing.Gaudl.gp.MarioData;


/*
 * This metric is utilizing the Gamalyzer done by Joseph Osborn
 */
public class TraceFitness extends GameplayMetricFitness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8750632855093986122L;
	
	private byte [][] referenceTraces;
	String [] referenceTraceFiles;
	private int simulationTime;
	private int gamalyzerFramesPerChunk = 12;
	private int slidingWindow = 10;
	//private Trace refTrace;

	public TraceFitness(Task task,MarioAIOptions options){
		super(task, options);
		num_lvls = 1;
		//File f = new File("human-ld1-lvl1.act");
		referenceTraceFiles = new String [2];
		referenceTraceFiles[0] = "players-19022014s1-p-test2-lvl-0-time-200-difficulty-0-trial-1";
		referenceTraceFiles[1] = "players-19022014s1-p-test2-lvl-1-time-200-difficulty-0-trial-1";
		bestFit = 0.01;
		referenceTraces = new byte[2][];		
		// reading the tracing at 15chunks per second
		try {
			referenceTraces[0] =  Files.readAllBytes(new File(referenceTraceFiles[0]+".act").toPath());
			referenceTraces[1] =  Files.readAllBytes(new File(referenceTraceFiles[1]+".act").toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * compared current trace of an agent to the one specified by the ref_num which 
	 * is read during instantiation 
	 */
	private float CompareTrace(byte[] current,int ref_num, int time){
		
		byte[] refTrace = referenceTraces[ref_num];
		
		int length = refTrace.length;
		
		int currLength = current.length;
		refTrace = trimTrace(refTrace,time);
		int length2 = refTrace.length;
		
		if (length2 < currLength)
			current = trimTrace(current,length2);
		
		
		
		return (float) similarity(current,refTrace,slidingWindow);
	}

	private double similarity(byte[] current, byte[] refTrace,
			int slidingWindow2) {
		// currently the simplest comparison is a one to one mapping ignoring the sliding window
		double weight  = 0;
		// TODO create a gaussian fade from the ref to the current trace to allow some inaccuracy
		for (int i= 0; i< current.length;i++){
			weight += compareAction(current[i],refTrace[i]);
		}
		
		if (current.length < refTrace.length)
			weight *= 0.99d*current.length/refTrace.length; // let the length
		weight /= refTrace.length;
		
		return weight;
	}

	private double compareAction(byte curr, byte ref) {
		BitSet reference = BitSet.valueOf(new byte[] {ref});
		BitSet current = BitSet.valueOf(new byte[] {curr});
		
		reference.xor(current);
		
		return (reference.length() - reference.cardinality())/8d;
	}

	private byte[] trimTrace(byte[] trace, int newLength) {
		if (trace.length < newLength)
			return trace;
		
		byte[] output = new byte[newLength];
		
		for (int i = 0; i < newLength;i++){
			output[i] = trace[i];
		}
			
		return output;
	}
	
	@Override
	public void sequentialStep(IGPProgram [] programs){
		runReplayTask(programs,simulationTime, 0);
	}

	@Override
	protected double runFitness(IGPProgram prog) {
		double error = 0.0f;
		distance = new int[num_lvls];
		mariologFiles = new String[num_lvls];
		// Initialize local stores.
		// ------------------------
		//prog.getGPConfiguration().clearStack();
		//prog.getGPConfiguration().clearMemory();
		MarioData data = null;
		if (prog.getApplicationData() !=null){
			data = (MarioData) prog.getApplicationData();
		} else {
			data = new MarioData();
			prog.setApplicationData(data);
		}
		
		simulationTime = 200;
		int num_lvls = this.num_lvls;
		try {
			// Execute the program.
			// --------------------
			/*	if (prog.getGPConfiguration().getGenerationNr() < 30){
				
				distance = new int[num_lvls];
				simulationTime = 100;
			}
		
			else if (bestFit < .50d){
					simulationTime = 50;	
				} else if (bestFit < .100){
					simulationTime = 100;
				} else {
					simulationTime = 200;
				}*/
			//for (int lvl=0;lvl < num_lvls;lvl++){
			int lvl = 0;
		//		runReplayTask(prog,data,simulationTime,lvl);
				error += calculateFitness(((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo(),prog);
				runMarioTask(prog,data,simulationTime,lvl);
				distance[lvl]=((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells;
				//System.out.print(error+"-"+((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells+";");
				prog.setAdditionalFitnessInfo(String.format("%s:%s",error,((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells));
				//error += distance[lvl]/MarioData.getEnvironment().getEvaluationInfo().levelLength;
				
				
			//}
			// Determine success of individual in #lvls by averaging over all played levels
			// --------------------------------
			//error = error/num_lvls;
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
				error = 0.0d;
			}
			if (error < 0.000001) {
				error = 0.0d;
			}
			else if (error > 256) {
				// Add penalty 
				// ------------------------------
				error = 256;
				
					
			}
		} catch (IllegalStateException iex) {
			error = 0.0d;
			System.out.println(iex);
		}

        // if we are using delta distance we need to use "<" because we care for smaller errors
        if (error > bestFit ){
            System.out.println("reached a good solution");
            logBest(error, prog);

            bestFit = error;
        }
			

		return error;
	}
	
	protected boolean  runReplayTask(IGPProgram prog,MarioData data,int time,int lvl) {
		Object[] noargs = new Object[0];
		Mario_GPAgent mario = new Mario_GPAgent(prog, noargs, data);
		List<Agent> agentSet = new LinkedList<Agent>();
		
		agentSet.add(mario);
		GPMirrorTask replayTask = new GPMirrorTask(agentSet);
	    replayTask.reset(referenceTraceFiles[lvl]);
	    //GlobalOptions.FPS = m_options.getFPS();
	    
	    return ((GPMirrorTask)replayTask).startReplay(75,false);
	}
	
	protected boolean  runReplayTask(IGPProgram [] progs,int time,int lvl) {
		Object[] noargs = new Object[0];
		List<Agent> agentSet = new LinkedList<Agent>();
		
		for (IGPProgram prog : progs){
			MarioData data = new MarioData();
			Mario_GPAgent mario = new Mario_GPAgent(prog, noargs, data);
			prog.setApplicationData(data);
			agentSet.add(mario);
		}
		
		
		GPMirrorTask replayTask = new GPMirrorTask(agentSet);
	    replayTask.reset(referenceTraceFiles[lvl]);
	    //GlobalOptions.FPS = m_options.getFPS();
	    
	    return ((GPMirrorTask)replayTask).startReplay(100,false);
	}

	
	@Override
	protected double calculateFitness(EvaluationInfo env,IGPProgram prog){
		byte[] currentRaw = ((MarioData)prog.getApplicationData()).getActionTrace();
		
		double weight = CompareTrace(currentRaw, 0, simulationTime);
		//weight =  MarioData.getEnvironment().getEvaluationInfo().distancePassedCells * (1.01d-weight);
		
		return weight;
	}

}

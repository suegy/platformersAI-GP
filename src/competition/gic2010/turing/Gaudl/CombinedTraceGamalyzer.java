package competition.gic2010.turing.Gaudl;



import gamalyzer.data.input.Domains;
import gamalyzer.data.input.Trace;
import gamalyzer.data.input.Traces;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;


import org.jgap.gp.IGPProgram;

import clojure.lang.IPersistentVector;
import clojure.lang.LazySeq;

import competition.gic2010.turing.Gaudl.gp.MarioData;
import org.platformer.agents.Agent;
import org.platformer.benchmark.tasks.Task;
import org.platformer.benchmark.tasks.MirrorTask;
import org.platformer.tools.EvaluationInfo;
import org.platformer.tools.PlatformerAIOptions;


/*
 * This metric is utilizing the Gamalyzer done by Joseph Osborn
 */
public class CombinedTraceGamalyzer extends GameplayMetricFitness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8750632855093986122L;
	
	private byte [][] referenceTraces;
	private Traces referenceVectorTraces;
	String [] referenceTraceFiles;
	private int simulationTime;
	private int slidingWindow = 10;
	private int gamalyzerFramesPerChunk = 12;
	private float distanceScale = 0.1f;
	//private Trace refTrace;

	public CombinedTraceGamalyzer(Task task, PlatformerAIOptions options){
		super(task, options);
		num_lvls = 1;
		//File f = new File("human-ld1-lvl1.act");
		referenceTraceFiles = new String [2];
		//referenceVectorTraces = new Trace();
		referenceTraceFiles[0] = "players-19022014s1-p-test2-lvl-0-time-200-difficulty-0-trial-1";
		referenceTraceFiles[1] = "players-19022014s1-p-test2-lvl-1-time-200-difficulty-0-trial-1";
		bestFit = 0.01;
		referenceTraces = new byte[2][];		
		// reading the tracing at 15chunks per second
		try {
			referenceTraces[0] = Files.readAllBytes(new File(referenceTraceFiles[0] + ".act").toPath());
			referenceTraces[1] =  Files.readAllBytes(new File(referenceTraceFiles[1]+".act").toPath());
			referenceVectorTraces = gamalyzer.read.Mario.readLogs(new File[]{new File(referenceTraceFiles[0]+".act"),new File(referenceTraceFiles[1]+".act")},gamalyzerFramesPerChunk,1);
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
	public static BitSet toBitSet(byte b) {
		int n = 8;
		final BitSet set = new BitSet(n);
		while (n-- > 0) {
			boolean isSet = (b & 0x80) != 0;
			set.set(n, isSet);
			b <<= 1;
		}
		return set;
	}
	private double compareAction(byte curr, byte ref) {
		BitSet reference = toBitSet(ref);
		BitSet current = toBitSet(curr);
		
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
		if (prog.getApplicationData() != null){
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

			double traceError = calculateFitness(data.getEnvironment().getEvaluationInfo(), prog);
			double gamalyzerError = calculateGamalyzerFitness(data.getEnvironment().getEvaluationInfo(), prog);

			data = new MarioData();
			prog.setApplicationData(data);
			runMarioTask(prog, data, simulationTime, lvl);

			distance[lvl] = data.getEnvironment().getEvaluationInfo().distancePassedCells;

			//System.out.print(error+"-"+((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells+";");
			//prog.setAdditionalFitnessInfo(String.format("%s:%s",error,((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells));
			prog.setAdditionalFitnessInfo(String.format("%s:%s", gamalyzerError, distance[lvl]));

			double distanceWeight = (distance[lvl] / data.getEnvironment().getEvaluationInfo().levelLength);
			if (distanceWeight > distanceScale)
				distanceWeight *= distanceScale;

			error = traceError + distanceWeight;
			error /= (1+distanceScale);

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
            //TODO: fix the lvl assignment here as it is later not just level 0
            //runMarioTask(prog,data,simulationTime,0);
			//distance[0]=((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells;
			//double traceerror = calculateFitness(((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo(),prog);
			
			System.out.println("best: "+error+"-"+((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells+";");
			//prog.setAdditionalFitnessInfo(String.format("%s:%s",traceerror,((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells));
			
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
		MirrorTask replayTask = new MirrorTask(agentSet);
	    replayTask.reset(referenceTraceFiles[lvl]);
	    //GlobalOptions.FPS = m_options.getFPS();
	    
	    return replayTask.startReplay(75,false);
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
		
		
		MirrorTask replayTask = new MirrorTask(agentSet);
	    replayTask.reset(referenceTraceFiles[lvl]);
	    //GlobalOptions.FPS = m_options.getFPS();
	    
	    return replayTask.startReplay(24,false);
	}

	
	@Override
	protected double calculateFitness(EvaluationInfo env,IGPProgram prog){
		byte[] currentRaw = ((MarioData)prog.getApplicationData()).getActionTrace();
		
		double weight = CompareTrace(currentRaw, 0, simulationTime);
		//weight =  MarioData.getEnvironment().getEvaluationInfo().distancePassedCells * (1.01d-weight);
		
		return weight;
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
		IPersistentVector t = (IPersistentVector)referenceVectorTraces.traces;
		Trace refTrace = (Trace)t.entryAt(ref_num).getValue();
		// System.out.println(gamalyzer.cmp.tt.stringify(current));
		// System.out.println("vs");
		// System.out.println(gamalyzer.cmp.tt.stringify(refTrace));
		// System.out.println("---");
		int length = getTraceLength(refTrace);
		String test = gamalyzer.data.util.stringify(refTrace);
		int currLength = getTraceLength(current);
		refTrace = gamalyzer.data.util.trim(refTrace,simulationTime);
		int length2 = getTraceLength(refTrace);
		if (length < currLength)
			current = gamalyzer.data.util.trim(current,getTraceLength(refTrace));
		
		double dissimilarity = 1.0f;
		try {
		dissimilarity = gamalyzer.cmp.tt.distance(current,refTrace,domains,slidingWindow);
		} 
		catch (clojure.lang.ArityException e) {
			System.out.println(e);
			dissimilarity = 1.0f;
		}
		
		return (float)dissimilarity;
	}
	
	protected double calculateGamalyzerFitness(EvaluationInfo env, IGPProgram prog){
		Traces currentRaw = gamalyzer.read.Mario.readActions(referenceVectorTraces, ((MarioData)prog.getApplicationData()).getActionTrace(),gamalyzerFramesPerChunk,1);
		IPersistentVector t = (IPersistentVector)currentRaw.traces;
		Trace current = (Trace) t.entryAt(0).getValue();
		double weight = CompareTrace(current, 0, (Domains)currentRaw.domains,simulationTime);
		System.out.print(weight+"-"+((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells+";");
		//prog.setAdditionalFitnessInfo(String.format("%s:%s",weight,((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells));
		
		return (1.01d-weight);
	}


}

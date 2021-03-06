package competition.venue.year.type.Gaudl;

import gamalyzer.data.input.Domains;
import gamalyzer.data.input.Trace;
import gamalyzer.data.input.Traces;

import java.io.File;

import org.jgap.gp.IGPProgram;
import clojure.lang.IPersistentVector;
import clojure.lang.LazySeq;

import competition.venue.year.type.Gaudl.gp.MarioData;
import org.platformer.benchmark.tasks.BasicTask;
import org.platformer.tools.EvaluationInfo;
import org.platformer.tools.PlatformerAIOptions;


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
	private int gamalyzerFramesPerChunk = 12;
	private int slidingWindow = 10;
	//private Trace refTrace;

	public GamalyzerFitness(BasicTask task, PlatformerAIOptions options){
		super(task, options);
		num_lvls = 1;
		//File f = new File("human-ld1-lvl1.act");
		File [] hTraces = new File[1];
		hTraces[0] = new File("dataset"+File.separator+"players-19022014s1-p-test2-lvl-0-time-200-difficulty-0.act");
		//hTraces[0] = new File("dataset"+File.separator+"players-19022014s1-p-test2-lvl-1-time-200-difficulty-0-trial-1.act");
		bestFit = 0.01;
				
		// reading the tracing at 15chunks per second
		referenceTraces = gamalyzer.read.Mario.readLogs(hTraces,gamalyzerFramesPerChunk,1);
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

	@Override
	protected double runFitness(IGPProgram prog) {
		double error = 0.0f;
		distance = new int[num_lvls];
		mariologFiles = new String[num_lvls];
		MarioData data = new MarioData();
		// Initialize local stores.
		// ------------------------
		//prog.getGPConfiguration().clearStack();
		//prog.getGPConfiguration().clearMemory();
		prog.setApplicationData(data);
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
			int lvl = 1;
				runMarioTask(prog,data,simulationTime,lvl);
				distance[lvl]=((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells;
				error += calculateFitness(((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo(),prog);
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

	
	@Override
	protected double calculateFitness(EvaluationInfo env, IGPProgram prog){
		Traces currentRaw = gamalyzer.read.Mario.readActions(referenceTraces, ((MarioData)prog.getApplicationData()).getActionTrace(),gamalyzerFramesPerChunk,1);
		IPersistentVector t = (IPersistentVector)currentRaw.traces;
		Trace current = (Trace) t.entryAt(0).getValue();
		double weight = CompareTrace(current, 0, (Domains)currentRaw.domains,simulationTime);
		System.out.print(weight+"-"+((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells+";");
		prog.setAdditionalFitnessInfo(String.format("%s:%s",weight,((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells));
		weight =  ((MarioData)prog.getApplicationData()).getEnvironment().getEvaluationInfo().distancePassedCells * (1.01d-weight);
		//weight =(1.01d-weight);
		return weight;
	}

}

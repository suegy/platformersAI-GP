package competition.gic2010.turing.Gaudl;

import gamalyzer.data.input.Domains;
import gamalyzer.data.input.Trace;
import gamalyzer.data.input.Traces;
import gamalyzer.data.util;

import java.io.File;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;
import clojure.lang.IPersistentVector;

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
	//private Trace refTrace;

	public GamalyzerFitness(BasicTask task,MarioAIOptions options){
		super(task, options);
		num_lvls = 1;
		File f = new File("human-ld1-lvl1.act");
		bestFit = 1.0;
				
		referenceTraces = gamalyzer.read.Mario.readLogs(new File[] {f,});
	}
	
	/*
	 * compared current trace of an agent to the one specified by the ref_num which 
	 * is read during instantiation 
	 */
	private float CompareTrace(Trace current,int ref_num,Domains domains){
		IPersistentVector t = (IPersistentVector)referenceTraces.traces;
		Trace refTrace = (Trace)t.entryAt(ref_num).getValue();
        if(((IPersistentVector)refTrace.inputs).length() > ((IPersistentVector)current.inputs).length()) {
            refTrace = util.trim(refTrace, ((IPersistentVector)current.inputs).length());
        }
		// System.out.println(gamalyzer.cmp.tt.stringify(current));
		// System.out.println("vs");
		// System.out.println(gamalyzer.cmp.tt.stringify(refTrace));
		// System.out.println("---");
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

	
	protected double calculateFitness(EvaluationInfo env){
		Traces currentRaw = gamalyzer.read.Mario.readActions(referenceTraces, MarioData.getActionTrace());
		IPersistentVector t = (IPersistentVector)currentRaw.traces;
		Trace current = (Trace) t.entryAt(0).getValue();
		double weight = CompareTrace(current, 0, (Domains)currentRaw.domains);
		System.out.print(MarioData.getEnvironment().getEvaluationInfo().distancePassedCells+";");
		return MarioData.getEnvironment().getEvaluationInfo().distancePassedCells * (1.01d-weight);
	}

}

package competition.gic2010.turing.Gaudl;

import gamalyzer.data.input.Domains;
import gamalyzer.data.input.Trace;
import gamalyzer.data.input.Traces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
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
	

	public GamalyzerFitness(BasicTask task,MarioAIOptions options){
		super(task, options);
		num_lvls = 1;
		File f = new File("human-ld1-lvl1.act");
				
		referenceTraces = gamalyzer.read.Mario.readLogs(new File[] {f,});

	}
	
	/*
	 * compared current trace of an agent to the one specified by the ref_num which 
	 * is read during instantiation 
	 */
	private float CompareTrace(Trace current,int ref_num){
		IPersistentVector t = (IPersistentVector)referenceTraces.traces;
		Trace refTrace = (Trace) t.entryAt(ref_num).getValue();
		
		double dissimilarity = gamalyzer.cmp.tt.diss(current,refTrace,(Domains)referenceTraces.domains);
		System.out.println(" Test:"+dissimilarity);

		
		return (float)dissimilarity;
	}

	
	protected float calculateFitness(EvaluationInfo env){
		Traces currentRaw = gamalyzer.read.Mario.readActions(referenceTraces, MarioData.getActionTrace());
		IPersistentVector t = (IPersistentVector)currentRaw.traces;
		Trace current = (Trace) t.entryAt(0).getValue();
		
		return CompareTrace(current, 0);
	}

}

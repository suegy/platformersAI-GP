/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package competition.gic2010.turing.Gaudl;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.ADF;
import org.jgap.gp.function.And;
import org.jgap.gp.function.Equals;
import org.jgap.gp.function.GreaterThan;
import org.jgap.gp.function.IfElse;
import org.jgap.gp.function.Not;
import org.jgap.gp.function.Or;
import org.jgap.gp.function.SubProgram;
import org.jgap.gp.impl.BranchTypingCross;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.TournamentSelector;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;
import org.jgap.impl.FittestPopulationMerger;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import competition.gic2010.turing.Gaudl.Genes.Down;
import competition.gic2010.turing.Gaudl.Genes.GpADF;
import competition.gic2010.turing.Gaudl.Genes.GpGreaterThan;
import competition.gic2010.turing.Gaudl.Genes.GpTrue;
import competition.gic2010.turing.Gaudl.Genes.IsAirAt;
import competition.gic2010.turing.Gaudl.Genes.IsBreakableAt;
import competition.gic2010.turing.Gaudl.Genes.IsCoinAt;
import competition.gic2010.turing.Gaudl.Genes.IsEnemyAt;
import competition.gic2010.turing.Gaudl.Genes.IsFireFlowerAt;
import competition.gic2010.turing.Gaudl.Genes.IsMushroomAt;
import competition.gic2010.turing.Gaudl.Genes.IsPrincessAt;
import competition.gic2010.turing.Gaudl.Genes.IsTall;
import competition.gic2010.turing.Gaudl.Genes.IsWalkableAt;
import competition.gic2010.turing.Gaudl.Genes.Jump;
import competition.gic2010.turing.Gaudl.Genes.JumpLeft;
import competition.gic2010.turing.Gaudl.Genes.JumpRight;
import competition.gic2010.turing.Gaudl.Genes.LastActionWas;
import competition.gic2010.turing.Gaudl.Genes.Left;
import competition.gic2010.turing.Gaudl.Genes.LongJump;
import competition.gic2010.turing.Gaudl.Genes.LongJumpLeft;
import competition.gic2010.turing.Gaudl.Genes.LongJumpRight;
import competition.gic2010.turing.Gaudl.Genes.ObjectAtXY;
import competition.gic2010.turing.Gaudl.Genes.Right;
import competition.gic2010.turing.Gaudl.Genes.Run;
import competition.gic2010.turing.Gaudl.Genes.Shoot;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */
public final class GPSystemStandAlone extends GPProblem
{
protected static Variable vx;
private GPGenotype Geno;
public Thread gpThread;

public GPSystemStandAlone(GPFitnessFunction metric) {
	GPConfiguration config;
	//Thread gpThread = null;
	try {
        config = new GPConfiguration();
        //config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
        config.setProgramCreationMaxTries(-1);
        //config.setStrictProgramCreation(true);
        config.setMaxInitDepth(30);
        config.setPopulationSize(500);
        //Taken from anttrail. WORTH INVESTIGATING.
        config.setCrossoverProb(0.8f);//orig: 0.9f
        config.setReproductionProb(0.2f); //orig: 0.1f
        config.setNewChromsPercent(0.0f); //orig: 0.3f
        config.setMutationProb(0.33f);
        //config.setUseProgramCache(true);
        //config.setCrossoverMethod(new BranchTypingCross(config));
        config.setPreservFittestIndividual(true);
        config.setFitnessFunction(metric);
        setGPConfiguration(config);
        Geno = create();
        
        Geno.setVerboseOutput(true);
        gpThread = new Thread(Geno);
    }
    catch (InvalidConfigurationException e){
        System.err.println("wrong config: \n"+e);
    }
	if (gpThread instanceof Thread)
		gpThread.start();
}

@SuppressWarnings("rawtypes")
@Override
public GPGenotype create() throws InvalidConfigurationException {
	Class [] types = { CommandGene.VoidClass};
	GPConfiguration conf  = getGPConfiguration();
	Class [][] argTypes = {{},
			//{CommandGene.VoidClass,CommandGene.VoidClass,CommandGene.VoidClass}
			};
	CommandGene [][] nodes = {
			{
				//vx = Variable.create(conf,"X", CommandGene.IntegerClass),
				new Terminal(conf, CommandGene.IntegerClass,-4,4,true),
				//new Terminal(conf, CommandGene.IntegerClass,-4,4,true),
				//new Terminal(conf, CommandGene.IntegerClass,-4,4,true),
				new SubProgram(conf,new Class[] {CommandGene.VoidClass,CommandGene.VoidClass}),
				//new SubProgram(conf),
				//new Add(conf, CommandGene.IntegerClass),
				//new GpGreaterThan(conf, CommandGene.IntegerClass),
				//new Equals(conf, CommandGene.IntegerClass),
				//new LastActionWas(conf),
				new IfElse(conf, CommandGene.BooleanClass),
				//new Equals(conf, CommandGene.BooleanClass),
				new Equals(conf, CommandGene.IntegerClass),
				//new Or(conf),
				new And(conf),
				new Not(conf),
				//new CanJump(conf),
				//new CanShoot(conf),
				new GpTrue(conf),
				new ObjectAtXY(conf),
				//new False(conf),
				//new IsBreakableAt(conf),
				//new IsCoinAt(conf),
				//new IsAirAt(conf),
				//new IsEnemyAt(conf),
				//new IsFireFlowerAt(conf),
				//new IsMushroomAt(conf),
				//new IsPrincessAt(conf),
				new IsTall(conf),
				//new IsWalkableAt(conf),
				//new SubProgram(conf,new Class[] {CommandGene.VoidClass,CommandGene.VoidClass,CommandGene.VoidClass}),
				new Down(conf),
				new Left(conf),
				new Right(conf),
				new Shoot(conf),
				new Jump(conf),
				new JumpLeft(conf),
				new JumpRight(conf),
				new LongJump(conf),
				new LongJumpLeft(conf),
				new LongJumpRight(conf),
				new Run(conf),
				//new GpADF(conf,1,3),
			},
			//{
				//new SubProgram(conf,new Class[] {CommandGene.VoidClass,CommandGene.VoidClass,CommandGene.VoidClass}),
			//}
	};
	
	return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodes,
			250, true);
}

public static void main(String[] args) throws InterruptedException
{
//        final String argsString = "-vis on";
    final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
    final BasicTask basicTask = new BasicTask(marioAIOptions);
    GameplayMetricFitness metric = new GameplayMetricFitness(basicTask,marioAIOptions);
    GamalyzerFitness metric2 = new GamalyzerFitness(basicTask,marioAIOptions);
    GPSystemStandAlone marioGP = new GPSystemStandAlone(metric2);
   
    
    while (marioGP.gpThread.isAlive()) {
    	Thread.sleep(10);
    }
    System.exit(0);
}

}

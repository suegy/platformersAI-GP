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
import org.jgap.gp.GPProblem;
import org.jgap.gp.function.And;
import org.jgap.gp.function.Equals;
import org.jgap.gp.function.GreaterThan;
import org.jgap.gp.function.IfElse;
import org.jgap.gp.function.Not;
import org.jgap.gp.function.Or;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import competition.gic2010.turing.Gaudl.Genes.Down;
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
import competition.gic2010.turing.Gaudl.Genes.LastActionWas;
import competition.gic2010.turing.Gaudl.Genes.Left;
import competition.gic2010.turing.Gaudl.Genes.LongJump;
import competition.gic2010.turing.Gaudl.Genes.Right;
import competition.gic2010.turing.Gaudl.Genes.Shoot;
import competition.gic2010.turing.Gaudl.Genes.SubProgram;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */
public final class GPSystemStandAlone extends GPProblem
{
protected static Variable vx;
private GPGenotype Geno;
public Thread gpThread;

public GPSystemStandAlone(GameplayMetricFitness metric) {
	GPConfiguration config;
	//Thread gpThread = null;
	try {
        config = new GPConfiguration();
        //config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
        //config.setMaxInitDepth(30);
        config.setPopulationSize(50);
        //config.setMinInitDepth(3);
        config.setCrossoverProb(0.6f);
        config.setReproductionProb(0.3f);
        config.setNewChromsPercent(0.2f);
        config.setMutationProb(0.1f);
        config.setPreservFittestIndividual(true);
        config.setFitnessFunction(metric);
        
        setGPConfiguration(config);
        Geno = create();

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
	Class [][] argTypes = {{},};
	CommandGene [][] nodes = {
			{
			vx = Variable.create(conf,"X",CommandGene.FloatClass),
			//new SubProgram(conf, new Class[] {CommandGene.VoidClass,
			//		CommandGene.VoidClass, CommandGene.VoidClass}, true),
			//new SubProgram(conf),
			//new Add(conf, CommandGene.IntegerClass),
			new IfElse(conf, CommandGene.BooleanClass),
			new IfElse(conf, CommandGene.VoidClass),
			new GpGreaterThan(conf, CommandGene.IntegerClass),
			new Or(conf),
			new And(conf),
			new Not(conf),
			new Equals(conf, CommandGene.IntegerClass),
			new Equals(conf, CommandGene.BooleanClass),
			new GpTrue(conf),
			//new False(conf),
			new Terminal(conf, CommandGene.IntegerClass,-4,4,true),
			//new CanJump(conf),
			//new CanShoot(conf),
			new Down(conf),
			new Left(conf),
			new Right(conf),
			new Shoot(conf),
			new Jump(conf),
			new LongJump(conf),
			new IsBreakableAt(conf),
			//new IsCoinAt(conf),
			new IsAirAt(conf),
			new IsEnemyAt(conf),
			//new IsFireFlowerAt(conf),
			//new IsMushroomAt(conf),
			//new IsPrincessAt(conf),
			new IsTall(conf),
			new IsWalkableAt(conf),
			new LastActionWas(conf),
			}
	};
	
	return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodes,
			150, true);
}

public static void main(String[] args) throws InterruptedException
{
//        final String argsString = "-vis on";
    final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
    final BasicTask basicTask = new BasicTask(marioAIOptions);
    GameplayMetricFitness metric = new GameplayMetricFitness(basicTask,marioAIOptions);
    GPSystemStandAlone marioGP = new GPSystemStandAlone(metric);
    
//        final Environment environment = new MarioEnvironment();
//        final Agent agent = new ForwardAgent();
//        final Agent agent = marioAIOptions.getAgent();
//        final Agent a = AgentsPool.loadAgent("ch.idsia.controllers.agents.controllers.ForwardJumpingAgent");
//    final BasicTask basicTask = new BasicTask(marioAIOptions);
//        for (int i = 0; i < 10; ++i)
//        {
//            int seed = 0;
//            do
//            {
//                marioAIOptions.setLevelDifficulty(i);
//                marioAIOptions.setLevelRandSeed(seed++);
//    basicTask.setOptionsAndReset(marioAIOptions);
//    basicTask.runSingleEpisode(1);
//    basicTask.doEpisodes(1,true,1);
//    System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
//            } while (basicTask.getEnvironment().getEvaluationInfo().marioStatus != Environment.MARIO_STATUS_WIN);
//        }
//
    
    while (marioGP.gpThread.isAlive()) {
    	Thread.sleep(10);
    }
    System.exit(0);
}

}

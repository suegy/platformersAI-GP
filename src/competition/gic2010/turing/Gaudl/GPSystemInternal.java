package competition.gic2010.turing.Gaudl;

import org.jgap.Genotype;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.And;
import org.jgap.gp.function.Equals;
import org.jgap.gp.function.GreaterThan;
import org.jgap.gp.function.IfElse;
import org.jgap.gp.function.Not;
import org.jgap.gp.function.Or;
import org.jgap.gp.function.SubProgram;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;


import org.jgap.gp.terminal.False;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.True;
import org.jgap.gp.terminal.Variable;

import competition.gic2010.turing.Gaudl.Genes.CanJump;
import competition.gic2010.turing.Gaudl.Genes.CanShoot;
import competition.gic2010.turing.Gaudl.Genes.Down;
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
import competition.gic2010.turing.Gaudl.Genes.Left;
import competition.gic2010.turing.Gaudl.Genes.MarioData;
import competition.gic2010.turing.Gaudl.Genes.Right;
import competition.gic2010.turing.Gaudl.Genes.Shoot;
import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import ch.idsia.agents.Agent;
import ch.idsia.agents.LearningAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.GamePlayTask;
import ch.idsia.benchmark.tasks.LearningTask;

public class GPSystemInternal extends GPProblem implements LearningAgent {

	protected static Variable vx;
	private GPGenotype Geno;
	
	@SuppressWarnings("rawtypes")
	@Override
	public GPGenotype create() throws InvalidConfigurationException {
		Class [] types = {CommandGene.FloatClass, CommandGene.BooleanClass};
		GPConfiguration conf  = getGPConfiguration();
		Class [][] argTypes = {{}};
		CommandGene [][] nodes = {
				{
				vx = Variable.create(conf,"X",CommandGene.FloatClass),
				new SubProgram(conf, 2, CommandGene.IntegerClass),
				new SubProgram(conf, 2, CommandGene.VoidClass),
				new Add(conf, CommandGene.IntegerClass),
				new IfElse(conf, CommandGene.BooleanClass),
				new GreaterThan(conf, CommandGene.IntegerClass),
				new Or(conf),
				//new And(conf),
				//new Not(conf),
				new Equals(conf, CommandGene.IntegerClass),
				new Equals(conf, CommandGene.BooleanClass),
				new True(conf),
				new False(conf),
				new Terminal(conf, CommandGene.IntegerClass,-10,10,true),
				new CanJump(conf),
				new CanShoot(conf),
				new Down(conf),
				new Left(conf),
				new Right(conf),
				new Shoot(conf),
				new Jump(conf),
				new IsBreakableAt(conf),
				new IsCoinAt(conf),
				new IsAirAt(conf),
				new IsEnemyAt(conf),
				new IsFireFlowerAt(conf),
				new IsMushroomAt(conf),
				//new IsPrincessAt(conf),
				new IsTall(conf),
				new IsWalkableAt(conf),
				}
		};
		
		return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodes,
				100, true);
	}
	
	@Override
	public boolean[] getAction() {
		return ((MarioData)this.Geno.getFittestProgram().getApplicationData()).getActions();
	}

	@Override
	public void integrateObservation(Environment environment) {
		MarioData.setEnvironment(environment);

	}

	@Override
	public void giveIntermediateReward(float intermediateReward) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		MarioData.setEnvironment(null);
	}

	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow,
			int egoCol) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "GaudlOsborn-GP";
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void learn() {
		// TODO Auto-generated method stub
	}

	@Override
	public void giveReward(float reward) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newEpisode() {
		MarioData.setEnvironment(null);
	}

	@Override
	public void setLearningTask(LearningTask learningTask) {}

	@Override
	public void setEvaluationQuota(long num) {}

	@Override
	public Agent getBestAgent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(){
		GPConfiguration config;
		Thread gpThread = null;
		try {
            config = new GPConfiguration();
            config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
            config.setMaxInitDepth(6);
            config.setPopulationSize(100);
            //TODO:: This does not work and needs some fixing after the rest is tested
            config.setFitnessFunction(new GameplayMetricFitness(new BasicTask(null),null));
            
            
            setGPConfiguration(config);
            Geno = create();

            gpThread = new Thread(Geno);
        }
        catch (InvalidConfigurationException e){
            System.err.println("wrong config: /n"+e);
        }
		if (gpThread instanceof Thread)
			gpThread.start();

	}
	
}

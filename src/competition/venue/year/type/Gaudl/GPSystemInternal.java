package competition.venue.year.type.Gaudl;

import competition.venue.year.type.Gaudl.gp.MarioData;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Equals;
import org.jgap.gp.function.GreaterThan;
import org.jgap.gp.function.IfElse;
import org.jgap.gp.function.Or;
import org.jgap.gp.function.SubProgram;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;


import org.jgap.gp.terminal.False;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.True;
import org.jgap.gp.terminal.Variable;

import competition.venue.year.type.Gaudl.Genes.CanJump;
import competition.venue.year.type.Gaudl.Genes.CanShoot;
import competition.venue.year.type.Gaudl.Genes.Down;
import competition.venue.year.type.Gaudl.Genes.IsAirAt;
import competition.venue.year.type.Gaudl.Genes.IsBreakableAt;
import competition.venue.year.type.Gaudl.Genes.IsCoinAt;
import competition.venue.year.type.Gaudl.Genes.IsEnemyAt;
import competition.venue.year.type.Gaudl.Genes.IsFireFlowerAt;
import competition.venue.year.type.Gaudl.Genes.IsMushroomAt;
import competition.venue.year.type.Gaudl.Genes.IsTall;
import competition.venue.year.type.Gaudl.Genes.IsWalkableAt;
import competition.venue.year.type.Gaudl.Genes.Jump;
import competition.venue.year.type.Gaudl.Genes.Left;
import competition.venue.year.type.Gaudl.Genes.Right;
import competition.venue.year.type.Gaudl.Genes.Shoot;
import org.platformer.agents.Agent;
import org.platformer.agents.LearningAgent;
import org.platformer.benchmark.platform.environments.Environment;
import org.platformer.benchmark.tasks.BasicTask;
import org.platformer.benchmark.tasks.LearningTask;


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
		for(IGPProgram prog : this.Geno.getGPPopulation().getGPPrograms()){
			((MarioData)prog.getApplicationData()).setEnvironment(environment);
		}
	}

	@Override
	public void giveIntermediateReward(float intermediateReward) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		for(IGPProgram prog : this.Geno.getGPPopulation().getGPPrograms()){
			((MarioData)prog.getApplicationData()).setEnvironment(null);
		}
		
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
		for(IGPProgram prog : this.Geno.getGPPopulation().getGPPrograms()){
			((MarioData)prog.getApplicationData()).setEnvironment(null);
		}
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

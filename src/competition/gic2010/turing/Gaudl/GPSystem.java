package competition.gic2010.turing.Gaudl;

import org.jgap.Genotype;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Equals;
import org.jgap.gp.function.GreaterThan;
import org.jgap.gp.function.IfElse;
import org.jgap.gp.function.Or;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;


import org.jgap.gp.terminal.False;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.True;
import org.jgap.gp.terminal.Variable;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import ch.idsia.agents.Agent;
import ch.idsia.agents.LearningAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.LearningTask;

public class GPSystem extends GPProblem implements LearningAgent {

	protected static Variable vx;
	
	@SuppressWarnings("rawtypes")
	@Override
	public GPGenotype create() throws InvalidConfigurationException {
		Class [] types = {CommandGene.FloatClass, CommandGene.BooleanClass};
		GPConfiguration conf  = getGPConfiguration();
		Class [][] argTypes = {{}};
		CommandGene [][] nodes = {
				{
				vx = Variable.create(conf,"X",CommandGene.FloatClass),
				new Add(conf, CommandGene.IntegerClass),
				new IfElse(conf, CommandGene.BooleanClass),
				new GreaterThan(conf, CommandGene.IntegerClass),
				new Or(conf),
				new Equals(conf, CommandGene.IntegerClass),
				new True(conf),
				new False(conf),
				new Terminal(conf, CommandGene.IntegerClass,-10,10,true),
				}
		};
		
		return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodes,
				100, true);
	}
	
	@Override
	public boolean[] getAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void integrateObservation(Environment environment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void giveIntermediateReward(float intermediateReward) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void setLearningTask(LearningTask learningTask) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEvaluationQuota(long num) {
		// TODO Auto-generated method stub

	}

	@Override
	public Agent getBestAgent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(){
		GPConfiguration config;
		try {
            config = new GPConfiguration();
            config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
            config.setMaxInitDepth(6);
            config.setPopulationSize(100);
            //config.setFitnessFunction(new JoesMetric());
            
            
            setGPConfiguration(config);
            GPGenotype geno = create();
        }
        catch (InvalidConfigurationException e){
            System.err.println("wrong config: /n"+e);
        }

	}
	

}

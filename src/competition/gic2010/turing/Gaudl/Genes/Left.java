package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import competition.gic2010.turing.Gaudl.gp.MarioCommand;
import competition.gic2010.turing.Gaudl.gp.MarioData;
import ch.idsia.benchmark.mario.engine.sprites.Mario;


public class Left extends MarioCommand implements IMutateable{




	/**
	 * 
	 */
	private static final long serialVersionUID = 7016345793588283803L;

	public Left(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "left";
	}
	
	public String getName() {
		return "left";
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.moveLeft();
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.moveRight();
		return true;
	}
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {

//		if (a_percentage < 0.05d)
//		    return new LongJumpRight(getGPConfiguration());
//		if (a_percentage < 0.10d)
//		    return new JumpRight(getGPConfiguration());
//		if (a_percentage < 0.15d)
//		    return new LongJumpLeft(getGPConfiguration());
//		if (a_percentage < 0.20d)
//		    return new JumpLeft(getGPConfiguration());
		
		if (a_percentage < 0.14d)
		    return new Shoot(getGPConfiguration());
		if (a_percentage < 0.28d)
		    return new LongJump(getGPConfiguration());
		if (a_percentage < 0.42d)
		    return new Run(getGPConfiguration());
		if (a_percentage < 0.56d)
		    return new Wait(getGPConfiguration());
		if (a_percentage < 0.7d)
		    return new Jump(getGPConfiguration());
		if (a_percentage < 0.84d)
		    return new Down(getGPConfiguration());
		
		return new Right(getGPConfiguration());
	}

}

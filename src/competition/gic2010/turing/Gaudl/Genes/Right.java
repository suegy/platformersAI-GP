package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import competition.gic2010.turing.Gaudl.gp.MarioCommand;
import competition.gic2010.turing.Gaudl.gp.MarioData;
import ch.idsia.benchmark.mario.engine.sprites.Mario;

public class Right extends MarioCommand implements IMutateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5900540399088398229L;

	public Right(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "right";
	}
	
	public String getName() {
		return "right";
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.moveRight();
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.moveRight();
		
		return true;
	}
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {

		if (a_percentage < 0.05d)
		    return new LongJumpRight(getGPConfiguration());
		if (a_percentage < 0.10d)
		    return new JumpRight(getGPConfiguration());
		if (a_percentage < 0.15d)
		    return new LongJumpLeft(getGPConfiguration());
		if (a_percentage < 0.20d)
		    return new JumpLeft(getGPConfiguration());
		if (a_percentage < 0.33d)
		    return new Shoot(getGPConfiguration());
		if (a_percentage < 0.46d)
		    return new Run(getGPConfiguration());
		if (a_percentage < 0.59d)
		    return new Wait(getGPConfiguration());
		if (a_percentage < 0.72d)
		    return new Jump(getGPConfiguration());
		if (a_percentage < 0.85d)
		    return new Down(getGPConfiguration());
		
		return new Left(getGPConfiguration());
	}

}

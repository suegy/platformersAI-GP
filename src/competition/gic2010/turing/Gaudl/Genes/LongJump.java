package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

public class LongJump extends MarioCommand implements IMutateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5766607684924069508L;

	public LongJump(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,0,CommandGene.VoidClass);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "longJump";
	}
	
	public String getName() {
		return "longJump";
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.longJump();
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);

		return data.longJump();
	}
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {

		if (a_percentage < 0.20d)
		    return new Right(getGPConfiguration());
		if (a_percentage < 0.30d)
		    return new Left(getGPConfiguration());
		if (a_percentage < 0.40d)
		    return new Jump(getGPConfiguration());
		if (a_percentage < 0.50d)
		    return new LongJumpLeft(getGPConfiguration());
		
		return new LongJumpRight(getGPConfiguration());
	}

}

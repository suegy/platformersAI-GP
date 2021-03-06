package competition.venue.year.type.Gaudl.Genes;

import competition.venue.year.type.Gaudl.gp.MarioCommand;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;

import competition.venue.year.type.Gaudl.gp.MarioData;

public class LongJumpRight extends MarioCommand implements IMutateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5766607684924069508L;

	public LongJumpRight(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,0,CommandGene.VoidClass);
		// TODO Auto-generated constructor stub
	}

	public LongJumpRight() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "longJumpRight";
	}
	
	public String getName() {
		return "longJumpRight";
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.longJumpRight();
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);

		if (data.canJump()){
			data.longJumpLeft();
			return true;
		}
		return false;
	}
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {

		if (a_percentage < 0.125d)
		    return new JumpRight(getGPConfiguration());
		if (a_percentage < 0.25d)
		    return new JumpLeft(getGPConfiguration());
		if (a_percentage < 0.375d)
		    return new LongJumpLeft(getGPConfiguration());
		if (a_percentage < 0.50d)
		    return new Jump(getGPConfiguration());
		
		if (a_percentage < 0.58d)
		    return new Shoot(getGPConfiguration());
		if (a_percentage < 0.66d)
		    return new Left(getGPConfiguration());
		if (a_percentage < 0.74d)
		    return new Wait(getGPConfiguration());
		if (a_percentage < 0.82d)
		    return new Run(getGPConfiguration());
		if (a_percentage < 0.90d)
		    return new Down(getGPConfiguration());
		
		return new Right(getGPConfiguration());
	}

}

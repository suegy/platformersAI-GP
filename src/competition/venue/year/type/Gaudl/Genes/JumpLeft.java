package competition.venue.year.type.Gaudl.Genes;

import competition.venue.year.type.Gaudl.gp.MarioCommand;
import competition.venue.year.type.Gaudl.gp.MarioData;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;


public class JumpLeft extends MarioCommand implements IMutateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5766607684924069508L;

	public JumpLeft(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,0,CommandGene.VoidClass);
		// TODO Auto-generated constructor stub
	}

	public JumpLeft() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "jumpleft";
	}
	
	public String getName() {
		return "jumpleft";
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.jumpLeft();
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);

		if (data.canJump()) {
			data.jumpLeft();
			return true;
		}
		
		return false;
	}
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {

		if (a_percentage < 0.125d)
		    return new LongJumpRight(getGPConfiguration());
		if (a_percentage < 0.25d)
		    return new JumpRight(getGPConfiguration());
		if (a_percentage < 0.375d)
		    return new LongJumpLeft(getGPConfiguration());
		if (a_percentage < 0.50d)
		    return new Jump(getGPConfiguration());
		
		if (a_percentage < 0.58d)
		    return new Shoot(getGPConfiguration());
		if (a_percentage < 0.66d)
		    return new Right(getGPConfiguration());
		if (a_percentage < 0.74d)
		    return new Wait(getGPConfiguration());
		if (a_percentage < 0.82d)
		    return new Run(getGPConfiguration());
		if (a_percentage < 0.90d)
		    return new Down(getGPConfiguration());
		
		return new Left(getGPConfiguration());
	}

}

package competition.venue.year.type.Gaudl.Genes;

import competition.venue.year.type.Gaudl.gp.MarioCommand;
import competition.venue.year.type.Gaudl.gp.MarioData;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;

public class Wait extends MarioCommand implements IMutateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2705904061922960600L;


	public Wait(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf);
		// TODO Auto-generated constructor stub
	}

	public Wait() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "wait";
	}
	
	public String getName() {
		return "wait";
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.pause();
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.pause();
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
			return new Left(getGPConfiguration());
		if (a_percentage < 0.42d)
		    return new Run(getGPConfiguration());
		if (a_percentage < 0.56d)
		    return new Down(getGPConfiguration());
		if (a_percentage < 0.7d)
		    return new Jump(getGPConfiguration());
		if (a_percentage < 0.84d)
		    return new LongJump(getGPConfiguration());
		
		return new Right(getGPConfiguration());
	}

}

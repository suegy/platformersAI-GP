package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;

import competition.gic2010.turing.Gaudl.gp.MarioCommand;
import competition.gic2010.turing.Gaudl.gp.MarioData;

public class Shoot extends MarioCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8002984028415270780L;

	public Shoot(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,0,CommandGene.VoidClass);
		// TODO Auto-generated constructor stub
	}

	public Shoot() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "shoot";
	}
	
	public String getName() {
		return "shoot";
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.shoot();
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		
		if (data.canShoot()){
			data.shoot();
			return true;
		}
		
		return false;
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
		    return new Wait(getGPConfiguration());
		if (a_percentage < 0.28d)
		    return new Run(getGPConfiguration());
		if (a_percentage < 0.34d)
		    return new Down(getGPConfiguration());
		if (a_percentage < 0.46d)
		    return new JumpLeft(getGPConfiguration());
		if (a_percentage < 0.7d)
		    return new Jump(getGPConfiguration());
		if (a_percentage < 0.84d)
		    return new Left(getGPConfiguration());
		
		return new Right(getGPConfiguration());
	}

}

package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

public class Down extends MarioCommand implements IMutateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2705904061922960600L;


	public Down(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "down";
	}
	
	public String getName() {
		return "down";
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.down();
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.down();
		return true;
	}
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {

		if (a_percentage < 0.20d)
		    return new Shoot(getGPConfiguration());
		if (a_percentage < 0.25d)
		    return new Left(getGPConfiguration());
		if (a_percentage < 0.30d)
		    return new Right(getGPConfiguration());
		if (a_percentage < 0.35d)
		    return new LongJump(getGPConfiguration());
		if (a_percentage < 0.40d)
		    return new Jump(getGPConfiguration());
		
		return new Run(getGPConfiguration());
	}

}

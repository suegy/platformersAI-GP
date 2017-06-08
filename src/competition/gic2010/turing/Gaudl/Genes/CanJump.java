package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;

import competition.gic2010.turing.Gaudl.gp.MarioCommand;
import competition.gic2010.turing.Gaudl.gp.MarioData;

public class CanJump extends MarioCommand implements IMutateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5225349215374824634L;
	
	public CanJump(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,0,CommandGene.BooleanClass);
		
	}

	public CanJump() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "canJump";
	}
	
	public String getName() {
		return "canJump";
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		
		return data.canJump();
	}
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {
		if (a_percentage < 0.50d)
			return new IsTall(getGPConfiguration());


		return new CanShoot(getGPConfiguration());
	}

}

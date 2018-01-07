package competition.venue.year.type.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;

import competition.venue.year.type.Gaudl.gp.MarioCommand;
import competition.venue.year.type.Gaudl.gp.MarioData;


public class CanShoot extends MarioCommand implements IMutateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3004018066050264465L;

	public CanShoot(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,0,CommandGene.BooleanClass);
		
	}
	public CanShoot() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "canShoot";
	}
	
	public String getName() {
		return "canShoot";
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		
		return data.canShoot();
	}
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {
		if (a_percentage < 0.50d)
			return new IsTall(getGPConfiguration());

		return new CanJump(getGPConfiguration());
		
	}

}

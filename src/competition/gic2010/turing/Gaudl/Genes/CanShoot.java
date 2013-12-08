package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;


public class CanShoot extends MarioCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3004018066050264465L;

	public CanShoot(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,0,CommandGene.BooleanClass);
		
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

}

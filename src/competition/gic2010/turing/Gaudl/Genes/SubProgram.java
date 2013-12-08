package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;


public class SubProgram extends MarioCommand{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3673639561584674103L;
	
	public SubProgram(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,3,CommandGene.BooleanClass);
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "&1; &2; &3;";
	}
	
	public String getName() {
		return "subProgram";
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args){
		c.execute_boolean(a_n, 0, a_args);
		c.execute_boolean(a_n, 1, a_args);
		c.execute_boolean(a_n, 2, a_args);
		
		return true;
		
	}
	
	@SuppressWarnings("rawtypes")
	public Class getChildType(IGPProgram a_ind, int a_chromNum) {
		return CommandGene.BooleanClass;
			
	  }
	
}

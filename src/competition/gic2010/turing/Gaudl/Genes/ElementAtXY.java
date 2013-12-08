package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;


public class ElementAtXY extends MarioCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3673639561584674103L;
	
	public ElementAtXY(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,2,CommandGene.IntegerClass);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "elementAtXY ";
	}
	
	public String getName() {
		return "elementAtXY";
	}
	
	public int execute_int(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		
		return data.getEgoElementAt(c.execute_int(a_n, 0, a_args), c.execute_int(a_n, 1, a_args));
	}
	
	@SuppressWarnings("rawtypes")
	public Class getChildType(IGPProgram a_ind, int a_chromNum) {
		if (a_chromNum == 0)
			return CommandGene.BooleanClass;

		    return CommandGene.IntegerClass;
			
	  }

}

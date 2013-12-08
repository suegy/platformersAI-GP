package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;


public class IsBreakableAt extends MarioCommand implements IMutateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3673639561584674103L;
	
	public IsBreakableAt(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,2,CommandGene.BooleanClass);
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "isBreakableAt ";
	}
	
	public String getName() {
		return "isBreakableAt";
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		int x = c.execute_int(a_n, 0, a_args);
		int y = c.execute_int(a_n, 1, a_args);
		
		return data.canBreak(data.getEgoElementAt(x, y));
	}
	
	@SuppressWarnings("rawtypes")
	public Class getChildType(IGPProgram a_ind, int a_chromNum) {
		return CommandGene.IntegerClass;
			
	  }
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {

		if (a_percentage < 0.10d)
		    return new IsPrincessAt(getGPConfiguration());
		if (a_percentage < 0.15d)
		    return new IsMushroomAt(getGPConfiguration());
		if (a_percentage < 0.20d)
		    return new IsCoinAt(getGPConfiguration());
		if (a_percentage < 0.30d)
		    return new IsAirAt(getGPConfiguration());
		if (a_percentage < 0.40d)
		    return new IsFireFlowerAt(getGPConfiguration());
		if (a_percentage < 0.50d)
		    return new IsEnemyAt(getGPConfiguration());
		
		return new IsWalkableAt(getGPConfiguration());
	}

}

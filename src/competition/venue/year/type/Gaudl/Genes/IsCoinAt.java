package competition.venue.year.type.Gaudl.Genes;

import competition.venue.year.type.Gaudl.gp.MarioCommand;
import competition.venue.year.type.Gaudl.gp.MarioData;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;


public class IsCoinAt extends MarioCommand implements IMutateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 11825230591195140L;
	
	public IsCoinAt(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,2,CommandGene.BooleanClass);
	}

	public IsCoinAt() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "isCoinAt &1 &2";
	}
	
	public String getName() {
		return "isCoinAt";
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		int x = c.execute_int(a_n, 0, a_args);
		int y = c.execute_int(a_n, 1, a_args);
		
		return data.isCoin(data.getEgoElementAt(x, y));
	}
	

	
	
	@SuppressWarnings("rawtypes")
	public Class getChildType(IGPProgram a_ind, int a_chromNum) {
		return CommandGene.IntegerClass;
			
	  }
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {

		if (a_percentage < 0.142d)
		    return new IsAirAt(getGPConfiguration());
		if (a_percentage < 0.284d)
		    return new IsWalkableAt(getGPConfiguration());
		if (a_percentage < 0.426d)
		    return new IsEnemyAt(getGPConfiguration());
		if (a_percentage < 0.568d)
		    return new IsFireFlowerAt(getGPConfiguration());
		if (a_percentage < 0.71d)
		    return new IsMushroomAt(getGPConfiguration());
		if (a_percentage < 0.852d)
		    return new IsPrincessAt(getGPConfiguration());
		
		return new IsBreakableAt(getGPConfiguration());
	}


}
package competition.venue.year.type.Gaudl.Genes;

import competition.venue.year.type.Gaudl.gp.MarioCommand;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;

import competition.venue.year.type.Gaudl.gp.MarioData;


public class ElementAtXY extends MarioCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3673639561584674103L;
	public static int CanBreak = -4;
	public static int Coin = -3;
	public static int Enemy = -2;
	public static int Flower = -1;
	public static int Mushroom = 0;
	public static int Princess = 1;
	public static int Air = 2;
	public static int Walkable = 3;
	public static int Nothing = 4;
	
	
	
	public ElementAtXY(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,2,CommandGene.IntegerClass);
	}

	public ElementAtXY() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "elementAtXY &1 &2";
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

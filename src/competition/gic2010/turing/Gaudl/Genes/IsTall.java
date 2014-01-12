package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;


public class IsTall extends MarioCommand implements IMutateable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3004018066050264465L;

	public IsTall(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,0,CommandGene.BooleanClass);
		
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "isTall";
	}
	
	public String getName() {
		return "isTall";
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		
		return data.isTall();
	}


	@Override
	public CommandGene applyMutation(int a_index, double a_percentage) throws InvalidConfigurationException{
		if (a_percentage < 0.10d)
		    return new IsPrincessAt(getGPConfiguration());
		if (a_percentage < 0.15d)
		    return new IsCoinAt(getGPConfiguration());
		if (a_percentage < 0.20d)
		    return new IsBreakableAt(getGPConfiguration());
		if (a_percentage < 0.30d)
		    return new IsAirAt(getGPConfiguration());
		if (a_percentage < 0.40d)
		    return new IsFireFlowerAt(getGPConfiguration());
		if (a_percentage < 0.50d)
		    return new IsMushroomAt(getGPConfiguration());
		
		return new IsEnemyAt(getGPConfiguration());
	}

}

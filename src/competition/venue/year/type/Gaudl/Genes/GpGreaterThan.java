package competition.venue.year.type.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.function.GreaterThan;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.gp.terminal.True;

public class GpGreaterThan extends GreaterThan {

	

	public GpGreaterThan(GPConfiguration a_conf, Class a_type)
			throws InvalidConfigurationException {
		super(a_conf, a_type);
		// TODO Auto-generated constructor stub
	}

	public GpGreaterThan() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration(), CommandGene.IntegerClass);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6284208713921376897L;
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args){}

}

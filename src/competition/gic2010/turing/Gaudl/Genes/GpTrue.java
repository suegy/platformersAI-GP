package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.gp.terminal.True;

public class GpTrue extends True {

	public GpTrue(GPConfiguration a_conf) throws InvalidConfigurationException {
		super(a_conf);
		// TODO Auto-generated constructor stub
	}

	public GpTrue() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6284208713921376897L;
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args){}

}

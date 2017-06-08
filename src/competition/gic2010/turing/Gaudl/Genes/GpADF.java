package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.function.ADF;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;

public class GpADF extends ADF {

	public GpADF(GPConfiguration a_conf, int a_chromosomeNum, int a_arity)
			throws InvalidConfigurationException {
		super(a_conf, a_chromosomeNum, a_arity);
		// TODO Auto-generated constructor stub
	}

	public GpADF() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration(),1,1);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6713581051524635659L;
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args) {
		c.execute_void(a_n, 0, a_args);
	}

}

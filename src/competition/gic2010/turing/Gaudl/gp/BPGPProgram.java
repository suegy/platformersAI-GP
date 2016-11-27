package competition.gic2010.turing.Gaudl.gp;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPProgram;
import org.jgap.gp.impl.ProgramChromosome;

public class BPGPProgram extends GPProgram {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2636355029923331295L;

	public BPGPProgram(GPConfiguration a_conf, Class[] a_types,
			Class[][] a_argTypes, CommandGene[][] a_nodeSets,
			int[] a_minDepths, int[] a_maxDepths, int a_maxNodes)
					throws InvalidConfigurationException {
		super(a_conf,a_types,a_argTypes,a_nodeSets,a_minDepths,a_maxDepths,a_maxNodes);

	}

	public BPGPProgram(IGPProgram a_prog)
			throws InvalidConfigurationException {
		super(a_prog);
	}

	public BPGPProgram(GPConfiguration a_conf, int a_numChromosomes)
			throws InvalidConfigurationException {
		super(a_conf,a_numChromosomes);
	}

	

}

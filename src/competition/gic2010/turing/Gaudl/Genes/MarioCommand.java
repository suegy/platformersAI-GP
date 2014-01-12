package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;


public class MarioCommand extends CommandGene {

	/**
	 * 
	 */
	
	public static final int VoidClass = 0;
	public static final int BooleanClass = 1;
	public static final int IntegerClass = 2;
	public static final int FloatClass = 3;
	
	private static final long serialVersionUID = 652817845838641019L;
	protected Class m_type;
	

	public MarioCommand(GPConfiguration a_conf, int a_arity, Class a_returnType)
			throws InvalidConfigurationException {
		super(a_conf, a_arity, a_returnType);
		m_type = a_returnType;		
		
	}
	
	public MarioCommand(final GPConfiguration a_conf)
		      throws InvalidConfigurationException {
		    super(a_conf, 0, CommandGene.VoidClass);
		    m_type = CommandGene.VoidClass;
		  }
	
	  

	@SuppressWarnings("rawtypes")
	public MarioCommand(final GPConfiguration a_conf, int arity ,Class a_type, int a_subReturnType,
	                int[] a_subChildTypes)
	      throws InvalidConfigurationException {
	    super(a_conf, arity, a_type, a_subReturnType, a_subChildTypes);
	    m_type = a_type;
	  }
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public MarioData getMarioData(ProgramChromosome a_chrom) {
	    return (MarioData)a_chrom.getIndividual().getApplicationData();
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args){}
	
	public int execute_int(ProgramChromosome c, int a_n, Object[] a_args){
		if (m_type == CommandGene.BooleanClass){
			return (execute_boolean(c, a_n, a_args)) ? 1 : 0;
		}
		return 0;
	}

}

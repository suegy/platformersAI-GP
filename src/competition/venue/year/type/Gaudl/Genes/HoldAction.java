package competition.venue.year.type.Gaudl.Genes;

import competition.venue.year.type.Gaudl.gp.MarioCommand;
import competition.venue.year.type.Gaudl.gp.MarioData;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;

import competition.venue.year.type.Gaudl.Genes.ControllerButton.Ebutton;

public class HoldAction extends MarioCommand {

	public HoldAction(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,1,CommandGene.VoidClass);
		// TODO Auto-generated constructor stub
	}

	public HoldAction() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1553834656005411884L;
	
	@SuppressWarnings("rawtypes")
	public Class getChildType(IGPProgram a_ind, int a_chromNum) {
		return ControllerButton.class;
			
	  }
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		Object x = c.execute_object(a_n, 0, a_args);
		if (x instanceof Ebutton){
		/*TODO use mariodata to set button active until it is released*/	
		}
		return false;
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		int x = c.execute_int(a_n, 0, a_args);
		int y = c.execute_int(a_n, 1, a_args);
		
	}

}

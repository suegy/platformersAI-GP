package competition.venue.year.type.Gaudl.Genes;

import competition.venue.year.type.Gaudl.gp.MarioCommand;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;

import org.jgap.gp.impl.GPGenotype;

public class TriggerAction extends MarioCommand implements IMutateable{

	public TriggerAction(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,1,CommandGene.BooleanClass);
		// TODO Auto-generated constructor stub
	}

	public TriggerAction() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7932861895343443952L;
	
	@SuppressWarnings("rawtypes")
	public Class getChildType(IGPProgram a_ind, int a_chromNum) {
		return ControllerButton.class;
			
	  }

	@Override
	public CommandGene applyMutation(int a_index, double a_percentage)
			throws InvalidConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

}

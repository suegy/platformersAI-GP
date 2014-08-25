package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;

import competition.gic2010.turing.Gaudl.gp.MarioCommand;

public class TriggerAction extends MarioCommand implements IMutateable{

	public TriggerAction(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,1,CommandGene.BooleanClass);
		// TODO Auto-generated constructor stub
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

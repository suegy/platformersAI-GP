package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.gp.terminal.Terminal;
import org.jgap.util.CloneException;
import org.jgap.util.ICloneable;

public class ControllerButton extends CommandGene implements IMutateable,ICloneable{


	private Ebutton m_button = Ebutton.NONE;
	
	public static enum Ebutton {NONE,UP,DOWN,LEFT,RIGHT,JUMP,SHOOT};
	
	public ControllerButton(GPConfiguration a_conf) throws InvalidConfigurationException {
		super(a_conf, 0, Ebutton.class);
		createRandomButton();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4735119821092293572L;

	private void createRandomButton(){
		int choice = getGPConfiguration().getRandomGenerator().nextInt(Ebutton.values().length);
		this.m_button = Ebutton.values()[choice];
	}
	
	private void createRandomButton(double percentage){
		int choice = (int) (Ebutton.values().length*percentage);
		this.m_button = Ebutton.values()[choice];
	}
	
	@Override
	public String toString() {
		return "b-"+m_button.name();
	}
	
		
	public Object execute_object(ProgramChromosome c, int a_n, Object[] a_args){
		
		return m_button;
	}

	@Override
	public CommandGene applyMutation(int a_index, double a_percentage)
			throws InvalidConfigurationException {
		ControllerButton button = new ControllerButton(getGPConfiguration());
		button.createRandomButton(a_percentage);
		
		return button;
	}
	
	public Object clone() {
	    try {
	      ControllerButton result = new ControllerButton(getGPConfiguration());
	      result.m_button= m_button;
	      return result;
	    } catch (Throwable t) {
	      throw new CloneException(t);
	    }
	  }

}

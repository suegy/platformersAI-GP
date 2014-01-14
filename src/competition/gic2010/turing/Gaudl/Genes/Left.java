package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ch.idsia.benchmark.mario.engine.sprites.Mario;


public class Left extends MarioCommand implements IMutateable{




	/**
	 * 
	 */
	private static final long serialVersionUID = 7016345793588283803L;

	public Left(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "left";
	}
	
	public String getName() {
		return "left";
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.moveLeft();
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.moveRight();
		return true;
	}
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {

		if (a_percentage < 0.20d)
		    return new Jump(getGPConfiguration());
		if (a_percentage < 0.30d)
		    return new LongJump(getGPConfiguration());
		if (a_percentage < 0.40d)
		    return new Shoot(getGPConfiguration());
		if (a_percentage < 0.50d)
		    return new Down(getGPConfiguration());
		
		return new Right(getGPConfiguration());
	}

}
package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

public class Shoot extends MarioCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8002984028415270780L;

	public Shoot(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,0,CommandGene.BooleanClass);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "shoot";
	}
	
	public String getName() {
		return "shoot";
	}
	
	public void execute_void(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		data.shoot();
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args) {
		MarioData data = getMarioData(c);
		
		if (data.canShoot()){
			data.shoot();
			return true;
		}
		
		return false;
	}
	
	public CommandGene applyMutation(int index, double a_percentage)
		      throws InvalidConfigurationException {

		if (a_percentage < 0.20d)
		    return new Down(getGPConfiguration());
		if (a_percentage < 0.30d)
		    return new Left(getGPConfiguration());
		if (a_percentage < 0.40d)
		    return new Jump(getGPConfiguration());
		if (a_percentage < 0.50d)
		    return new LongJump(getGPConfiguration());
		
		return new Right(getGPConfiguration());
	}

}

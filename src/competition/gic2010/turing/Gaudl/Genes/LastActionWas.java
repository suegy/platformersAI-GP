package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

public class LastActionWas extends MarioCommand{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2566287475789254483L;
	
	public LastActionWas(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,1,CommandGene.BooleanClass);
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "lastActionWas";
	}
	
	public String getName() {
		return "lastActionWas";
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		int x = c.execute_int(a_n, 0, a_args);
		switch (x) {
		case Mario.KEY_DOWN:
			return data.getLastActions()[x];
		case Mario.KEY_LEFT:
			return data.getLastActions()[x];
		case Mario.KEY_RIGHT:
			return data.getLastActions()[x];
		case Mario.KEY_JUMP:
			return data.getLastActions()[x];
		case Mario.KEY_SPEED:
			return data.getLastActions()[x];
		default:
			return false;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Class getChildType(IGPProgram a_ind, int a_chromNum) {
		return CommandGene.IntegerClass;
			
	  }
	
	}

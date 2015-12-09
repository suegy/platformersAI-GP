package competition.gic2010.turing.Gaudl.Genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

import competition.gic2010.turing.Gaudl.gp.MarioCommand;
import competition.gic2010.turing.Gaudl.gp.MarioData;
import ch.idsia.benchmark.mario.engine.sprites.Mario;

public class LastActionWas extends MarioCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2566287475789254483L;
	
	public LastActionWas(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,2,CommandGene.BooleanClass);
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "lastActionWas &1";
	}
	
	public String getName() {
		return "lastActionWas";
	}
	
	/*
	 * checks in the history [y] if action [x] was present
	 * @see org.jgap.gp.CommandGene#execute_boolean(org.jgap.gp.impl.ProgramChromosome, int, java.lang.Object[])
	 */
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		int x = c.execute_int(a_n, 0, a_args);
		int y = c.execute_int(a_n, 1, a_args);
		y = Math.abs(y) % 5;
		switch (y) {
		case Mario.KEY_DOWN:
			return data.getLastActions(x)[y];
		case Mario.KEY_LEFT:
			return data.getLastActions(x)[y];
		case Mario.KEY_RIGHT:
			return data.getLastActions(x)[y];
		case Mario.KEY_JUMP:
			return data.getLastActions(x)[y];
		case Mario.KEY_SPEED:
			return data.getLastActions(x)[y];
		default:
			return false;
		}
	}
	
	public int execute_int(ProgramChromosome c, int a_n, Object[] a_args){
		if (execute_boolean(c, a_n, a_args))
			return 1;
		else 
			return 0;
	
	}
	
	@SuppressWarnings("rawtypes")
	public Class getChildType(IGPProgram a_ind, int a_chromNum) {
		return CommandGene.IntegerClass;
	  }
	
	}

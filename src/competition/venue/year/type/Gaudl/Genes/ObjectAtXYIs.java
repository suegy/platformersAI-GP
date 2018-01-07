package competition.venue.year.type.Gaudl.Genes;

import competition.venue.year.type.Gaudl.gp.MarioCommand;
import competition.venue.year.type.Gaudl.gp.MarioData;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;

import org.platformer.benchmark.platform.engine.sprites.Sprite;


public class ObjectAtXYIs extends MarioCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3673639561584674103L;
	
	public static final int CanBreak = -4;
	public static final int Coin = -3;
	public static final int Enemy = -2;
	public static final int FireFlower = -1;
	public static final int Mushroom = 0;
	public static final int Princess = 1;
	public static final int Air = 2;
	public static final int Walkable = 3;
	public static final int Unkown = 4;
	
	
	
	public ObjectAtXYIs(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf,3,CommandGene.BooleanClass);
	}

	public ObjectAtXYIs() throws InvalidConfigurationException{
		this(GPGenotype.getStaticGPConfiguration());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "objectAtXYIs &1, &2, &3";
	}
	
	public String getName() {
		return "objectAtXYIs";
	}
	
	public int execute_int(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		int elem = data.getEgoElementAt(c.execute_int(a_n, 0, a_args), c.execute_int(a_n, 1, a_args));
		
		switch (elem) {
		case Sprite.KIND_NONE:
			return Air;
		case -20:
		case -22:
			return CanBreak;
		case Sprite.KIND_BULLET_BILL:
		case Sprite.KIND_SPIKY:
		case Sprite.KIND_SPIKY_WINGED:
		case Sprite.KIND_GOOMBA:
		case Sprite.KIND_GOOMBA_WINGED:
		case Sprite.KIND_GREEN_KOOPA:
		case Sprite.KIND_GREEN_KOOPA_WINGED:
		case Sprite.KIND_RED_KOOPA:
		case Sprite.KIND_RED_KOOPA_WINGED:
			return Enemy;
		case Sprite.KIND_COIN_ANIM:
			return Coin;
		case Sprite.KIND_MUSHROOM:
			return Mushroom;
		case Sprite.KIND_FIRE_FLOWER:
			return FireFlower;
		case Sprite.KIND_PRINCESS:
			return Princess;
		case -60:
			return Walkable;
		default:
			return Unkown;
		}
	}
	
	public boolean execute_boolean(ProgramChromosome c, int a_n, Object[] a_args){
		MarioData data = getMarioData(c);
		int elem = Sprite.KIND_NONE;
		try{
			elem = data.getEgoElementAt(c.execute_int(a_n, 0, a_args), c.execute_int(a_n, 1, a_args));
		}catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(String.format("arity: %d, representation: %s", c.getArity(), c.getIndividual().toStringNorm(0)));
			System.out.println(e);
			return false;
		}
		catch( UnsupportedOperationException v){
			System.out.println(String.format("arity: %d, representation: %s", c.getArity(), c.getIndividual().toStringNorm(0)));
			System.out.println(v);
			return false;
		}
		switch (elem) {
		case Sprite.KIND_NONE:
			return (Air == c.execute_int(a_n, 2, a_args));
		case -20:
		case -22:
			return (CanBreak == c.execute_int(a_n, 2, a_args));
		case Sprite.KIND_BULLET_BILL:
		case Sprite.KIND_SPIKY:
		case Sprite.KIND_SPIKY_WINGED:
		case Sprite.KIND_GOOMBA:
		case Sprite.KIND_GOOMBA_WINGED:
		case Sprite.KIND_GREEN_KOOPA:
		case Sprite.KIND_GREEN_KOOPA_WINGED:
		case Sprite.KIND_RED_KOOPA:
		case Sprite.KIND_RED_KOOPA_WINGED:
			return (Enemy == c.execute_int(a_n, 2, a_args));
		case Sprite.KIND_COIN_ANIM:
			return (Coin == c.execute_int(a_n, 2, a_args));
		case Sprite.KIND_MUSHROOM:
			return (Mushroom == c.execute_int(a_n, 2, a_args));
		case Sprite.KIND_FIRE_FLOWER:
			return (FireFlower == c.execute_int(a_n, 2, a_args));
		case Sprite.KIND_PRINCESS:
			return (Princess == c.execute_int(a_n, 2, a_args));
		case -60:
			return (Walkable == c.execute_int(a_n, 2, a_args));
		default:
			return (Unkown == c.execute_int(a_n, 2, a_args));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Class getChildType(IGPProgram a_ind, int a_chromNum) {
		if (a_chromNum == 0)
			return CommandGene.IntegerClass;

		    return CommandGene.IntegerClass;
			
	  }
	

}

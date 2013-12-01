package competition.gic2010.turing.Gaudl.Genes;

import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.level.Level;
import ch.idsia.benchmark.mario.engine.mapedit.TilePicker;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;

public class MarioData {


	private boolean[] actions;
	private int lastAction;
	private Environment environment;

	public MarioData(Environment env) {
		//TODO: If the number of actions chance this has to change as well
		actions = new boolean[Environment.numberOfKeys];
		this.environment = env;
	}

	public void setEnvironment(Environment env) {
		this.environment = env;
	}

	public boolean[] getActions() {
		boolean [] output = this.actions.clone();
		this.actions = new boolean[output.length];
		return output;
	}

	public void setActions(boolean [] act) {
		this.actions = act;
	}

	public void setAction(boolean act,int marioaction) {
		this.actions[marioaction] = act;
	}

	public byte[][] getSensorField() {
		return environment.getMergedObservationZZ(1, 0);
	}

	public int getElementAt(int x,int y) {
		if (x < 0 || x >= environment.getReceptiveFieldWidth() || y < 0 || y >= environment.getReceptiveFieldHeight())
	        return 0;
		return getSensorField()[x][y];
	}
	public void jump() {
		setAction(true, Environment.MARIO_KEY_JUMP);
	}
	public void moveLeft() {
		setAction(true, Environment.MARIO_KEY_LEFT);
	}
	public void moveRight() {
		setAction(true, Environment.MARIO_KEY_RIGHT);
	}
	public void shoot() {
		setAction(true, Environment.MARIO_KEY_SPEED);
	}
	/* Removed because it has no impact on mario's behaviour
	 * public void Up() {
		setAction(true, 5);
	}*/
	public void down() {
		setAction(true, Environment.MARIO_KEY_DOWN);
	}
	public boolean canJump(){
		return environment.isMarioAbleToJump();
	}
	public boolean canShoot(){
		return environment.isMarioAbleToShoot();
	}
	
	/*
	 * checks if Mario can walk on the tile number.
	 * Walking mostly means touching it without follow through.
	 * This includes stomping as well.
	 */
	public boolean isWalkable(int elem){
		//TODO: This is not an optimal solution but a quick one.
		switch (elem) {
		case Sprite.KIND_NONE:
			return false;
		case Sprite.KIND_SPIKY:
		case Sprite.KIND_SPIKY_WINGED:
			return false;
		case Sprite.KIND_COIN_ANIM:
			return false;
		case Sprite.KIND_FIREBALL:
			return false;
		case Sprite.KIND_MUSHROOM:
			return false;
		case -60:
			return true;
		default:
			return true;
		}
	}
	
	public boolean isEnemy(int elem){
		switch (elem) {
		case Sprite.KIND_BULLET_BILL:
			return true;
		case Sprite.KIND_SPIKY:
		case Sprite.KIND_SPIKY_WINGED:
			return true;
		case Sprite.KIND_GOOMBA:
		case Sprite.KIND_GOOMBA_WINGED:
			return true;
		case Sprite.KIND_GREEN_KOOPA:
		case Sprite.KIND_GREEN_KOOPA_WINGED:
			return true;
		case Sprite.KIND_RED_KOOPA:
		case Sprite.KIND_RED_KOOPA_WINGED:
				return true;
		default:
			return false;
		}
	}
	
	public boolean isCoin(int elem){
		return (elem == Sprite.KIND_COIN_ANIM) ? true : false;
	}
	public boolean isFlower(int elem){
		return (elem == Sprite.KIND_FIRE_FLOWER) ? true : false;
	}
	public boolean isMushroom(int elem){
		return (elem == Sprite.KIND_MUSHROOM) ? true : false;
	}
	public boolean isPrincess(int elem){
		return (elem == Sprite.KIND_PRINCESS) ? true : false;
	}
	
	public boolean canBreak(int elem){
		switch (elem) {
		case -20:
		case -22:
			return true;
		default:
			return false;
		}
	}
	


}




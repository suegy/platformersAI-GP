package competition.gic2010.turing.Gaudl.Genes;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;

public class MarioData {


	private boolean[] actions;
	private boolean[] last_actions;
	private int longJump;
	//private int lastAction;
	private static Environment environment;

	public MarioData(Environment env) {
		//TODO: If the number of actions chance this has to change as well
		actions = new boolean[Environment.numberOfKeys];
		last_actions = new boolean[Environment.numberOfKeys];
		
		environment = env;
	}
	public MarioData() {
		//TODO: If the number of actions chance this has to change as well
		actions = new boolean[Environment.numberOfKeys];
		last_actions = new boolean[Environment.numberOfKeys];
		
	}

	public static void setEnvironment(Environment env) {
		environment = env;
	}
	public static Environment getEnvironment() {
		return environment;
	}

	public boolean[] getActions() {
		this.last_actions=this.actions.clone();
		this.actions = new boolean[last_actions.length];
		
		if (this.longJump > 0)
			jump();
		
		return last_actions;
	}

	public void setActions(boolean [] act) {
		this.actions = act;
	}
	
	public boolean[] getLastActions(){
		return this.last_actions;
	}

	public void setAction(boolean act,int marioaction) {
		this.actions[marioaction] = act;
	}

	public byte[][] getSensorField() {
		return (environment != null) ? environment.getMergedObservationZZ(1, 0) : null;
	}

	public int getElementAt(int x,int y) {
		if (environment == null)
			return 0;
		if (x < 0 || x >= environment.getReceptiveFieldWidth() || y < 0 || y >= environment.getReceptiveFieldHeight())
	        return 0;
		return getSensorField()[x][y];
	}
	public int getEgoElementAt(int x,int y) {
		if (environment == null)
			return 0;
		byte[][] sensorField = getSensorField();
		if (x < 0 || x >= environment.getReceptiveFieldWidth() || y < 0 || y >= environment.getReceptiveFieldHeight())
	        return 0;
		
		return sensorField[environment.getMarioEgoPos()[0]+x][environment.getMarioEgoPos()[1]+y];
	}
	public void jump() {
		setAction(true, Environment.MARIO_KEY_JUMP);
	}
	
	public boolean longJump(){
		if (this.longJump > 0){
			jump();
			this.longJump--;
			return false;
		}

		this.longJump = 24;
		return true;
		
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
	public void up() {
		setAction(true, 5);
	}
	public void down() {
		setAction(true, Environment.MARIO_KEY_DOWN);
	}
	public boolean canJump(){
		if (environment == null)
			return false;
		return environment.isMarioAbleToJump();
	}
	public boolean canShoot(){
		if (environment == null)
			return false;
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
	
	public boolean isTall(){
		if (environment == null)
			return false;
		return  (environment.getMarioMode() >= 1) ? true : false;
	}
	
	public boolean isCoin(int elem){
		return (elem == Sprite.KIND_COIN_ANIM) ? true : false;
	}
	public boolean isAir(int elem){
		return (elem == Sprite.KIND_NONE) ? true : false;
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




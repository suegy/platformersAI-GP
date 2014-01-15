package competition.gic2010.turing.Gaudl.Genes;

import java.io.IOException;
import java.util.ArrayList;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;

public class MarioData {


	private boolean[] actions;
	private boolean[] last_actions;
	private int longJump;
	private boolean longJumpRight;
	private boolean longJumpLeft;
	private int move;
	private int run;
	//private int lastAction;
	private static Environment environment;
	private static ArrayList<Byte> actionRecord;

	public MarioData(Environment env) {
		this();
		environment = env;
	}
	public MarioData() {
		//TODO: If the number of actions chance this has to change as well
		actions = new boolean[Environment.numberOfKeys];
		last_actions = new boolean[Environment.numberOfKeys];
		actionRecord = new ArrayList<>();
		
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

		
		if (this.longJump > 0){
			longJump();
			if (longJumpLeft)
				moveLeft();
			if (longJumpRight)
				moveRight();
		}
		storeAction(last_actions);
		return last_actions;
	}

	public void setActions(boolean [] act) {
		this.actions = act;
	}
	
	public static byte[] getActionTrace(){
		byte[] out = new byte[actionRecord.size()];
		
		for (int i = 0; i < out.length;i++) {
			out[i]=actionRecord.get(i);
		}
		return out;
	}
	
	protected void storeAction(boolean[] act){
		//copied from recorder code which writes the actions.act file
		byte action = 0;

		for (int i = 0; i < act.length; i++)
			if (act[i])
				action |= (1 << i);
		actionRecord.add(action);
	}
	
	public boolean[] getLastActions(){
		return this.last_actions;
	}

	public void setAction(boolean act,int marioaction) {
		this.actions[marioaction] = act;
	}

	@SuppressWarnings("unused")
	public byte[][] getSensorField() {
		byte[][] sensoryField;
	    byte[][] levelScene = environment.getLevelSceneObservationZ(1);
	    byte[][]enemies = environment.getEnemiesObservationZ(0);
	    byte[][] mergedObservation = environment.getMergedObservationZZ(1, 0);
		sensoryField= (environment != null) ? environment.getMergedObservationZZ(1, 0) : null;
		
		return sensoryField;
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
	public void jumpLeft() {
		this.moveLeft();
		this.jump();
		
	}
	public void jumpRight() {
		this.moveRight();
		this.jump();
		
	}
	public boolean longJump(){
		if (this.longJump > 0){
			jump();
			this.longJump--;
			return false;
		}

		this.longJump = 10;
		this.longJumpLeft = false;
		this.longJumpRight = false;
		return true;
	}
	
	public void longJumpLeft() {

		if (this.longJump <= 0){
			this.longJump();
			this.moveLeft();
			this.longJumpRight = false;
			this.longJumpLeft = true;
			
			
		}
		
	}
	public void longJumpRight() {
		
		if (this.longJump <= 0){
			this.longJump();
			this.longJumpLeft = false;
			this.longJumpRight = true;
			this.moveRight();
			
		}
	}
	
	public boolean run(){
		if (this.run > 0){
			shoot();
			this.run--;
			return false;
		}

		this.run = 5;
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




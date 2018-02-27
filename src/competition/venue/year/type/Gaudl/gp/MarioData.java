package competition.venue.year.type.Gaudl.gp;

import org.platformer.benchmark.platform.engine.sprites.Sprite;
import org.platformer.benchmark.platform.environments.Environment;

import java.io.IOException;
import java.util.ArrayList;

public class MarioData {


	private boolean[] actions;
	private boolean[][] last_actions;
	private int lastActionLength =50;
	private int lastActionCounter = 0;
	private int longJump;
	private boolean longJumpRight;
	private boolean longJumpLeft;
	private int move;
	private int run;
	//private int lastAction;
	private Environment environment;
	private ArrayList<Byte> actionRecord;

	public MarioData(Environment env) {
		this();
		environment = env;
	}
	public MarioData() {
		//TODO: If the number of actions chance this has to change as well
		actions = new boolean[Environment.numberOfKeys];
		//TODO: currently the memory goes only back x timesteps and the position is identified by lastActionCounter
		last_actions = new boolean[lastActionLength][Environment.numberOfKeys];
		actionRecord = new ArrayList<Byte>();
		
	}

	public void setEnvironment(Environment env) {
		environment = env;
	}
	public Environment getEnvironment() {
		return environment;
	}

	public boolean[] getActions() {
		if (lastActionCounter >= lastActionLength)
			lastActionCounter = 0;
		this.last_actions[lastActionCounter]=this.actions.clone();
		this.actions = new boolean[last_actions.length];
		
		
		if (this.longJump > 0){
			if (longJumpLeft)
				longJump();
				moveLeft();
			if (longJumpRight)
				longJump();
				moveRight();
		}
		if (this.run > 0)
			run();
		storeAction(last_actions[lastActionCounter]);
		return last_actions[lastActionCounter++];
	}

	public void setActions(boolean [] act) {
		this.actions = act;
	}
	
	public byte[] getActionTrace(){
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
	
	public boolean[] getLastActions(int time){
		time = Math.abs(time);
		return this.last_actions[time%lastActionLength];
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
		sensoryField= (environment != null) ? mergedObservation : null;
		
		return sensoryField;
	}

	public int getElementAt(int x,int y) {
		if (environment == null)
			return 0;
		if (x < 0 || x >= environment.getReceptiveFieldWidth() || y < 0 || y >= environment.getReceptiveFieldHeight())
	        return 0;
		return getSensorField()[y][x];
	}
	public int getEgoElementAt(int x,int y) {
		if (environment == null)
			return 0;
		byte[][] sensorField = getSensorField();
		x = environment.getMarioEgoPos()[0]+x;
		y = environment.getMarioEgoPos()[1]+y;
		if (y < 0 || y >= sensorField.length-1 || x < 0 || x >= sensorField[0].length-1)
	        return 0;
		
		return sensorField[y][x];
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
			this.longJumpLeft = true;
			this.longJumpRight = false;
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
			case -60: // ground block
				return true;
            case 96: //TODO: check if that works walking on enemies
            case 97:
            case 80:
                return true;
			case 24: //breakable
				return true;
			case -85: // pipe
		default:
			return true;
		}
	}
	
	public boolean isEnemy(int elem){
		switch (elem) {
			case 13:
				return true;
			case 80://Goomba
				return true;
			case 96://Sprite.KIND_GREEN_KOOPA:
				 case 97://Sprite.KIND_RED_KOOPA:
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
		return (elem == 2) ? true : false;
	}
	public boolean isAir(int elem){
		return (elem == 0 || elem == 2 || elem == 3 || elem == 5) ? true : false;
	}
	public boolean isFlower(int elem){
		return (elem == 3) ? true : false;
	}
	public boolean isMushroom(int elem){
		return (elem == 2) ? true : false;
	}
	public boolean isPrincess(int elem){
		return (elem == 5) ? true : false;
	}
	
	public boolean canBreak(int elem){
		switch (elem) {
		case -24:
			return true;
		default:
			return false;
		}
	}
	public void pause() {
		setActions(new boolean[actions.length]);
		
	}
	
	


}




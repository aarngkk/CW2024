package com.example.demo;

public class EnemyPlane extends FighterPlane {

	private static final String ENEMY_FIRE_SOUND = "/com/example/demo/audio/enemyfire.wav";
	private static final String IMAGE_NAME = "enemyplane.png";
	private static final int IMAGE_HEIGHT = 40;
	private static final int HORIZONTAL_VELOCITY = -6;
	private static final double PROJECTILE_X_POSITION_OFFSET = -45;
	private static final double PROJECTILE_Y_POSITION_OFFSET = 20;
	private static final int INITIAL_HEALTH = 1;
	private static final double FIRE_RATE = .01;
	private static final SoundEffectPlayer sharedFireSound = new SoundEffectPlayer(ENEMY_FIRE_SOUND);
	static {
		sharedFireSound.setVolume(0.07);
	}

	public EnemyPlane(double initialXPos, double initialYPos) {
		super(IMAGE_NAME, IMAGE_HEIGHT, initialXPos, initialYPos, INITIAL_HEALTH);
	}

	@Override
	public void updatePosition() {
		moveHorizontally(HORIZONTAL_VELOCITY);
	}

	@Override
	public ActiveActorDestructible fireProjectile() {
		if (Math.random() < FIRE_RATE) {
			double projectileXPosition = getProjectileXPosition(PROJECTILE_X_POSITION_OFFSET);
			double projectileYPostion = getProjectileYPosition(PROJECTILE_Y_POSITION_OFFSET);
			sharedFireSound.playSound();
			return new EnemyProjectile(projectileXPosition, projectileYPostion);
		}
		return null;
	}

	@Override
	public void updateActor() {
		updatePosition();
	}

}

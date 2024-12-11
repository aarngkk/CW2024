package com.finalflight.game.gameobjects;

import com.finalflight.game.audio.SoundEffectPlayer;

public class EnemyPlane extends FighterPlane {

	private static final String ENEMY_FIRE_SOUND = "/com/finalflight/game/audio/enemyfire.wav";
	private static final String IMAGE_NAME = "enemyplane.png";
	private static final int IMAGE_HEIGHT = 40;
	private static final int HORIZONTAL_VELOCITY = -6;
	private static final double PROJECTILE_X_POSITION_OFFSET = -40;
	private static final double PROJECTILE_Y_POSITION_OFFSET = 18;
	private static final int INITIAL_HEALTH = 3;
	private static final double FIRE_RATE = .01;
	private static final SoundEffectPlayer fireSound = new SoundEffectPlayer(ENEMY_FIRE_SOUND);
	static {
		fireSound.setVolume(0.07);
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
		if (randomSupplier.get() < FIRE_RATE) {
			double projectileXPosition = getProjectileXPosition(PROJECTILE_X_POSITION_OFFSET);
			double projectileYPosition = getProjectileYPosition(PROJECTILE_Y_POSITION_OFFSET);
			fireSound.playSound();
			return new EnemyProjectile(projectileXPosition, projectileYPosition);
		}
		return null;
	}

	@Override
	public void updateActor() {
		updatePosition();
	}

}

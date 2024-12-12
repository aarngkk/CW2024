package com.finalflight.game.gameobjects;

public class BossProjectile extends Projectile {
	
	private static final String IMAGE_NAME = "bossfire2.png";
	private static final int IMAGE_HEIGHT = 65;
	private static final int HORIZONTAL_VELOCITY = -15;
	private static final int INITIAL_X_POSITION = 1020;

	public BossProjectile(double initialYPos) {
		super(IMAGE_NAME, IMAGE_HEIGHT, INITIAL_X_POSITION, initialYPos);
	}

	@Override
	public void updatePosition() {
		moveHorizontally(HORIZONTAL_VELOCITY);
	}
	
	@Override
	public void updateActor() {
		updatePosition();
	}
	
}

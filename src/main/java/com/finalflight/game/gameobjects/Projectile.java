package com.finalflight.game.gameobjects;

public abstract class Projectile extends ActiveActorDestructible {

	public Projectile(String imageName, int imageHeight, double initialXPos, double initialYPos) {
		super(imageName, imageHeight, initialXPos, initialYPos);
	}

	// Overloaded constructor for testing
	public Projectile(String imageName, int imageHeight, double initialXPos, double initialYPos, boolean skipImageLoading) {
		super(imageName, imageHeight, initialXPos, initialYPos, skipImageLoading);
	}

	@Override
	public void takeDamage() {
		this.destroy();
	}

	@Override
	public abstract void updatePosition();

}

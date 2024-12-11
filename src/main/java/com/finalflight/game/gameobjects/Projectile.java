package com.finalflight.game.gameobjects;

public abstract class Projectile extends ActiveActorDestructible {

	private final String imageName;
	private final int imageHeight;

	public Projectile(String imageName, int imageHeight, double initialXPos, double initialYPos) {
		super(imageName, imageHeight, initialXPos, initialYPos);
		this.imageName = imageName;
		this.imageHeight = imageHeight;
	}

	// Overloaded constructor for testing
	public Projectile(String imageName, int imageHeight, double initialXPos, double initialYPos, boolean skipImageLoading) {
		super(imageName, imageHeight, initialXPos, initialYPos, skipImageLoading);
		this.imageName = imageName;
		this.imageHeight = imageHeight;
	}

	@Override
	public void takeDamage() {
		this.destroy();
	}

	@Override
	public abstract void updatePosition();

	public String getImageName() {
		return imageName;
	}

	public int getImageHeight() {
		return imageHeight;
	}

}

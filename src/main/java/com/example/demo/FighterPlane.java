package com.example.demo;

public abstract class FighterPlane extends ActiveActorDestructible {

	private int health;
	private final int maxHealth; // Store max health

	public FighterPlane(String imageName, int imageHeight, double initialXPos, double initialYPos, int health) {
		super(imageName, imageHeight, initialXPos, initialYPos);
		this.health = health;
		this.maxHealth = health;
	}

	public abstract ActiveActorDestructible fireProjectile();
	
	@Override
	public void takeDamage() {
		health--;
		if (health < 0) {
			health = 0;  // Ensure health doesn't go below 0
		}
		if (healthAtZero()) {
			this.destroy();
		}
	}

	protected double getProjectileXPosition() {
		return getLayoutX() + getTranslateX();
	}

	protected double getProjectileYPosition(double yPositionOffset) {
		return getLayoutY() + getTranslateY() + yPositionOffset;
	}

	private boolean healthAtZero() {
		return health == 0;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}
}

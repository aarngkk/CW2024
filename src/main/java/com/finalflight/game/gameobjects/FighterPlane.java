package com.finalflight.game.gameobjects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;

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

		flashOnDamage();

		if (healthAtZero()) {
			this.destroy();
		}
	}

	private void flashOnDamage() {
		// Create a ColorAdjust effect to create a flash effect
		ColorAdjust flashEffect = new ColorAdjust();
		flashEffect.setBrightness(0.5);
		setEffect(flashEffect);

		// Timeline to reset the flash after a short duration
		Timeline flashTimeline = new Timeline(
				new KeyFrame(Duration.millis(200), event -> setEffect(null)) // Reset effect
		);

		flashTimeline.setCycleCount(1); // Only flash once
		flashTimeline.play();
	}

	protected double getProjectileXPosition(double xPositionOffset) {
		return getLayoutX() + getTranslateX() + xPositionOffset;
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

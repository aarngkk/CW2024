package com.finalflight.game.gameobjects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;

import java.util.function.Supplier;

/**
 * The {@code FighterPlane} class represents a general fighter plane in the game.
 * It is an abstract class that provides basic attributes and behaviors for
 * fighter planes, including health management, damage handling, and projectile firing.
 *
 * <p>Subclasses must implement specific behaviors for updating position and firing projectiles.</p>
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/gameobjects/FighterPlane.java</p>
 */
public abstract class FighterPlane extends DestructibleGameObject {

    private int health;
    private final int maxHealth; // Store max health
    protected Supplier<Double> randomSupplier = Math::random; // Default to Math.random()

    /**
     * Constructs a {@code FighterPlane} with the specified attributes.
     *
     * @param imageName     the image name of the fighter plane.
     * @param imageHeight   the height of the image.
     * @param initialXPos   the initial X position.
     * @param initialYPos   the initial Y position.
     * @param health        the initial health of the fighter plane.
     */
    public FighterPlane(String imageName, int imageHeight, double initialXPos, double initialYPos, int health) {
        super(imageName, imageHeight, initialXPos, initialYPos);
        this.health = health;
        this.maxHealth = health;
    }

    /**
     * Overloaded constructor for testing purposes (skips image loading).
     *
     * @param imageName         the image name of the fighter plane.
     * @param imageHeight       the height of the image.
     * @param initialXPos       the initial X position.
     * @param initialYPos       the initial Y position.
     * @param health            the initial health of the fighter plane.
     * @param skipImageLoading  {@code true} to skip image loading, {@code false} otherwise.
     */
    public FighterPlane(String imageName, int imageHeight, double initialXPos, double initialYPos, int health, boolean skipImageLoading) {
        super(imageName, imageHeight, initialXPos, initialYPos, skipImageLoading);
        this.health = health;
        this.maxHealth = health;
    }

    /**
     * Abstract method to fire a projectile.
     * Subclasses must provide an implementation to define projectile behavior.
     *
     * @return a {@code DestructibleGameObject} representing the fired projectile.
     */
    public abstract DestructibleGameObject fireProjectile();

    /**
     * Reduces the health of the fighter plane by 1. If health reaches zero, the plane is destroyed.
     */
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

    /**
     * Creates a flash effect on the plane when it takes damage.
     */
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

    /**
     * Allows subclasses to customize the random supplier.
     *
     * @param randomSupplier a {@code Supplier<Double>} providing random values.
     */
    public void setRandomSupplier(Supplier<Double> randomSupplier) {
        this.randomSupplier = randomSupplier;
    }

    /**
     * Calculates the X position for a projectile based on the specified offset.
     *
     * @param xPositionOffset the offset to add to the current X position.
     * @return the calculated X position.
     */
    protected double getProjectileXPosition(double xPositionOffset) {
        return getLayoutX() + getTranslateX() + xPositionOffset;
    }

    /**
     * Calculates the Y position for a projectile based on the specified offset.
     *
     * @param yPositionOffset the offset to add to the current Y position.
     * @return the calculated Y position.
     */
    protected double getProjectileYPosition(double yPositionOffset) {
        return getLayoutY() + getTranslateY() + yPositionOffset;
    }

    /**
     * Checks if the health of the plane is zero.
     *
     * @return {@code true} if health is zero, {@code false} otherwise.
     */
    private boolean healthAtZero() {
        return health == 0;
    }

    /**
     * Returns the current health of the fighter plane.
     *
     * @return the current health.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Returns the maximum health of the fighter plane.
     *
     * @return the maximum health.
     */
    public int getMaxHealth() {
        return maxHealth;
    }
}

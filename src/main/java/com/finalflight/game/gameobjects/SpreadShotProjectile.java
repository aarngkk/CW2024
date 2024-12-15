package com.finalflight.game.gameobjects;

/**
 * The {@code SpreadShotProjectile} class represents a projectile with a spread effect,
 * moving in a specified direction and destroyed once it exceeds its range.
 *
 * <p>This class extends {@link AbstractProjectile} and adds functionality for range-based destruction
 * and directional movement based on initial velocity values.</p>
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/gameobjects/SpreadShotProjectile.java</p>
 */
public class SpreadShotProjectile extends AbstractProjectile {

    private static final double RANGE = 300.0;
    private static final String IMAGE_NAME = "userfire.png";
    private static final int IMAGE_HEIGHT = 6;
    private final double velocityX;
    private final double velocityY;
    private double traveledDistance = 0.0;

    /**
     * Constructs a {@code SpreadShotProjectile} with specified initial position and velocity.
     *
     * @param x         the initial X-coordinate of the projectile.
     * @param y         the initial Y-coordinate of the projectile.
     * @param velocityX the horizontal velocity of the projectile.
     * @param velocityY the vertical velocity of the projectile.
     */
    public SpreadShotProjectile(double x, double y, double velocityX, double velocityY) {
        super(IMAGE_NAME, IMAGE_HEIGHT, x, y);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /**
     * Updates the position of the spread-shot projectile and destroys it if it exceeds its range.
     */
    @Override
    public void updatePosition() {
        double newX = getTranslateX() + velocityX;
        double newY = getTranslateY() + velocityY;
        setTranslateX(newX);
        setTranslateY(newY);

        // Update traveled distance
        traveledDistance += Math.sqrt(velocityX * velocityX + velocityY * velocityY);

        // Destroy the projectile if it exceeds its range
        if (traveledDistance >= RANGE) {
            destroy();
        }
    }

    /**
     * Updates the state of the spread-shot projectile. This method ensures
     * the position update logic is executed.
     */
    @Override
    public void updateActor() {
        updatePosition();
    }

}

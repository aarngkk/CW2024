package com.example.demo;

public class SpreadProjectile extends Projectile {

    private static final double RANGE = 300.0;
    private static final String IMAGE_NAME = "userfire.png";
    private static final int DAMAGE = 6;
    private final double velocityX;
    private final double velocityY;
    private double traveledDistance = 0.0;

    /**
     * Creates a spread projectile with specified position and velocity.
     * @param x Initial X position
     * @param y Initial Y position
     * @param velocityX Horizontal velocity
     * @param velocityY Vertical velocity
     */
    public SpreadProjectile(double x, double y, double velocityX, double velocityY) {
        super(IMAGE_NAME, DAMAGE, x, y);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /**
     * Updates the position of the projectile and destroys it if it exceeds its range.
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

    @Override
    public void updateActor() {
        updatePosition();
    }

}

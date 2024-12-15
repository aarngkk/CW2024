package com.finalflight.game.gameobjects;

/**
 * The {@code HeavyShotProjectile} class represents a high-damage projectile with limited health.
 * It extends {@link AbstractProjectile} and introduces functionality for decrementing health
 * upon damage and movement in both horizontal and vertical directions.
 *
 * <p>This class is designed for slower, more durable projectiles with a predefined lifespan.</p>
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/gameobjects/HeavyShotProjectile.java</p>
 */
public class HeavyShotProjectile extends AbstractProjectile {

    private static final String IMAGE_NAME = "heavyfire.png";
    private static final int IMAGE_HEIGHT = 50;
    private static final double HORIZONTAL_VELOCITY = 8;
    private static final double VERTICAL_VELOCITY = 10;
    private static final int PROJECTILE_HEALTH = 6;
    private int health;

    /**
     * Constructs a {@code HeavyShotProjectile} with a predefined image and health,
     * setting its initial position.
     *
     * @param initialXPos the initial X-coordinate of the projectile.
     * @param initialYPos the initial Y-coordinate of the projectile.
     */
    public HeavyShotProjectile(double initialXPos, double initialYPos) {
        super(IMAGE_NAME, IMAGE_HEIGHT, initialXPos, initialYPos);
        this.health = PROJECTILE_HEALTH;
    }

    /**
     * Updates the position of the heavy-shot projectile, moving it in both
     * horizontal and vertical directions at predefined velocities.
     */
    @Override
    public void updatePosition() {
        setTranslateX(getTranslateX() + HORIZONTAL_VELOCITY);
        setTranslateY(getTranslateY() + VERTICAL_VELOCITY);
    }

    /**
     * Reduces the health of the projectile by 1 upon taking damage.
     * If the health reaches 0, the projectile is destroyed.
     */
    @Override
    public void takeDamage() {
        health--; // Decrease health by 1
        if (health <= 0) {
            health = 0;
            this.destroy(); // Destroy the projectile if health reaches 0
        }
    }

    /**
     * Updates the state of the heavy-shot projectile. This method ensures
     * the position update logic is executed.
     */
    @Override
    public void updateActor() {
        updatePosition();
    }

    /**
     * Retrieves the current health of the projectile.
     *
     * @return the current health as an {@code int}.
     */
    public int getHealth() {
        return health;
    }
}

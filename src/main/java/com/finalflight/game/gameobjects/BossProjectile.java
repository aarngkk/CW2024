package com.finalflight.game.gameobjects;

/**
 * The {@code BossProjectile} class represents projectiles fired by a boss enemy in the game.
 * It extends {@link AbstractProjectile} and defines specific behavior for movement and updates.
 *
 * <p>This class is tailored for boss attacks, featuring predefined image, velocity, and initial position.</p>
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/gameobjects/BossProjectile.java</p>
 */
public class BossProjectile extends AbstractProjectile {

    private static final String IMAGE_NAME = "bossfire2.png";
    private static final int IMAGE_HEIGHT = 65;
    private static final int HORIZONTAL_VELOCITY = -15;
    private static final int INITIAL_X_POSITION = 1020;

    /**
     * Constructs a {@code BossProjectile} with a predefined image and velocity, setting its initial Y-coordinate.
     *
     * @param initialYPos the initial Y-coordinate of the projectile.
     */
    public BossProjectile(double initialYPos) {
        super(IMAGE_NAME, IMAGE_HEIGHT, INITIAL_X_POSITION, initialYPos);
    }

    /**
     * Updates the position of the boss projectile, moving it horizontally at a constant velocity.
     */
    @Override
    public void updatePosition() {
        moveHorizontally(HORIZONTAL_VELOCITY);
    }

    /**
     * Updates the state of the boss projectile. This method ensures
     * the position update logic is executed.
     */
    @Override
    public void updateActor() {
        updatePosition();
    }

}

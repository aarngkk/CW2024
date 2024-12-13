/**
 * The {@code SingleShotProjectile} class represents a basic projectile fired by the player.
 * It extends {@link AbstractProjectile} and defines specific behavior for movement and updates.
 *
 * <p>This class is used for simple, straight-line projectiles with predefined image and velocity settings.</p>
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/gameobjects/SingleShotProjectile.java">SingleShotProjectile.java</a></p>
 */
package com.finalflight.game.gameobjects;

public class SingleShotProjectile extends AbstractProjectile {

    private static final String IMAGE_NAME = "userfire.png";
    private static final int IMAGE_HEIGHT = 8;
    private static final int HORIZONTAL_VELOCITY = 15;

    /**
     * Constructs a {@code SingleShotProjectile} with a predefined image and velocity, setting its initial position.
     *
     * @param initialXPos the initial X-coordinate of the projectile.
     * @param initialYPos the initial Y-coordinate of the projectile.
     */
    public SingleShotProjectile(double initialXPos, double initialYPos) {
        super(IMAGE_NAME, IMAGE_HEIGHT, initialXPos, initialYPos);
    }

    /**
     * Updates the position of the single-shot projectile, moving it horizontally at a constant velocity.
     */
    @Override
    public void updatePosition() {
        moveHorizontally(HORIZONTAL_VELOCITY);
    }

    /**
     * Updates the state of the single-shot projectile. This method ensures
     * the position update logic is executed.
     */
    @Override
    public void updateActor() {
        updatePosition();
    }

}

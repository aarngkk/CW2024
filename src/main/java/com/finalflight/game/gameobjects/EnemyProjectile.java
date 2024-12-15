package com.finalflight.game.gameobjects;

/**
 * The {@code EnemyProjectile} class represents projectiles fired by enemy entities
 * in the game. It extends {@link AbstractProjectile} and defines specific behavior
 * for movement and updates.
 *
 * <p>This class is tailored to enemy attacks, using predefined image and velocity settings
 * to simulate hostile projectiles moving across the screen.</p>
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/gameobjects/EnemyProjectile.java</p>
 */
public class EnemyProjectile extends AbstractProjectile {

    private static final String IMAGE_NAME = "enemyfire.png";
    private static final int IMAGE_HEIGHT = 16;
    private static final int HORIZONTAL_VELOCITY = -10;

    /**
     * Constructs an {@code EnemyProjectile} with a predefined image and velocity,
     * and sets its initial position.
     *
     * @param initialXPos the initial X-coordinate of the projectile.
     * @param initialYPos the initial Y-coordinate of the projectile.
     */
    public EnemyProjectile(double initialXPos, double initialYPos) {
        super(IMAGE_NAME, IMAGE_HEIGHT, initialXPos, initialYPos);
    }

    /**
     * Updates the position of the enemy projectile, moving it horizontally
     * at a constant velocity.
     */
    @Override
    public void updatePosition() {
        moveHorizontally(HORIZONTAL_VELOCITY);
    }

    /**
     * Updates the state of the enemy projectile. This method ensures
     * the position update logic is executed.
     */
    @Override
    public void updateActor() {
        updatePosition();
    }

}

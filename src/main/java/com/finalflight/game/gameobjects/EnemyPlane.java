package com.finalflight.game.gameobjects;

import com.finalflight.game.audio.SoundEffectPlayer;

/**
 * The {@code EnemyPlane} class represents a basic enemy fighter plane in the game.
 * It can move horizontally and fire projectiles at random intervals.
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/gameobjects/EnemyPlane.java</p>
 */
public class EnemyPlane extends FighterPlane {

    private static final String ENEMY_FIRE_SOUND = "/com/finalflight/game/audio/enemyfire.wav";
    private static final String IMAGE_NAME = "enemyplane.png";
    private static final int IMAGE_HEIGHT = 40;
    private static final int HORIZONTAL_VELOCITY = -6;
    private static final double PROJECTILE_X_POSITION_OFFSET = -40;
    private static final double PROJECTILE_Y_POSITION_OFFSET = 18;
    private static final int INITIAL_HEALTH = 3;
    private static final double FIRE_RATE = .01;
    private static final SoundEffectPlayer fireSound = new SoundEffectPlayer(ENEMY_FIRE_SOUND);

    static {
        fireSound.setVolume(0.07);
    }

    /**
     * Constructs an {@code EnemyPlane} with the specified initial position.
     *
     * @param initialXPos the initial X position.
     * @param initialYPos the initial Y position.
     */
    public EnemyPlane(double initialXPos, double initialYPos) {
        super(IMAGE_NAME, IMAGE_HEIGHT, initialXPos, initialYPos, INITIAL_HEALTH);
    }

    /**
     * Updates the position of the enemy plane, moving it horizontally.
     */
    @Override
    public void updatePosition() {
        moveHorizontally(HORIZONTAL_VELOCITY);
    }

    /**
     * Fires a projectile at random intervals based on the fire rate.
     *
     * @return an {@code EnemyProjectile} if fired, otherwise {@code null}.
     */
    @Override
    public DestructibleGameObject fireProjectile() {
        if (randomSupplier.get() < FIRE_RATE) {
            double projectileXPosition = getProjectileXPosition(PROJECTILE_X_POSITION_OFFSET);
            double projectileYPosition = getProjectileYPosition(PROJECTILE_Y_POSITION_OFFSET);
            fireSound.playSound();
            return new EnemyProjectile(projectileXPosition, projectileYPosition);
        }
        return null;
    }

    /**
     * Updates the state of the enemy plane. This method ensures
     * the position update logic is executed.
     */
    @Override
    public void updateActor() {
        updatePosition();
    }

}

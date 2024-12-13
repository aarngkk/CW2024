/**
 * The {@code AdvancedEnemyPlane} class represents a more challenging enemy plane in the game.
 * It extends {@link FighterPlane} and introduces faster movement, higher health, and a higher firing rate
 * compared to a regular enemy plane.
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/gameobjects/AdvancedEnemyPlane.java">AdvancedEnemyPlane.java</a></p>
 */
package com.finalflight.game.gameobjects;

import com.finalflight.game.audio.SoundEffectPlayer;

public class AdvancedEnemyPlane extends FighterPlane {

    private static final String IMAGE_NAME = "advancedenemyplane.png";
    private static final int IMAGE_HEIGHT = 50;
    private static final int HORIZONTAL_VELOCITY = -8;
    private static final double PROJECTILE_X_POSITION_OFFSET = -50;
    private static final double PROJECTILE_Y_POSITION_OFFSET = 20;
    private static final int INITIAL_HEALTH = 6;
    private static final double FIRE_RATE = 0.02;
    private static final String FIRE_SOUND = "/com/finalflight/game/audio/advancedenemyfire.wav";
    private static final SoundEffectPlayer fireSound = new SoundEffectPlayer(FIRE_SOUND);

    static {
        fireSound.setVolume(0.1);
    }

    /**
     * Constructs an {@code AdvancedEnemyPlane} with a specified initial position.
     *
     * @param initialXPos the initial X position of the plane.
     * @param initialYPos the initial Y position of the plane.
     */
    public AdvancedEnemyPlane(double initialXPos, double initialYPos) {
        super(IMAGE_NAME, IMAGE_HEIGHT, initialXPos, initialYPos, INITIAL_HEALTH);
    }

    /**
     * Updates the position of the plane by moving it horizontally at a faster speed.
     */
    @Override
    public void updatePosition() {
        moveHorizontally(HORIZONTAL_VELOCITY); // Faster movement
    }

    /**
     * Fires a projectile with a chance determined by the plane's firing rate.
     *
     * @return a new {@link EnemyProjectile} if the firing condition is met; otherwise, {@code null}.
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
     * Updates the actor's state, including position updates.
     */
    @Override
    public void updateActor() {
        updatePosition();
    }
}

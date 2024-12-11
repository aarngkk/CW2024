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

    public AdvancedEnemyPlane(double initialXPos, double initialYPos) {
        super(IMAGE_NAME, IMAGE_HEIGHT, initialXPos, initialYPos, INITIAL_HEALTH);
    }

    @Override
    public void updatePosition() {
        moveHorizontally(HORIZONTAL_VELOCITY); // Faster movement
    }

    @Override
    public ActiveActorDestructible fireProjectile() {
        if (randomSupplier.get() < FIRE_RATE) {
            double projectileXPosition = getProjectileXPosition(PROJECTILE_X_POSITION_OFFSET);
            double projectileYPosition = getProjectileYPosition(PROJECTILE_Y_POSITION_OFFSET);
            fireSound.playSound();
            return new EnemyProjectile(projectileXPosition, projectileYPosition);
        }
        return null;
    }

    @Override
    public void updateActor() {
        updatePosition();
    }
}

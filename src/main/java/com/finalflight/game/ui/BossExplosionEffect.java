/**
 * The {@code BossExplosionEffect} class represents the visual and audio effects
 * of a boss explosion in the game. It extends {@link ImageView} to display a GIF animation
 * of the explosion and plays a sound effect when triggered.
 *
 * <p>The explosion is centered around the boss's position with predefined offsets.</p>
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/ui/BossExplosionEffect.java">BossExplosionEffect.java</a></p>
 */
package com.finalflight.game.ui;

import com.finalflight.game.audio.SoundEffectPlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class BossExplosionEffect extends ImageView {

    private static final String BOSS_EXPLOSION_GIF = "/com/finalflight/game/images/bossexplosion.gif";
    private static final String BOSS_EXPLOSION_SOUND = "/com/finalflight/game/audio/bossexplosion.mp3";
    private static final int EXPLOSION_SIZE = 800;
    private static final double BOSS_EXPLOSION_X_OFFSET = -200;
    private static final double BOSS_EXPLOSION_Y_OFFSET = -325;
    private final SoundEffectPlayer bossExplodeSound;

    /**
     * Constructs a {@code BossExplosionEffect} at the specified position.
     *
     * @param bossXPosition the X-coordinate of the boss.
     * @param bossYPosition the Y-coordinate of the boss.
     */
    public BossExplosionEffect(double bossXPosition, double bossYPosition) {
        this.setLayoutX(bossXPosition + BOSS_EXPLOSION_X_OFFSET);
        this.setLayoutY(bossYPosition + BOSS_EXPLOSION_Y_OFFSET);
        this.setImage(new Image(getClass().getResource(BOSS_EXPLOSION_GIF).toExternalForm()));
        this.setFitWidth(EXPLOSION_SIZE);
        this.setPreserveRatio(true);
        bossExplodeSound = new SoundEffectPlayer(BOSS_EXPLOSION_SOUND);
        bossExplodeSound.setVolume(0.7);
    }

    /**
     * Plays the explosion sound effect.
     */
    public void playExplosionSound() {
        if (bossExplodeSound != null) {
            bossExplodeSound.playSound();
        }
    }

}

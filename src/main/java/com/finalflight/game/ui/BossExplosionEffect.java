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

    public BossExplosionEffect(double bossXPosition, double bossYPosition) {
        this.setLayoutX(bossXPosition + BOSS_EXPLOSION_X_OFFSET);
        this.setLayoutY(bossYPosition + BOSS_EXPLOSION_Y_OFFSET);
        this.setImage(new Image(getClass().getResource(BOSS_EXPLOSION_GIF).toExternalForm()));
        this.setFitWidth(EXPLOSION_SIZE);
        this.setPreserveRatio(true);
        bossExplodeSound = new SoundEffectPlayer(BOSS_EXPLOSION_SOUND);
        bossExplodeSound.setVolume(0.7);
    }

    public void playExplosionSound() {
        if (bossExplodeSound != null) {
            bossExplodeSound.playSound();
        }
    }

}

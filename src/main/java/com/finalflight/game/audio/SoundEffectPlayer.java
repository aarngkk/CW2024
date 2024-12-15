package com.finalflight.game.audio;

import javafx.scene.media.AudioClip;

/**
 * The {@code SoundEffectPlayer} class is responsible for playing sound effects
 * in the game. It uses JavaFX's {@link AudioClip} to handle audio playback.
 * This class provides methods to play, stop, adjust volume, and loop sound effects.
 *
 * <p>This class is designed to manage individual sound effects and provides
 * straightforward control over their behavior.</p>
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/audio/SoundEffectPlayer.java</p>
 */
public class SoundEffectPlayer {

    private final AudioClip audioClip;

    /**
     * Creates a new instance of {@code SoundEffectPlayer} for the specified sound file.
     *
     * @param soundFilePath the path to the sound file to be played.
     *                      The file should be accessible as a resource.
     */
    public SoundEffectPlayer(String soundFilePath) {
        this.audioClip = new AudioClip(getClass().getResource(soundFilePath).toExternalForm());
    }

    /**
     * Plays the sound effect. If the sound effect is already playing,
     * it will play from the beginning.
     */
    public void playSound() {
        audioClip.play();
    }

    /**
     * Sets the volume of the sound effect.
     *
     * @param volume the volume level as a double between 0.0 (mute) and 1.0 (maximum).
     */
    public void setVolume(double volume) {
        audioClip.setVolume(volume);
    }

    /**
     * Stops the currently playing sound effect. If the sound is not playing,
     * this method has no effect.
     */
    public void stopSound() {
        audioClip.stop();
    }

    /**
     * Sets whether the sound effect should loop indefinitely or play only once.
     *
     * @param isLooping {@code true} to loop the sound effect indefinitely,
     *                  {@code false} to play it only once.
     */
    public void setLooping(boolean isLooping) {
        audioClip.setCycleCount(isLooping ? AudioClip.INDEFINITE : 1);
    }

}


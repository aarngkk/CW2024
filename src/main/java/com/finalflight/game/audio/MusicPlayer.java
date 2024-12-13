/**
 * MusicPlayer is a singleton class responsible for managing music playback
 * in the game. It handles playing, pausing, resuming, and fading music in
 * and out. This class ensures that only one instance of the MusicPlayer is
 * created at a time.
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/audio/MusicPlayer.java">MusicPlayer.java</a></p>
 */
package com.finalflight.game.audio;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicPlayer {

    private static MusicPlayer instance;
    private final MediaPlayer mediaPlayer;
    private final DoubleProperty volumeProperty = new SimpleDoubleProperty(0.2);

    /**
     * Private constructor to initialize the MusicPlayer with a specific music file.
     *
     * @param musicFile the path to the music file to be played.
     */
    private MusicPlayer(String musicFile) {
        Media media = new Media(getClass().getResource(musicFile).toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.volumeProperty().bind(volumeProperty);
    }

    /**
     * Retrieves the singleton instance of MusicPlayer, creating a new instance if
     * necessary or if the requested music file differs from the currently playing music.
     *
     * @param musicFile the path to the music file to be played.
     * @return the singleton instance of MusicPlayer.
     */
    public static MusicPlayer getInstance(String musicFile) {
        if (instance == null || !instance.isSameMusic(musicFile)) {
            if (instance != null) {
                instance.stopMusic(); // Stop the previous music before creating a new instance
            }
            instance = new MusicPlayer(musicFile);
        }
        return instance;
    }

    /**
     * Checks if the current music file matches the provided music file.
     *
     * @param musicFile the path to the music file to check.
     * @return true if the provided music file matches the current one; false otherwise.
     */
    private boolean isSameMusic(String musicFile) {
        return mediaPlayer != null &&
                mediaPlayer.getMedia().getSource().equals(getClass().getResource(musicFile).toExternalForm());
    }

    /**
     * Plays the music. If looping is enabled, the music will repeat indefinitely.
     *
     * @param shouldLoop true to loop the music indefinitely; false otherwise.
     */
    public void playMusic(boolean shouldLoop) {
        if (mediaPlayer != null && mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            mediaPlayer.setCycleCount(shouldLoop ? MediaPlayer.INDEFINITE : 1);
            mediaPlayer.play();
        }
    }

    /**
     * Pauses the currently playing music.
     */
    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        }
    }

    /**
     * Resumes the music if it is currently paused.
     */
    public void resumeMusic() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaPlayer.play();
        }
    }

    /**
     * Stops the currently playing music.
     */
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * Gets the current volume of the music.
     *
     * @return the current volume as a double between 0.0 and 1.0.
     */
    public double getVolume() {
        return volumeProperty.get();
    }

    /**
     * Sets the volume of the music.
     *
     * @param volume a double between 0.0 and 1.0 representing the desired volume level.
     */
    public void setVolume(double volume) {
        volumeProperty.set(volume);
    }

    /**
     * Fades out the music volume over a specified duration.
     *
     * @param durationSeconds the duration of the fade-out in seconds.
     * @param targetVolume the target volume to reach at the end of the fade-out.
     * @param onFinished a callback to be executed when the fade-out completes.
     */
    public void fadeOutMusic(double durationSeconds, double targetVolume, Runnable onFinished) {
        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(volumeProperty, getVolume())),
                new KeyFrame(Duration.seconds(durationSeconds), new KeyValue(volumeProperty, targetVolume))
        );
        fadeOut.setOnFinished(event -> {
            if (onFinished != null) onFinished.run();
        });
        fadeOut.play();
    }

    /**
     * Fades in the music volume over a specified duration.
     *
     * @param durationSeconds the duration of the fade-in in seconds.
     * @param targetVolume the target volume to reach at the end of the fade-in.
     */
    public void fadeInMusic(double durationSeconds, double targetVolume) {
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(volumeProperty, getVolume())),
                new KeyFrame(Duration.seconds(durationSeconds), new KeyValue(volumeProperty, targetVolume))
        );
        fadeIn.play();
    }

    /**
     * Gets the underlying MediaPlayer instance.
     *
     * @return the MediaPlayer instance being used.
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * Gets the current status of the MediaPlayer.
     *
     * @return the status of the MediaPlayer, or null if no media is loaded.
     */
    public MediaPlayer.Status getMediaPlayerStatus() {
        return mediaPlayer != null ? mediaPlayer.getStatus() : null;
    }

}

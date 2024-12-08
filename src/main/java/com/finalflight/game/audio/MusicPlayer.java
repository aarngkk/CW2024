package com.finalflight.game.audio;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class MusicPlayer {

    private static MusicPlayer instance;
    private MediaPlayer mediaPlayer;
    private DoubleProperty volumeProperty = new SimpleDoubleProperty(0.2);

    private MusicPlayer(String musicFile) {
        Media media = new Media(getClass().getResource(musicFile).toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.volumeProperty().bind(volumeProperty);
    }

    public static MusicPlayer getInstance(String musicFile) {
        if (instance == null || !instance.isSameMusic(musicFile)) {
            if (instance != null) {
                instance.stopMusic(); // Stop the previous music before creating a new instance
            }
            instance = new MusicPlayer(musicFile);
        }
        return instance;
    }

    private boolean isSameMusic(String musicFile) {
        return mediaPlayer != null &&
                mediaPlayer.getMedia().getSource().equals(getClass().getResource(musicFile).toExternalForm());
    }

    public void playMusic(boolean shouldLoop) {
        if (mediaPlayer != null && mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            mediaPlayer.setCycleCount(shouldLoop ? MediaPlayer.INDEFINITE : 1);
            mediaPlayer.play();
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaPlayer.play();
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public DoubleProperty volumeProperty() {
        return volumeProperty;
    }

    public double getVolume() {
        return volumeProperty.get();
    }

    public void setVolume(double volume) {
        volumeProperty.set(volume);
    }

    // Fade out music
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

    // Fade in music
    public void fadeInMusic(double durationSeconds, double targetVolume) {
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(volumeProperty, getVolume())),
                new KeyFrame(Duration.seconds(durationSeconds), new KeyValue(volumeProperty, targetVolume))
        );
        fadeIn.play();
    }

}

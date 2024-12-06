package com.example.demo;

import javafx.scene.media.AudioClip;

public class SoundEffectPlayer {

    private AudioClip audioClip;

    public SoundEffectPlayer(String soundFilePath) {
        this.audioClip = new AudioClip(getClass().getResource(soundFilePath).toExternalForm());
    }

    public void playSound() {
        audioClip.play();
    }

    public void setVolume(double volume) {
        audioClip.setVolume(volume);
    }

    public void stopSound() {
        audioClip.stop();
    }

    public void setLooping(boolean isLooping) {
        audioClip.setCycleCount(isLooping ? AudioClip.INDEFINITE : 1);
    }

}


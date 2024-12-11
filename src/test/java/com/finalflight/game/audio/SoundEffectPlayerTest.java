package com.finalflight.game.audio;

import javafx.scene.media.AudioClip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SoundEffectPlayerTest {

    private SoundEffectPlayer soundEffectPlayer;
    private static final String TEST_SOUND_FILE = "/com/finalflight/game/audio/userfire.wav";

    @BeforeEach
    void setUp() {
        // Initialize SoundEffectPlayer with a test sound file
        soundEffectPlayer = new SoundEffectPlayer(TEST_SOUND_FILE);
    }

    @Test
    void testPlaySound() {
        // Play the sound
        soundEffectPlayer.playSound();

        // Verify that the AudioClip is playing
        AudioClip audioClip = getAudioClip(soundEffectPlayer);
        assertNotNull(audioClip, "AudioClip instance should not be null.");
        assertTrue(audioClip.isPlaying(), "AudioClip should be playing.");
    }

    @Test
    void testSetVolume() {
        // Set the volume
        double volume = 0.5;
        soundEffectPlayer.setVolume(volume);

        // Verify the volume level
        AudioClip audioClip = getAudioClip(soundEffectPlayer);
        assertNotNull(audioClip, "AudioClip instance should not be null.");
        assertEquals(volume, audioClip.getVolume(), "Volume should be set correctly.");
    }

    @Test
    void testStopSound() {
        // Play and then stop the sound
        soundEffectPlayer.playSound();
        soundEffectPlayer.stopSound();

        // Verify that the AudioClip is not playing
        AudioClip audioClip = getAudioClip(soundEffectPlayer);
        assertNotNull(audioClip, "AudioClip instance should not be null.");
        assertFalse(audioClip.isPlaying(), "AudioClip should not be playing.");
    }

    @Test
    void testSetLooping() {
        // Set looping to true
        soundEffectPlayer.setLooping(true);

        // Verify the cycle count
        AudioClip audioClip = getAudioClip(soundEffectPlayer);
        assertNotNull(audioClip, "AudioClip instance should not be null.");
        assertEquals(AudioClip.INDEFINITE, audioClip.getCycleCount(), "AudioClip should loop indefinitely.");

        // Set looping to false
        soundEffectPlayer.setLooping(false);

        // Verify the cycle count
        assertEquals(1, audioClip.getCycleCount(), "AudioClip should play only once.");
    }

    // Utility method to access the private AudioClip field
    private AudioClip getAudioClip(SoundEffectPlayer player) {
        try {
            var field = SoundEffectPlayer.class.getDeclaredField("audioClip");
            field.setAccessible(true);
            return (AudioClip) field.get(player);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access AudioClip field: " + e.getMessage());
            return null;
        }
    }
}

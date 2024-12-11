package com.finalflight.game.audio;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MusicPlayerTest {

    private static final String TEST_MUSIC_FILE = "/com/finalflight/game/audio/levelmusic.mp3";
    private MusicPlayer musicPlayer;

    @BeforeAll
    static void setupJavaFxToolkit() {
        // Initialize JavaFX Toolkit to avoid errors
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        musicPlayer = MusicPlayer.getInstance(TEST_MUSIC_FILE);
    }

    private void waitForMediaPlayerStatus(MediaPlayer mediaPlayer, MediaPlayer.Status expectedStatus) {
        CountDownLatch latch = new CountDownLatch(1);
        ChangeListener<MediaPlayer.Status> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends MediaPlayer.Status> observable, MediaPlayer.Status oldValue, MediaPlayer.Status newValue) {
                if (newValue == expectedStatus) {
                    latch.countDown();
                }
            }
        };
        mediaPlayer.statusProperty().addListener(listener);

        try {
            // Wait for a maximum of 5 seconds
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("MediaPlayer status transition timed out.");
        } finally {
            mediaPlayer.statusProperty().removeListener(listener);
        }
    }

    @Test
    void testSingletonBehavior() {
        MusicPlayer anotherInstance = MusicPlayer.getInstance(TEST_MUSIC_FILE);
        assertSame(musicPlayer, anotherInstance, "MusicPlayer should follow the singleton pattern for the same music file.");
    }

    @Test
    void testPlayMusic() {
        musicPlayer.playMusic(false);
        waitForMediaPlayerStatus(musicPlayer.getMediaPlayer(), MediaPlayer.Status.PLAYING);
        assertEquals(MediaPlayer.Status.PLAYING, musicPlayer.getMediaPlayerStatus(), "MediaPlayer should be in PLAYING status.");
    }

    @Test
    void testPauseMusic() {
        musicPlayer.playMusic(false);
        waitForMediaPlayerStatus(musicPlayer.getMediaPlayer(), MediaPlayer.Status.PLAYING);

        musicPlayer.pauseMusic();
        waitForMediaPlayerStatus(musicPlayer.getMediaPlayer(), MediaPlayer.Status.PAUSED);
        assertEquals(MediaPlayer.Status.PAUSED, musicPlayer.getMediaPlayerStatus(), "MediaPlayer should be in PAUSED status.");
    }


    @Test
    void testResumeMusic() {
        musicPlayer.playMusic(false);
        waitForMediaPlayerStatus(musicPlayer.getMediaPlayer(), MediaPlayer.Status.PLAYING);

        musicPlayer.pauseMusic();
        waitForMediaPlayerStatus(musicPlayer.getMediaPlayer(), MediaPlayer.Status.PAUSED);

        musicPlayer.resumeMusic();
        waitForMediaPlayerStatus(musicPlayer.getMediaPlayer(), MediaPlayer.Status.PLAYING);
        assertEquals(MediaPlayer.Status.PLAYING, musicPlayer.getMediaPlayerStatus(), "MediaPlayer should resume to PLAYING status.");
    }

    @Test
    void testStopMusic() {
        musicPlayer.playMusic(false);
        waitForMediaPlayerStatus(musicPlayer.getMediaPlayer(), MediaPlayer.Status.PLAYING);

        musicPlayer.stopMusic();
        waitForMediaPlayerStatus(musicPlayer.getMediaPlayer(), MediaPlayer.Status.STOPPED);
        assertEquals(MediaPlayer.Status.STOPPED, musicPlayer.getMediaPlayerStatus(), "MediaPlayer should be in STOPPED status.");
    }

    @Test
    void testVolumeSetAndGet() {
        double newVolume = 0.5;
        musicPlayer.setVolume(newVolume);
        assertEquals(newVolume, musicPlayer.getVolume(), 0.01, "Volume should be updated correctly.");
    }

    @Test
    void testFadeOutMusic() {
        double targetVolume = 0.0;

        musicPlayer.fadeOutMusic(2.0, targetVolume, () -> assertEquals(targetVolume, musicPlayer.getVolume(), 0.01, "Volume should fade out to target value."));

        // Simulate waiting for the fade-out duration
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            fail("Fade-out simulation interrupted.");
        }
    }

    @Test
    void testFadeInMusic() {
        musicPlayer.setVolume(0.0);
        double targetVolume = 0.8;

        musicPlayer.fadeInMusic(2.0, targetVolume);

        // Simulate waiting for the fade-in duration
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            fail("Fade-in simulation interrupted.");
        }

        assertEquals(targetVolume, musicPlayer.getVolume(), 0.01, "Volume should fade in to target value.");
    }
}

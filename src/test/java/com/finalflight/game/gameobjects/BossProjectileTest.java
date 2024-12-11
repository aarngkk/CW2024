package com.finalflight.game.gameobjects;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BossProjectileTest {

    private BossProjectile testBossProjectile;
    private static final double INITIAL_X_POS = 1000.0;
    private static final double INITIAL_Y_POS = 200.0;
    private static final int HORIZONTAL_VELOCITY = -15;

    @BeforeAll
    static void setupJavaFxToolkit() {
        // Initialize JavaFX Toolkit to avoid errors
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        // Create a new instance of BossProjectile with the required initial position
        testBossProjectile = new BossProjectile(INITIAL_Y_POS);
    }

    @Test
    void testInitialization() {
        assertEquals(INITIAL_X_POS, testBossProjectile.getLayoutX(), 0.01, "Initial X position is incorrect.");
        assertEquals(INITIAL_Y_POS, testBossProjectile.getLayoutY(), 0.01, "Initial Y position is incorrect.");
        assertFalse(testBossProjectile.isDestroyed(), "BossProjectile should not be destroyed upon initialization.");
    }

    @Test
    void testUpdatePosition() {
        double initialTranslateX = testBossProjectile.getTranslateX();
        double initialTranslateY = testBossProjectile.getTranslateY();

        testBossProjectile.updatePosition();

        assertEquals(initialTranslateX + HORIZONTAL_VELOCITY, testBossProjectile.getTranslateX(), 0.01, "X position should update based on horizontal velocity.");
        assertEquals(initialTranslateY, testBossProjectile.getTranslateY(), 0.01, "Y position should remain unchanged.");
    }

    @Test
    void testUpdateActor() {
        double initialTranslateX = testBossProjectile.getTranslateX();

        testBossProjectile.updateActor();

        assertEquals(initialTranslateX + HORIZONTAL_VELOCITY, testBossProjectile.getTranslateX(), 0.01, "updateActor should update the X position.");
    }

    @Test
    void testTakeDamageDestroysBossProjectile() {
        assertFalse(testBossProjectile.isDestroyed(), "BossProjectile should not be destroyed initially.");

        testBossProjectile.takeDamage();

        assertTrue(testBossProjectile.isDestroyed(), "BossProjectile should be destroyed after taking damage.");
    }
}

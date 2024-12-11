package com.finalflight.game.gameobjects;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnemyProjectileTest {

    private EnemyProjectile testEnemyProjectile;
    private static final String IMAGE_NAME = "enemyfire.png";
    private static final int IMAGE_HEIGHT = 16;
    private static final double INITIAL_X_POS = 100.0;
    private static final double INITIAL_Y_POS = 50.0;
    private static final int HORIZONTAL_VELOCITY = -10;

    @BeforeAll
    static void setupJavaFxToolkit() {
        // Initialize JavaFX Toolkit to avoid errors
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        // Create a new instance of EnemyProjectile with the required initial position
        testEnemyProjectile = new EnemyProjectile(INITIAL_X_POS, INITIAL_Y_POS);
    }

    @Test
    void testInitialization() {
        assertEquals(INITIAL_X_POS, testEnemyProjectile.getLayoutX(), 0.01, "Initial X position is incorrect.");
        assertEquals(INITIAL_Y_POS, testEnemyProjectile.getLayoutY(), 0.01, "Initial Y position is incorrect.");
        assertFalse(testEnemyProjectile.isDestroyed(), "EnemyProjectile should not be destroyed upon initialization.");
    }

    @Test
    void testUpdatePosition() {
        double initialTranslateX = testEnemyProjectile.getTranslateX();
        double initialTranslateY = testEnemyProjectile.getTranslateY();

        testEnemyProjectile.updatePosition();

        assertEquals(initialTranslateX + HORIZONTAL_VELOCITY, testEnemyProjectile.getTranslateX(), 0.01, "X position should update based on horizontal velocity.");
        assertEquals(initialTranslateY, testEnemyProjectile.getTranslateY(), 0.01, "Y position should remain unchanged.");
    }

    @Test
    void testUpdateActor() {
        double initialTranslateX = testEnemyProjectile.getTranslateX();

        testEnemyProjectile.updateActor();

        assertEquals(initialTranslateX + HORIZONTAL_VELOCITY, testEnemyProjectile.getTranslateX(), 0.01, "updateActor should update the X position.");
    }


    @Test
    void testTakeDamageDestroysEnemyProjectile() {
        assertFalse(testEnemyProjectile.isDestroyed(), "EnemyProjectile should not be destroyed initially.");

        testEnemyProjectile.takeDamage();

        assertTrue(testEnemyProjectile.isDestroyed(), "EnemyProjectile should be destroyed after taking damage.");
    }
}

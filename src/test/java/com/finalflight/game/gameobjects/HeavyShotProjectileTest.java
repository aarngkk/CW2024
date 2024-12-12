package com.finalflight.game.gameobjects;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeavyShotProjectileTest {

    private HeavyShotProjectile testProjectile;
    private static final double INITIAL_X_POS = 100.0;
    private static final double INITIAL_Y_POS = 50.0;
    private static final double EXPECTED_HORIZONTAL_VELOCITY = 8.0;
    private static final double EXPECTED_VERTICAL_VELOCITY = 10.0;
    private static final int EXPECTED_HEALTH = 6;

    @BeforeAll
    static void setupJavaFxToolkit() {
        // Initialize JavaFX Toolkit to avoid errors during testing
        Platform.startup(() -> {
        });
    }

    @BeforeEach
    void setUp() {
        testProjectile = new HeavyShotProjectile(INITIAL_X_POS, INITIAL_Y_POS);
    }

    @Test
    void testInitialization() {
        assertEquals(INITIAL_X_POS, testProjectile.getLayoutX(), 0.01, "Initial X position is incorrect.");
        assertEquals(INITIAL_Y_POS, testProjectile.getLayoutY(), 0.01, "Initial Y position is incorrect.");
        assertFalse(testProjectile.isDestroyed(), "UserHeavyProjectile should not be destroyed upon initialization.");
        assertEquals(EXPECTED_HEALTH, testProjectile.getHealth(), "Initial health is incorrect.");
    }

    @Test
    void testUpdatePosition() {
        double initialTranslateX = testProjectile.getTranslateX();
        double initialTranslateY = testProjectile.getTranslateY();

        testProjectile.updatePosition();

        assertEquals(initialTranslateX + EXPECTED_HORIZONTAL_VELOCITY, testProjectile.getTranslateX(), 0.01, "X position should update based on horizontal velocity.");
        assertEquals(initialTranslateY + EXPECTED_VERTICAL_VELOCITY, testProjectile.getTranslateY(), 0.01, "Y position should update based on vertical velocity.");
    }

    @Test
    void testUpdateActor() {
        double initialTranslateX = testProjectile.getTranslateX();
        double initialTranslateY = testProjectile.getTranslateY();

        testProjectile.updateActor();

        assertEquals(initialTranslateX + EXPECTED_HORIZONTAL_VELOCITY, testProjectile.getTranslateX(), 0.01, "updateActor should update the X position.");
        assertEquals(initialTranslateY + EXPECTED_VERTICAL_VELOCITY, testProjectile.getTranslateY(), 0.01, "updateActor should update the Y position.");
    }

    @Test
    void testTakeDamage() {
        assertEquals(EXPECTED_HEALTH, testProjectile.getHealth(), "Initial health is incorrect.");

        testProjectile.takeDamage();
        assertEquals(EXPECTED_HEALTH - 1, testProjectile.getHealth(), "Health should decrease by 1 after taking damage.");
        assertFalse(testProjectile.isDestroyed(), "Projectile should not be destroyed after a single hit.");

        // Inflict damage until health reaches 0
        for (int i = 1; i < EXPECTED_HEALTH; i++) {
            testProjectile.takeDamage();
        }

        assertEquals(0, testProjectile.getHealth(), "Health should be 0 after taking maximum damage.");
        assertTrue(testProjectile.isDestroyed(), "Projectile should be destroyed when health reaches 0.");
    }

    @Test
    void testTakeDamageDoesNotGoNegative() {
        // Inflict damage until health reaches 0
        for (int i = 0; i < EXPECTED_HEALTH; i++) {
            testProjectile.takeDamage();
        }

        assertEquals(0, testProjectile.getHealth(), "Health should not go below 0.");

        // Additional damage should not decrease health further
        testProjectile.takeDamage();
        assertEquals(0, testProjectile.getHealth(), "Health should remain at 0 after being destroyed.");
    }
}

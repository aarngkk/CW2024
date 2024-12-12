package com.finalflight.game.gameobjects;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdvancedEnemyPlaneTest {

    private AdvancedEnemyPlane advancedEnemyPlane;
    private static final double INITIAL_X_POS = 200.0;
    private static final double INITIAL_Y_POS = 100.0;
    private static final int EXPECTED_HEALTH = 6;
    private static final int EXPECTED_HORIZONTAL_VELOCITY = -8;

    @BeforeAll
    static void setupJavaFxToolkit() {
        Platform.startup(() -> {
        });
    }

    @BeforeEach
    void setUp() {
        advancedEnemyPlane = new AdvancedEnemyPlane(INITIAL_X_POS, INITIAL_Y_POS);
    }

    @Test
    void testInitialization() {
        assertEquals(INITIAL_X_POS, advancedEnemyPlane.getLayoutX(), 0.01, "Initial X position is incorrect.");
        assertEquals(INITIAL_Y_POS, advancedEnemyPlane.getLayoutY(), 0.01, "Initial Y position is incorrect.");
        assertEquals(EXPECTED_HEALTH, advancedEnemyPlane.getHealth(), "Health value is incorrect.");
    }

    @Test
    void testUpdatePosition() {
        double initialX = advancedEnemyPlane.getTranslateX();

        advancedEnemyPlane.updatePosition();

        assertEquals(initialX + EXPECTED_HORIZONTAL_VELOCITY, advancedEnemyPlane.getTranslateX(), 0.01, "X position should update based on velocity.");
    }

    @Test
    void testFireProjectileReturnsProjectileWhenRandomConditionMet() {
        // Mock randomSupplier to always trigger firing
        advancedEnemyPlane.setRandomSupplier(() -> 0.0); // Ensures fireProjectile condition is always true

        DestructibleGameObject projectile = advancedEnemyPlane.fireProjectile();

        assertNotNull(projectile, "fireProjectile should return a projectile when the fire condition is met.");
        assertEquals(EnemyProjectile.class, projectile.getClass(), "fireProjectile should return an EnemyProjectile.");
    }

    @Test
    void testFireProjectileReturnsNullWhenRandomConditionNotMet() {
        // Mock randomSupplier to never trigger firing
        advancedEnemyPlane.setRandomSupplier(() -> 1.0); // Ensures fireProjectile condition is always false

        DestructibleGameObject projectile = advancedEnemyPlane.fireProjectile();

        assertNull(projectile, "fireProjectile should return null when the fire condition is not met.");
    }

    @Test
    void testUpdateActor() {
        double initialX = advancedEnemyPlane.getTranslateX();

        advancedEnemyPlane.updateActor();

        assertEquals(initialX + EXPECTED_HORIZONTAL_VELOCITY, advancedEnemyPlane.getTranslateX(), 0.01, "updateActor should update X position.");
    }
}

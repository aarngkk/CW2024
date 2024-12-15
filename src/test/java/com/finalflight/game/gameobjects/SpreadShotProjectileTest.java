package com.finalflight.game.gameobjects;

import com.finalflight.game.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpreadShotProjectileTest extends BaseTest {

    private SpreadShotProjectile testProjectile;
    private static final double INITIAL_X_POS = 100.0;
    private static final double INITIAL_Y_POS = 200.0;
    private static final double VELOCITY_X = 5.0;
    private static final double VELOCITY_Y = 3.0;
    private static final double RANGE = 300.0;

    @BeforeEach
    void setUp() {
        testProjectile = new SpreadShotProjectile(INITIAL_X_POS, INITIAL_Y_POS, VELOCITY_X, VELOCITY_Y);
    }

    @Test
    void testInitialization() {
        assertEquals(INITIAL_X_POS, testProjectile.getLayoutX(), 0.01, "Initial X position is incorrect.");
        assertEquals(INITIAL_Y_POS, testProjectile.getLayoutY(), 0.01, "Initial Y position is incorrect.");
        assertFalse(testProjectile.isDestroyed(), "UserSpreadProjectile should not be destroyed upon initialization.");
    }

    @Test
    void testUpdatePosition() {
        double initialX = testProjectile.getTranslateX();
        double initialY = testProjectile.getTranslateY();

        testProjectile.updatePosition();

        assertEquals(initialX + VELOCITY_X, testProjectile.getTranslateX(), 0.01, "X position should update based on horizontal velocity.");
        assertEquals(initialY + VELOCITY_Y, testProjectile.getTranslateY(), 0.01, "Y position should update based on vertical velocity.");
        assertFalse(testProjectile.isDestroyed(), "Projectile should not be destroyed within range.");
    }

    @Test
    void testRangeDestruction() {
        for (int i = 0; i < RANGE / Math.sqrt(VELOCITY_X * VELOCITY_X + VELOCITY_Y * VELOCITY_Y); i++) {
            testProjectile.updatePosition();
        }
        assertTrue(testProjectile.isDestroyed(), "Projectile should be destroyed after exceeding range.");
    }

    @Test
    void testUpdateActor() {
        double initialX = testProjectile.getTranslateX();
        double initialY = testProjectile.getTranslateY();

        testProjectile.updateActor();

        assertEquals(initialX + VELOCITY_X, testProjectile.getTranslateX(), 0.01, "updateActor should update X position.");
        assertEquals(initialY + VELOCITY_Y, testProjectile.getTranslateY(), 0.01, "updateActor should update Y position.");
    }
}

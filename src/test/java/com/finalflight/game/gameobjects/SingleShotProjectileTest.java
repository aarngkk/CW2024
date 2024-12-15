package com.finalflight.game.gameobjects;

import com.finalflight.game.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingleShotProjectileTest extends BaseTest {

    private SingleShotProjectile testProjectile;
    private static final String EXPECTED_IMAGE_NAME = "userfire.png";
    private static final int EXPECTED_IMAGE_HEIGHT = 8;
    private static final double INITIAL_X_POS = 200.0;
    private static final double INITIAL_Y_POS = 100.0;
    private static final int EXPECTED_HORIZONTAL_VELOCITY = 15;

    @BeforeEach
    void setUp() {
        // Create a new instance of UserSingleProjectile with the required initial position
        testProjectile = new SingleShotProjectile(INITIAL_X_POS, INITIAL_Y_POS);
    }

    @Test
    void testInitialization() {
        assertEquals(INITIAL_X_POS, testProjectile.getLayoutX(), 0.01, "Initial X position is incorrect.");
        assertEquals(INITIAL_Y_POS, testProjectile.getLayoutY(), 0.01, "Initial Y position is incorrect.");
        assertFalse(testProjectile.isDestroyed(), "UserSingleProjectile should not be destroyed upon initialization.");
        assertEquals(EXPECTED_IMAGE_NAME, testProjectile.getImageName(), "Image name is incorrect.");
        assertEquals(EXPECTED_IMAGE_HEIGHT, testProjectile.getImageHeight(), "Image height is incorrect.");
    }

    @Test
    void testUpdatePosition() {
        double initialTranslateX = testProjectile.getTranslateX();
        double initialTranslateY = testProjectile.getTranslateY();

        testProjectile.updatePosition();

        assertEquals(initialTranslateX + EXPECTED_HORIZONTAL_VELOCITY, testProjectile.getTranslateX(), 0.01, "X position should update based on horizontal velocity.");
        assertEquals(initialTranslateY, testProjectile.getTranslateY(), 0.01, "Y position should remain unchanged.");
    }

    @Test
    void testUpdateActor() {
        double initialTranslateX = testProjectile.getTranslateX();

        testProjectile.updateActor();

        assertEquals(initialTranslateX + EXPECTED_HORIZONTAL_VELOCITY, testProjectile.getTranslateX(), 0.01, "updateActor should update the X position.");
    }

    @Test
    void testTakeDamageDestroysProjectile() {
        assertFalse(testProjectile.isDestroyed(), "UserSingleProjectile should not be destroyed initially.");

        testProjectile.takeDamage();

        assertTrue(testProjectile.isDestroyed(), "UserSingleProjectile should be destroyed after taking damage.");
    }
}

package com.finalflight.game.gameobjects;

import com.finalflight.game.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractProjectileTest extends BaseTest {

    private AbstractProjectile testProjectile;
    private static final String IMAGE_NAME = "testImage.png";
    private static final int IMAGE_HEIGHT = 10;
    private static final double INITIAL_X_POS = 100.0;
    private static final double INITIAL_Y_POS = 50.0;

    @BeforeEach
    void setUp() {
        testProjectile = new TestProjectile(IMAGE_NAME, IMAGE_HEIGHT, INITIAL_X_POS, INITIAL_Y_POS, true);
    }

    @Test
    void testInitialization() {
        assertEquals(INITIAL_X_POS, testProjectile.getLayoutX(), 0.01);
        assertEquals(INITIAL_Y_POS, testProjectile.getLayoutY(), 0.01);
        assertFalse(testProjectile.isDestroyed(), "Projectile should not be destroyed upon initialization.");
    }

    @Test
    void testTakeDamageDestroysProjectile() {
        testProjectile.takeDamage();
        assertTrue(testProjectile.isDestroyed(), "Projectile should be destroyed after taking damage.");
    }

    @Test
    void testUpdatePosition() {
        double initialX = testProjectile.getLayoutX();
        double initialY = testProjectile.getLayoutY();
        testProjectile.updatePosition();
        assertEquals(initialX + 5, testProjectile.getLayoutX(), 0.01, "Projectile X position should have increased by 5.");
        assertEquals(initialY, testProjectile.getLayoutY(), 0.01, "Projectile Y position should remain unchanged.");
    }

    // A concrete subclass for testing purposes
    private static class TestProjectile extends AbstractProjectile {
        public TestProjectile(String imageName, int imageHeight, double initialXPos, double initialYPos, boolean skipImageLoading) {
            super(imageName, imageHeight, initialXPos, initialYPos, skipImageLoading);
        }

        @Override
        public void updatePosition() {
            // Simple movement logic for testing: move 5 units to the right
            setLayoutX(getLayoutX() + 5);
        }

        @Override
        public void updateActor() {
            updatePosition();
        }
    }
}

package com.finalflight.game.gameobjects;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FighterPlaneTest {

    private FighterPlane fighterPlane;
    private static final int INITIAL_HEALTH = 5;

    // Mock subclass for FighterPlane
    private static class MockFighterPlane extends FighterPlane {
        public MockFighterPlane(String imageName, int imageHeight, double initialXPos, double initialYPos, int health) {
            super(imageName, imageHeight, initialXPos, initialYPos, health, true);
        }

        @Override
        public DestructibleGameObject fireProjectile() {
            return new MockProjectile("projectile.png", 10, 0, 0, true); // Return a mock projectile
        }

        @Override
        public void updatePosition() {
            // Basic implementation for testing purposes
            setTranslateX(getTranslateX() + 1);
            setTranslateY(getTranslateY() + 1);
        }

        @Override
        public void updateActor() {
            updatePosition();
        }
    }

    // Mock subclass for Projectile
    private static class MockProjectile extends AbstractProjectile {
        public MockProjectile(String imageName, int imageHeight, double initialXPos, double initialYPos, boolean skipImageLoading) {
            super(imageName, imageHeight, initialXPos, initialYPos, skipImageLoading);
        }

        @Override
        public void updatePosition() {
            // Basic implementation for testing purposes
            setTranslateX(getTranslateX() + 1);
            setTranslateY(getTranslateY() + 1);
        }

        @Override
        public void updateActor() {
            updatePosition();
        }
    }

    @BeforeAll
    static void setupJavaFxToolkit() {
        // Initialize JavaFX Toolkit to avoid errors
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        fighterPlane = new MockFighterPlane("fighter.png", 50, 100, 100, INITIAL_HEALTH);
    }

    @Test
    void testInitialization() {
        assertEquals(INITIAL_HEALTH, fighterPlane.getHealth());
        assertEquals(INITIAL_HEALTH, fighterPlane.getMaxHealth());
    }

    @Test
    void testTakeDamage() {
        fighterPlane.takeDamage();
        assertEquals(INITIAL_HEALTH - 1, fighterPlane.getHealth());
    }

    @Test
    void testHealthCannotGoBelowZero() {
        for (int i = 0; i < 15; i++) {
            fighterPlane.takeDamage();
        }
        assertEquals(0, fighterPlane.getHealth());
    }

    @Test
    void testFlashEffectOnDamage() {
        // Ensure flash effect is applied
        fighterPlane.takeDamage();
        assertNotNull(fighterPlane.getEffect()); // Effect should be set
    }

    @Test
    void testDestroyOnHealthZero() {
        for (int i = 0; i < INITIAL_HEALTH; i++) {
            fighterPlane.takeDamage();
        }
        assertEquals(0, fighterPlane.getHealth());
        assertTrue(fighterPlane.isDestroyed());
    }

    @Test
    void testGetProjectilePosition() {
        double xOffset = 5.0;
        double yOffset = 10.0;

        double expectedX = fighterPlane.getLayoutX() + fighterPlane.getTranslateX() + xOffset;
        double expectedY = fighterPlane.getLayoutY() + fighterPlane.getTranslateY() + yOffset;

        assertEquals(expectedX, fighterPlane.getProjectileXPosition(xOffset), 0.01);
        assertEquals(expectedY, fighterPlane.getProjectileYPosition(yOffset), 0.01);
    }

    @Test
    void testFireProjectile() {
        DestructibleGameObject projectile = fighterPlane.fireProjectile();
        assertNotNull(projectile);
        assertInstanceOf(AbstractProjectile.class, projectile); // Verify the type of projectile
    }

    @Test
    void testHealthManagement() {
        int damage = 3;
        for (int i = 0; i < damage; i++) {
            fighterPlane.takeDamage();
        }
        assertEquals(INITIAL_HEALTH - damage, fighterPlane.getHealth());
        assertFalse(fighterPlane.getHealth() <= 0);
    }
}

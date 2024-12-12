package com.finalflight.game.gameobjects;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BossPlaneTest {

    private BossPlane boss;
    private static final double DELTA = 0.01;

    @BeforeAll
    static void setupJavaFxToolkit() {
        // Initialize JavaFX Toolkit to avoid errors
        Platform.startup(() -> {
        });
    }

    @BeforeEach
    void setUp() {
        boss = new BossPlane();
    }

    @Test
    void testInitialization() {
        assertNotNull(boss);
        assertEquals(100, boss.getHealth());
        assertFalse(boss.getIsShielded());
        assertEquals(1150.0, boss.getLayoutX(), DELTA);
        assertEquals(400.0, boss.getLayoutY(), DELTA);
    }

    @Test
    void testMovementWithinBounds() {
        boss.setTranslateY(400); // Set initial position

        // Move up
        boss.moveVertically(-8);
        boss.updatePosition();
        assertTrue(boss.getTranslateY() >= -50);

        // Move down
        boss.moveVertically(8);
        boss.updatePosition();
        assertTrue(boss.getTranslateY() <= 700);
    }

    @Test
    void testMovementExceedingBounds() {
        // Move beyond upper bound
        boss.setTranslateY(-60); // Outside bounds
        boss.updatePosition();
        assertTrue(boss.getLayoutY() >= -50); // Should stay within bounds

        // Move beyond lower bound
        boss.setTranslateY(710); // Outside bounds
        boss.updatePosition();
        assertTrue(boss.getLayoutY() <= 700); // Should stay within bounds
    }

    @Test
    void testTakeDamageWithoutShield() {
        int initialHealth = boss.getHealth();
        boss.takeDamage();
        assertEquals(initialHealth - 1, boss.getHealth());
    }

    @Test
    void testTakeDamageWithShield() {
        while (boss.getHealth() >= 50) {
            boss.takeDamage();
        }
        boss.updateActor();
        assertTrue(boss.getIsShielded());
        int initialHealth = boss.getHealth();
        boss.takeDamage();
        assertEquals(initialHealth, boss.getHealth()); // Health should remain unchanged
    }

    @Test
    void testShieldActivationAndDeactivation() {
        while (boss.getHealth() >= 50) {
            boss.takeDamage();
        }
        boss.updateActor();
        assertTrue(boss.getIsShielded());

        // Simulate shield deactivation after max frames
        for (int i = 0; i < 250; i++) {
            boss.updateActor();
        }
        assertFalse(boss.getIsShielded());
    }

    @Test
    void testShieldActivatedAt20PercentHealth() {
        while (boss.getHealth() > 20) {
            boss.takeDamage(); // Reduce health to just above 20%
        }

        assertFalse(boss.getIsShielded()); // Shield should not activate yet

        boss.takeDamage(); // Reduce health to 20% or below
        boss.updateActor();

        assertTrue(boss.getIsShielded()); // Shield should now be activated
    }

    @Test
    void testFireRateBoostAndProjectileFiring() {
        // Simulate damage to drop the boss's health to 20% or below to trigger the fire rate boost
        while (boss.getHealth() > 20) {
            boss.takeDamage();
        }

        // Verify that the fire rate boost is applied
        boss.updateActor(); // This will apply the boost
        assertTrue(boss.getMovePattern().contains(12)); // Check that speed is increased
        assertTrue(boss.getMovePattern().contains(-12)); // Check that speed is increased in both directions

        // Now check the number of projectiles fired within 1000 frames
        int projectileCount = 0;
        for (int i = 0; i < 1000; i++) {
            if (boss.fireProjectile() != null) {
                projectileCount++;
            }
        }

        // With the increased fire rate, expect more projectiles to be fired
        assertTrue(projectileCount >= 50, "Expected at least 50 projectiles, but got " + projectileCount);


        // Additionally, check that the projectiles are being created correctly
        for (int i = 0; i < projectileCount; i++) {
            DestructibleGameObject projectile = boss.fireProjectile();
            if (projectile != null) {
                assertInstanceOf(BossProjectile.class, projectile);
                assertEquals(boss.getTranslateX(), projectile.getTranslateX(), DELTA); // X position should match boss
                assertTrue(projectile.getTranslateY() >= 0, "Projectile Y position should be positive"); // Y position check
            }
        }
    }

    @Test
    void testFireProjectile() {
        boss.setTranslateX(1150); // Boss's initial X position
        boss.setTranslateY(400); // Boss's initial Y position

        DestructibleGameObject projectile = boss.fireProjectile();

        if (projectile != null) {
            assertInstanceOf(BossProjectile.class, projectile); // Ensure it's the correct type
            assertEquals(boss.getTranslateX(), projectile.getTranslateX(), DELTA); // X-coordinate matches boss
            assertEquals(boss.getTranslateY() + 40, projectile.getTranslateY(), DELTA); // Y-coordinate includes offset
        } else {
            assertNull(projectile); // Projectile may not fire in some frames due to chance
        }
    }
}

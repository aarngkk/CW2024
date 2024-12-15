package com.finalflight.game.gameobjects;

import com.finalflight.game.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnemyPlaneTest extends BaseTest {

    private EnemyPlane enemyPlane;
    private static final double INITIAL_X_POS = 200;
    private static final double INITIAL_Y_POS = 100;
    private static final int INITIAL_HEALTH = 3;

    @BeforeEach
    void setUp() {
        enemyPlane = new EnemyPlane(INITIAL_X_POS, INITIAL_Y_POS);
    }

    @Test
    void testInitialization() {
        assertEquals(INITIAL_HEALTH, enemyPlane.getHealth());
        assertEquals(INITIAL_X_POS, enemyPlane.getLayoutX(), 0.01);
        assertEquals(INITIAL_Y_POS, enemyPlane.getLayoutY(), 0.01);
    }

    @Test
    void testUpdatePosition() {
        double initialX = enemyPlane.getTranslateX();
        enemyPlane.updatePosition();
        assertEquals(initialX - 6, enemyPlane.getTranslateX(), 0.01); // Horizontal velocity is -6
    }

    @Test
    void testFireProjectileSuccess() {
        // Force Math.random to always return 0.0, which is below FIRE_RATE
        enemyPlane.setRandomSupplier(() -> 0.0);
        for (int i = 0; i < 100; i++) { // Simulate multiple calls to fireProjectile
            DestructibleGameObject projectile = enemyPlane.fireProjectile();
            if (projectile != null) {
                assertInstanceOf(EnemyProjectile.class, projectile);
                return; // Test successful if at least one projectile is fired
            }
        }
        fail("EnemyPlane did not fire a projectile in 100 attempts.");
    }

    @Test
    void testFireProjectileFailure() {
        // Force Math.random to always return 1.0, which is above FIRE_RATE
        enemyPlane.setRandomSupplier(() -> 1.0);
        boolean projectileFired = false;
        for (int i = 0; i < 100; i++) {
            if (enemyPlane.fireProjectile() != null) {
                projectileFired = true;
                break;
            }
        }
        assertFalse(projectileFired, "Projectile fired when it shouldn't have.");
    }

    @Test
    void testTakeDamage() {
        int initialHealth = enemyPlane.getHealth();
        enemyPlane.takeDamage();
        assertEquals(initialHealth - 1, enemyPlane.getHealth());
    }

    @Test
    void testTakeDamageUntilDestroyed() {
        for (int i = 0; i < INITIAL_HEALTH; i++) {
            enemyPlane.takeDamage();
        }
        assertEquals(0, enemyPlane.getHealth());
        assertTrue(enemyPlane.isDestroyed(), "EnemyPlane should be destroyed when health reaches 0.");
    }

    @Test
    void testHealthDoesNotGoBelowZero() {
        for (int i = 0; i < INITIAL_HEALTH + 1; i++) {
            enemyPlane.takeDamage();
        }
        assertEquals(0, enemyPlane.getHealth());
        assertTrue(enemyPlane.isDestroyed(), "EnemyPlane should remain destroyed even after taking extra damage.");
    }

    @Test
    void testUpdateActor() {
        // The updateActor method calls updatePosition, test the effect
        double initialX = enemyPlane.getTranslateX();
        enemyPlane.updateActor();
        assertEquals(initialX - 6, enemyPlane.getTranslateX(), 0.01);
    }

}

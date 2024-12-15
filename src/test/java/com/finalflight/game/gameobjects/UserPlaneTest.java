package com.finalflight.game.gameobjects;

import com.finalflight.game.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserPlaneTest extends BaseTest {

    private UserPlane userPlane;
    private static final int INITIAL_HEALTH = 5;
    private static final double DELTA = 0.01;

    @BeforeEach
    void setUp() {
        userPlane = new UserPlane(INITIAL_HEALTH);
    }

    @Test
    void testInitialization() {
        UserPlane.resetHealth(INITIAL_HEALTH);
        UserPlane userPlane = new UserPlane(5);
        assertEquals(5, userPlane.getHealth());
        assertEquals(UserPlane.getMaxBoostEnergy(), userPlane.getBoostEnergy(), 0.01);
        assertEquals(UserPlane.FiringMode.SINGLE, userPlane.getFiringMode());
    }

    @Test
    void testMovementWithinBounds() {
        // Test vertical movement (down)
        userPlane.move(1, true);
        userPlane.updatePosition();
        assertTrue(userPlane.getTranslateY() >= UserPlane.getYUpperBound());

        // Test horizontal movement (right)
        userPlane.move(1, false);
        userPlane.updatePosition();
        assertTrue(userPlane.getTranslateX() <= UserPlane.getXRightBound());
    }

    @Test
    void testMovementExceedingBounds() {
        // Try moving beyond the upper bound
        userPlane.setTranslateY(UserPlane.getYUpperBound());
        userPlane.move(-1, true); // Move up
        userPlane.updatePosition();
        assertTrue(userPlane.getTranslateY() >= UserPlane.getYUpperBound());

        // Try moving beyond the right bound
        userPlane.setTranslateX(UserPlane.getXRightBound());
        userPlane.move(1, false); // Move right
        userPlane.updatePosition();
        assertTrue(userPlane.getTranslateX() <= UserPlane.getXRightBound());
    }

    @Test
    void testBoostEffects() {
        userPlane.setSpeedBoost(true);
        assertTrue(userPlane.getBoostEnergy() > 0);
        userPlane.updateActor();
        assertTrue(userPlane.getBoostEnergy() < UserPlane.getMaxBoostEnergy());

        userPlane.setSpeedBoost(false);
        userPlane.updateActor();
        assertTrue(userPlane.getBoostEnergy() > 0); // Energy should start recharging
    }

    @Test
    void testBoostEnergyDrainAndRecharge() throws InterruptedException {
        // Activate speed boost
        userPlane.setSpeedBoost(true);
        assertTrue(userPlane.getBoostEnergy() > 0);

        // Simulate energy drain
        for (int i = 0; i < 100; i++) {
            userPlane.updateActor();
        }
        assertEquals(0, userPlane.getBoostEnergy(), 0.01);

        // Disable speed boost and simulate recharge after waiting for cooldown
        userPlane.setSpeedBoost(false);
        Thread.sleep(1500);
        for (int i = 0; i < 200; i++) {
            userPlane.updateActor();
        }
        assertTrue(userPlane.getBoostEnergy() > 0);
    }

    @Test
    void testRechargeBoostEnergyCap() {
        // Manually set boost energy beyond max
        userPlane.setSpeedBoost(false);
        userPlane.updateActor(); // Simulate frame updates
        for (int i = 0; i < 500; i++) { // Simulate long recharge period
            userPlane.updateActor();
        }

        // Verify it doesn't exceed the maximum energy
        assertEquals(UserPlane.getMaxBoostEnergy(), userPlane.getBoostEnergy(), DELTA);
    }

    @Test
    void testSpeedBoostDeactivationOnDepletion() {
        userPlane.setSpeedBoost(true); // Activate speed boost
        while (userPlane.getBoostEnergy() > 0) {
            userPlane.updateActor(); // Drain energy completely
        }

        // Ensure speed boost is disabled when energy is depleted
        assertFalse(userPlane.getIsSpeedBoostActive());
        assertEquals(0, userPlane.getBoostEnergy(), DELTA);
    }

    @Test
    void testFiringSingleProjectile() {
        DestructibleGameObject projectile = userPlane.fireProjectile();
        assertNotNull(projectile);
    }

    @Test
    void testFiringSpreadProjectile() {
        userPlane.setFiringMode(UserPlane.FiringMode.SPREAD);

        List<DestructibleGameObject> projectiles = userPlane.fire();
        assertEquals(3, projectiles.size()); // Spread mode fires 3 projectiles
    }

    @Test
    void testFiringHeavyProjectile() {
        userPlane.setFiringMode(UserPlane.FiringMode.HEAVY);

        List<DestructibleGameObject> projectiles = userPlane.fire();
        assertEquals(1, projectiles.size()); // Heavy mode fires 1 projectile
    }

    @Test
    void testFireProjectileCooldown() {
        // Fire once to initialize the lastFiredTime
        DestructibleGameObject projectile = userPlane.fireProjectile();
        assertNotNull(projectile);

        // Immediately try to fire again (cooldown still active)
        projectile = userPlane.fireProjectile();
        assertNull(projectile); // Should return null
    }

    @Test
    void testFireSpreadProjectileCooldown() {
        userPlane.setFiringMode(UserPlane.FiringMode.SPREAD);

        // Fire once
        List<DestructibleGameObject> projectiles = userPlane.fire();
        assertEquals(3, projectiles.size());

        // Immediately try to fire again (cooldown still active)
        projectiles = userPlane.fire();
        assertNull(projectiles);
    }

    @Test
    void testFireHeavyProjectileCooldown() {
        userPlane.setFiringMode(UserPlane.FiringMode.HEAVY);

        // Fire once
        List<DestructibleGameObject> projectiles = userPlane.fire();
        assertEquals(1, projectiles.size());

        // Immediately try to fire again (cooldown still active)
        projectiles = userPlane.fire();
        assertTrue(projectiles.isEmpty()); // Should return an empty list
    }

    @Test
    void testFiringModeChange() {
        // Change to SPREAD mode
        userPlane.setFiringMode(UserPlane.FiringMode.SPREAD);
        assertEquals(UserPlane.FiringMode.SPREAD, userPlane.getFiringMode());

        // Change to HEAVY mode
        userPlane.setFiringMode(UserPlane.FiringMode.HEAVY);
        assertEquals(UserPlane.FiringMode.HEAVY, userPlane.getFiringMode());

        // Change back to SINGLE mode
        userPlane.setFiringMode(UserPlane.FiringMode.SINGLE);
        assertEquals(UserPlane.FiringMode.SINGLE, userPlane.getFiringMode());
    }

    @Test
    void testPersistentHealth() {
        UserPlane.resetHealth(10); // Reset health to a known value
        UserPlane userPlane1 = new UserPlane(5);
        assertEquals(10, userPlane1.getHealth());

        userPlane1.takeDamage();
        assertTrue(userPlane1.getHealth() < 10);

        UserPlane userPlane2 = new UserPlane(5);
        assertEquals(userPlane1.getHealth(), userPlane2.getHealth()); // Health should persist
    }

    @Test
    void testTakeDamage() {
        int initialHealth = userPlane.getHealth();
        userPlane.takeDamage();
        assertEquals(initialHealth - 1, userPlane.getHealth());
    }

    @Test
    void testTakeDamageMultipleTimes() {
        for (int i = 0; i < 5; i++) {
            userPlane.takeDamage(); // Take damage repeatedly
        }

        // Verify health is reduced correctly
        assertEquals(0, userPlane.getHealth());
    }


    @Test
    void testKillCount() {
        assertEquals(0, userPlane.getNumberOfKills());

        userPlane.incrementKillCount();
        assertEquals(1, userPlane.getNumberOfKills());
    }

    @Test
    void testIncrementKillCountOverflow() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            userPlane.incrementKillCount(); // Ensure no overflow exception
        }
    }

}

/**
 * Represents the player's plane with specific abilities, firing modes, and movement mechanics.
 * Extends {@link FighterPlane} and includes additional features like speed boost and multiple firing modes.
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/gameobjects/UserPlane.java">UserPlane.java</a></p>
 */
package com.finalflight.game.gameobjects;

import com.finalflight.game.audio.SoundEffectPlayer;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

public class UserPlane extends FighterPlane {

    private static final String DAMAGED_SOUND = "/com/finalflight/game/audio/userdamaged.mp3";
    private static final String DAMAGED_BEEPING_SOUND = "/com/finalflight/game/audio/userbeeping.mp3";
    private static final String EQUIP_SINGLE_SOUND = "/com/finalflight/game/audio/equipsingle.wav";
    private static final String EQUIP_SPREAD_SOUND = "/com/finalflight/game/audio/equipspread.mp3";
    private static final String EQUIP_HEAVY_SOUND = "/com/finalflight/game/audio/equipheavy.mp3";
    private static final String SINGLE_FIRE_SOUND = "/com/finalflight/game/audio/userfire.wav";
    private static final String SPREAD_FIRE_SOUND = "/com/finalflight/game/audio/userspreadfire.wav";
    private static final String HEAVY_FIRE_SOUND = "/com/finalflight/game/audio/userheavyfire.mp3";
    private static final String SPEED_BOOST_SOUND = "/com/finalflight/game/audio/userspeedboost.mp3";
    private static final String IMAGE_NAME = "userplane.png";
    private static final double X_LEFT_BOUND = 0.0;
    private static final double X_RIGHT_BOUND = 1000.0;
    private static final double Y_UPPER_BOUND_OFFSET = 400.0;
    private static final double Y_UPPER_BOUND = - Y_UPPER_BOUND_OFFSET;
    private static final double Y_LOWER_BOUND = 800.0 - Y_UPPER_BOUND_OFFSET;
    private static final double INITIAL_X_POSITION = 5.0;
    private static final double INITIAL_Y_POSITION = 400.0;
    private static final int IMAGE_HEIGHT = 60;
    private static final int HORIZONTAL_VELOCITY = 8;
    private static final int VERTICAL_VELOCITY = 8;
    private static final long FIRE_RATE = 100; // Fire rate in milliseconds
    private static final long SPREAD_FIRE_RATE = 200;
    private static final long HEAVY_FIRE_RATE = 1000;
    private long lastFiredTime = 0; // Stores the last time the player fired a projectile
    private int velocityMultiplierY = 0;
    private double horizontalVelocity = 0;
    private int numberOfKills;

    public enum FiringMode {
        SINGLE, SPREAD, HEAVY
    }

    private static final double SPEED_BOOST_MULTIPLIER = 1.5;
    private static final double BOOST_DRAIN_RATE = 1.5; // Energy drained per frame while boosting
    private static final double BOOST_RECHARGE_RATE = 0.5; // Energy recharged per frame
    private static final double MAX_BOOST_ENERGY = 100;
    private static final int BOOST_COOLDOWN_TIME = 1500; // Cooldown time in milliseconds

    private double currentBoostEnergy = MAX_BOOST_ENERGY;
    private boolean isSpeedBoostActive = false;
    private long lastDepletedTime = 0; // Track the last time boost was depleted
    private final Rotate rotateEffect = new Rotate();

    private FiringMode currentFiringMode = FiringMode.SINGLE;
    private static int persistentHealth;

    private final SoundEffectPlayer damagedImpactSound;
    private final SoundEffectPlayer damagedBeepingSound;
    private final SoundEffectPlayer equipSingleSound;
    private final SoundEffectPlayer equipSpreadSound;
    private final SoundEffectPlayer equipHeavySound;
    private final SoundEffectPlayer fireSound;
    private final SoundEffectPlayer spreadFireSound;
    private final SoundEffectPlayer heavyFireSound;
    private final SoundEffectPlayer speedBoostSound;

    /**
     * Initializes a UserPlane instance with a specified initial health.
     *
     * @param initialHealth The starting health of the UserPlane.
     */
    public UserPlane(int initialHealth) {
        super(IMAGE_NAME, IMAGE_HEIGHT, INITIAL_X_POSITION, INITIAL_Y_POSITION, persistentHealth > 0 ? persistentHealth : initialHealth);
        persistentHealth = getHealth();

        fireSound = new SoundEffectPlayer(SINGLE_FIRE_SOUND);
        fireSound.setVolume(0.15);

        spreadFireSound = new SoundEffectPlayer(SPREAD_FIRE_SOUND);
        spreadFireSound.setVolume(0.15);

        heavyFireSound = new SoundEffectPlayer(HEAVY_FIRE_SOUND);
        heavyFireSound.setVolume(0.3);

        speedBoostSound = new SoundEffectPlayer(SPEED_BOOST_SOUND);
        speedBoostSound.setVolume(0.1);

        equipSingleSound = new SoundEffectPlayer(EQUIP_SINGLE_SOUND);
        equipSingleSound.setVolume(0.5);

        equipSpreadSound = new SoundEffectPlayer(EQUIP_SPREAD_SOUND);
        equipSpreadSound.setVolume(0.15);

        equipHeavySound = new SoundEffectPlayer(EQUIP_HEAVY_SOUND);
        equipHeavySound.setVolume(0.8);

        damagedBeepingSound = new SoundEffectPlayer(DAMAGED_BEEPING_SOUND);
        damagedBeepingSound.setVolume(0.2);

        damagedImpactSound = new SoundEffectPlayer(DAMAGED_SOUND);
        damagedImpactSound.setVolume(0.3);
    }

    /**
     * Updates the position of the user plane based on velocity and current boost status.
     * Ensures the plane stays within predefined bounds.
     */
    @Override
    public void updatePosition() {
        double boostFactor = isSpeedBoostActive ? SPEED_BOOST_MULTIPLIER : 1.0;

        // Handle vertical movement
        double newYPosition = getTranslateY() + (VERTICAL_VELOCITY * velocityMultiplierY * boostFactor);
        if (newYPosition >= Y_UPPER_BOUND && newYPosition <= Y_LOWER_BOUND) {
            setTranslateY(newYPosition);
        }

        // Handle horizontal movement
        double newXPosition = getTranslateX() + (horizontalVelocity * boostFactor);
        if (newXPosition >= X_LEFT_BOUND && newXPosition <= X_RIGHT_BOUND) {
            setTranslateX(newXPosition);
        }
    }

    /**
     * Updates the state of the user plane each frame.
     * Handles position, boost energy, and other periodic changes.
     */
    @Override
    public void updateActor() {
        updatePosition();
        drainBoostEnergy();  // Deplete boost when active
        rechargeBoostEnergy();  // Handle boost recharge
    }

    /**
     * Fires a single projectile from the user plane.
     *
     * @return A {@link DestructibleGameObject} representing the fired projectile, or null if the cooldown has not elapsed.
     */
    @Override
    public DestructibleGameObject fireProjectile() {
        long currentTime = System.currentTimeMillis(); // Get the current time in milliseconds

        // Check if enough time has passed since the last shot
        if (currentTime - lastFiredTime >= FIRE_RATE) {
            // Fire the projectile
            double projectileX = getTranslateX() + getBoundsInLocal().getWidth();
            double projectileY = getTranslateY() + Y_UPPER_BOUND_OFFSET + (getBoundsInLocal().getHeight() / 2);

            // Update last fired time
            lastFiredTime = currentTime;

            fireSound.playSound();

            return new SingleShotProjectile(projectileX, projectileY);
        }
        // Do nothing if the player tries to fire before the cooldown
        return null;
    }

    /**
     * Fires a spread of projectiles from the user plane.
     *
     * @return A list of {@link DestructibleGameObject} instances representing the spread projectiles.
     */
    public List<DestructibleGameObject> fireSpreadProjectile() {
        long currentTime = System.currentTimeMillis();


        // Check if enough time has passed since the last shot
        if (currentTime - lastFiredTime >= SPREAD_FIRE_RATE) {
            // Fire the spread projectiles
            List<DestructibleGameObject> spreadBullets = new ArrayList<>();
            double baseX = getTranslateX() + getBoundsInLocal().getWidth();
            double baseY = getTranslateY() + Y_UPPER_BOUND_OFFSET + getBoundsInLocal().getHeight() / 2;

            spreadBullets.add(new SpreadShotProjectile(baseX, baseY, 10, 0)); // Center bullet
            spreadBullets.add(new SpreadShotProjectile(baseX, baseY, 10, -5)); // Upward bullet
            spreadBullets.add(new SpreadShotProjectile(baseX, baseY, 10, 5)); // Downward bullet

            // Update last fired time
            lastFiredTime = currentTime;

            spreadFireSound.playSound();

            return spreadBullets;
        }
        // Do nothing if the player tries to fire before the cooldown
        return null;
    }

    /**
     * Fires a heavy projectile with high damage from the user plane.
     *
     * @return A list containing a single {@link DestructibleGameObject} instance representing the heavy projectile.
     */
    public List<DestructibleGameObject> fireHeavyProjectile() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastFiredTime >= HEAVY_FIRE_RATE) {
            double projectileX = getTranslateX() + (getBoundsInLocal().getWidth() / 2);
            double projectileY = getTranslateY() + Y_UPPER_BOUND_OFFSET + getBoundsInLocal().getHeight() - 10;

            lastFiredTime = currentTime;

            heavyFireSound.playSound();

            return List.of(new HeavyShotProjectile(projectileX, projectileY));
        }
        return List.of();
    }

    /**
     * Fires projectiles based on the current firing mode.
     *
     * @return A list of {@link DestructibleGameObject} instances representing the fired projectiles.
     */
    public List<DestructibleGameObject> fire() {
        if (currentFiringMode == FiringMode.SINGLE) {
            DestructibleGameObject singleProjectile = fireProjectile();
            return singleProjectile != null ? List.of(singleProjectile) : List.of();
        } else if (currentFiringMode == FiringMode.SPREAD) {
            return fireSpreadProjectile();
        } else if (currentFiringMode == FiringMode.HEAVY) {
            return fireHeavyProjectile();
        }
        return List.of();
    }

    /**
     * Sets the firing mode of the user plane.
     *
     * @param mode The desired {@link FiringMode} to switch to.
     */
    public void setFiringMode(FiringMode mode) {
        if (this.currentFiringMode != mode) {
            this.currentFiringMode = mode;

            switch (mode) {
                case SINGLE -> equipSingleSound.playSound();
                case SPREAD -> equipSpreadSound.playSound();
                case HEAVY -> equipHeavySound.playSound();
            }
        }
    }

    /**
     * Gets the current firing mode of the user plane.
     *
     * @return The current {@link FiringMode} of the plane.
     */
    public FiringMode getFiringMode() {
        return currentFiringMode;
    }

    /**
     * Moves the user plane in the specified direction.
     *
     * @param direction  The direction of movement (-1 for left/up, 1 for right/down, 0 to stop).
     * @param isVertical Whether the movement is vertical or horizontal.
     */
    public void move(int direction, boolean isVertical) {
        if (isVertical) {
            velocityMultiplierY = direction; // -1 for up, 1 for down, 0 to stop
        } else {
            horizontalVelocity = direction * HORIZONTAL_VELOCITY; // -1 for left, 1 for right, 0 to stop
        }
    }

    /**
     * Reduces the plane's health and plays damage sound effects.
     */
    @Override
    public void takeDamage() {
        super.takeDamage();
        persistentHealth = getHealth(); // Update persistent health
        damagedBeepingSound.stopSound();
        damagedBeepingSound.playSound();
        damagedImpactSound.playSound();
    }

    /**
     * Resets the static health of the user plane.
     *
     * @param health The new health value to set.
     */
    public static void resetHealth(int health) {
        persistentHealth = health;
    }

    /**
     * Updates the visual effects of the user plane when the speed boost is active.
     * If the speed boost is active, the plane will have a slight rotation to indicate boost.
     * If the boost is not active, any transformations applied (such as rotation) are cleared.
     */
    private void updateBoostEffects() {
        if (isSpeedBoostActive) {
            // Apply rotation for speed boost
            rotateEffect.setAngle(5); // Slight rotation when speed boost is active
            this.getTransforms().clear();
            this.getTransforms().add(rotateEffect); // Apply the rotation
        } else {
            // Reset the rotation when speed boost is off
            rotateEffect.setAngle(0);
            this.getTransforms().clear(); // Remove any transform when boost is inactive
        }
    }

    /**
     * Activates or deactivates the speed boost for the user plane.
     * Updates effects and handles sound playback.
     *
     * @param isActive Whether the speed boost should be active.
     */
    public void setSpeedBoost(boolean isActive) {
        if (isActive && currentBoostEnergy > 0) {
            if (!isSpeedBoostActive) {
                speedBoostSound.playSound();
            }
            isSpeedBoostActive = true;
        } else {
            if (isSpeedBoostActive) {
                speedBoostSound.stopSound();
            }
            isSpeedBoostActive = false;
            lastDepletedTime = System.currentTimeMillis(); // Track the time when boost was depleted
        }

        updateBoostEffects(); // Update plane speed boost effects
    }

    /**
     * Recharges the user's boost energy over time, provided the boost is not active.
     * Boost energy is recharged at a specified rate, and the recharge only begins after a cooldown
     * period has passed since the last depletion.
     */
    private void rechargeBoostEnergy() {
        if (!isSpeedBoostActive && currentBoostEnergy < MAX_BOOST_ENERGY) {
            // Check if cooldown period has passed before recharging
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastDepletedTime >= BOOST_COOLDOWN_TIME) {
                // Start recharging after cooldown period
                currentBoostEnergy += BOOST_RECHARGE_RATE;
                if (currentBoostEnergy > MAX_BOOST_ENERGY) {
                    currentBoostEnergy = MAX_BOOST_ENERGY;
                }
            }
        }
    }

    /**
     * Drains the user's boost energy while the speed boost is active.
     * The energy is drained at a specified rate, and if the energy reaches zero, the speed boost is deactivated.
     */
    private void drainBoostEnergy() {
        if (isSpeedBoostActive && currentBoostEnergy > 0) {
            currentBoostEnergy -= BOOST_DRAIN_RATE;
            if (currentBoostEnergy <= 0) {
                currentBoostEnergy = 0;
                setSpeedBoost(false); // Disable boost if energy is depleted
            }
        }
    }

    /**
     * Gets the current number of kills achieved by the user.
     *
     * @return The total kill count.
     */
    public int getNumberOfKills() {
        return numberOfKills;
    }

    /**
     * Increments the kill count of the user by one.
     */
    public void incrementKillCount() {
        numberOfKills++;
    }

    /**
     * Gets the maximum boost energy available for the user plane.
     *
     * @return The maximum boost energy value.
     */
    public static double getMaxBoostEnergy() {
        return MAX_BOOST_ENERGY;
    }

    /**
     * Gets the current boost energy level of the user plane.
     *
     * @return The current boost energy value.
     */
    public double getBoostEnergy() {
        return currentBoostEnergy;
    }

    /**
     * Checks whether the speed boost is currently active.
     *
     * @return True if the speed boost is active, false otherwise.
     */
    public boolean getIsSpeedBoostActive() {
        return isSpeedBoostActive;
    }

    /**
     * Gets the upper bound for the plane's vertical movement.
     *
     * @return The vertical upper bound.
     */
    public static double getYUpperBound() {
        return Y_UPPER_BOUND;
    }

    /**
     * Gets the right bound for the plane's horizontal movement.
     *
     * @return The horizontal right bound.
     */
    public static double getXRightBound() {
        return X_RIGHT_BOUND;
    }

}

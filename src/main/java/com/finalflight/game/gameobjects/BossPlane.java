package com.finalflight.game.gameobjects;

import com.finalflight.game.audio.SoundEffectPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@code BossPlane} class represents the final and most powerful enemy plane in the game.
 * It extends {@link FighterPlane} and features advanced behaviors such as shields, move patterns,
 * and the ability to boost its fire rate and movement speed when health is low.
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/gameobjects/BossPlane.java</p>
 */
public class BossPlane extends FighterPlane {

    private static final String BOSS_FIRE_SOUND = "/com/finalflight/game/audio/bossfire.mp3";
    private static final String SHIELD_ACTIVATE_SOUND = "/com/finalflight/game/audio/bossshieldactivate.wav";
    private static final String IMAGE_NAME = "bossplane.png";
    private static final double INITIAL_X_POSITION = 1150.0;
    private static final double INITIAL_Y_POSITION = 400;
    private static final double PROJECTILE_Y_POSITION_OFFSET = 45;
    private static final double BOSS_FIRE_RATE = 0.04;
    private static final double INCREASED_FIRE_RATE = 0.08;
    private static final int IMAGE_HEIGHT = 130;
    private static final int VERTICAL_VELOCITY = 8;
    private static final int INCREASED_VERTICAL_VELOCITY = 12;
    private static final int HEALTH = 100;
    private static final int MOVE_FREQUENCY_PER_CYCLE = 5;
    private static final int ZERO = 0;
    private static final int MAX_FRAMES_WITH_SAME_MOVE = 10;
    private static final int Y_POSITION_UPPER_BOUND = -50;
    private static final int Y_POSITION_LOWER_BOUND = 700;
    private static final int MAX_FRAMES_WITH_SHIELD = 250;
    private final List<Integer> movePattern;
    private final SoundEffectPlayer fireSound;
    private final SoundEffectPlayer shieldActivateSound;
    private boolean isShielded;
    private boolean shieldActivatedAt50;
    private boolean shieldActivatedAt20;
    private boolean fireRateAndSpeedBoosted;
    private int consecutiveMovesInSameDirection;
    private int indexOfCurrentMove;
    private int framesWithShieldActivated;

    /**
     * Constructs a {@code BossPlane} with default attributes and initializes its move pattern, shield sound,
     * and firing sound.
     */
    public BossPlane() {
        super(IMAGE_NAME, IMAGE_HEIGHT, INITIAL_X_POSITION, INITIAL_Y_POSITION, HEALTH);
        movePattern = new ArrayList<>();
        consecutiveMovesInSameDirection = 0;
        indexOfCurrentMove = 0;
        framesWithShieldActivated = 0;
        isShielded = false;
        shieldActivatedAt50 = false;
        shieldActivatedAt20 = false;
        fireRateAndSpeedBoosted = false;
        initializeMovePattern();

        shieldActivateSound = new SoundEffectPlayer(SHIELD_ACTIVATE_SOUND);
        shieldActivateSound.setVolume(0.6);

        fireSound = new SoundEffectPlayer(BOSS_FIRE_SOUND);
        fireSound.setVolume(0.1);
    }

    /**
     * Updates the position of the boss plane based on its current movement pattern.
     * Ensures that the plane remains within predefined bounds.
     */
    @Override
    public void updatePosition() {
        double initialTranslateY = getTranslateY();
        moveVertically(getNextMove());
        double currentPosition = getLayoutY() + getTranslateY();
        if (currentPosition < Y_POSITION_UPPER_BOUND || currentPosition > Y_POSITION_LOWER_BOUND) {
            setTranslateY(initialTranslateY);
        }
    }

    /**
     * Handles the boss plane's updates, including position, shield status, and boosted attributes.
     */
    @Override
    public void updateActor() {
        updatePosition();
        updateShield();
        checkAndApplyBoosts();
    }

    /**
     * Fires a projectile from the boss plane. The fire rate increases when health falls below a critical threshold.
     *
     * @return a new {@link BossProjectile} if the firing condition is met; otherwise, {@code null}.
     */
    @Override
    public DestructibleGameObject fireProjectile() {
        if (bossFiresInCurrentFrame()) {
            fireSound.playSound();
            return new BossProjectile(getProjectileInitialPosition());
        }
        return null;
    }

    /**
     * Reduces the boss plane's health when it takes damage. Damage is ignored if the shield is active.
     */
    @Override
    public void takeDamage() {
        if (!isShielded) {
            super.takeDamage();
        }
    }

    /**
     * Initializes the movement pattern for the boss plane with a sequence of vertical movements
     * (up, down, and stationary). The pattern is shuffled to introduce randomness.
     */
    private void initializeMovePattern() {
        for (int i = 0; i < MOVE_FREQUENCY_PER_CYCLE; i++) {
            movePattern.add(VERTICAL_VELOCITY);
            movePattern.add(-VERTICAL_VELOCITY);
            movePattern.add(ZERO);
        }
        Collections.shuffle(movePattern);
    }

    /**
     * Updates the shield status of the boss plane. Activates the shield if necessary,
     * increments the duration while the shield is active, and deactivates the shield
     * if its duration has expired.
     */
    private void updateShield() {
        if (isShielded) framesWithShieldActivated++;
        else if (shieldShouldBeActivated()) activateShield();
        if (shieldExhausted()) deactivateShield();
    }

    /**
     * Checks the boss plane's health and applies boosts to its fire rate and movement speed
     * if the health falls below a critical threshold (20% of the maximum health).
     */
    private void checkAndApplyBoosts() {
        // Apply fire rate and speed boost if health is 20% or below
        if (!fireRateAndSpeedBoosted && getHealth() <= HEALTH * 0.2) {
            fireRateAndSpeedBoosted = true;
            increaseFireRateAndMovementSpeed();
        }
    }

    /**
     * Determines the next vertical movement for the boss plane based on its current movement pattern.
     * Ensures the plane does not repeatedly move in the same direction for too long and reshuffles
     * the pattern as needed.
     *
     * @return the next vertical movement value from the movement pattern.
     */
    private int getNextMove() {
        int currentMove = movePattern.get(indexOfCurrentMove);
        consecutiveMovesInSameDirection++;
        if (consecutiveMovesInSameDirection == MAX_FRAMES_WITH_SAME_MOVE) {
            Collections.shuffle(movePattern);
            consecutiveMovesInSameDirection = 0;
            indexOfCurrentMove++;
        }
        if (indexOfCurrentMove == movePattern.size()) {
            indexOfCurrentMove = 0;
        }
        return currentMove;
    }

    /**
     * Determines whether the boss plane should fire a projectile in the current frame.
     * The fire rate is dynamically adjusted based on the boss plane's health.
     *
     * @return {@code true} if the boss plane fires in the current frame; {@code false} otherwise.
     */
    private boolean bossFiresInCurrentFrame() {
        double currentFireRate = fireRateAndSpeedBoosted ? INCREASED_FIRE_RATE : BOSS_FIRE_RATE;
        return Math.random() < currentFireRate;
    }

    /**
     * Calculates the Y-coordinate for the initial position of a projectile fired by the boss plane.
     *
     * @return the Y-coordinate for the projectile's starting position.
     */
    private double getProjectileInitialPosition() {
        return getLayoutY() + getTranslateY() + PROJECTILE_Y_POSITION_OFFSET;
    }

    /**
     * Boosts the boss plane's fire rate and movement speed by reinitializing its movement pattern
     * with increased velocity values. This boost is applied when the boss plane's health
     * drops below a critical threshold.
     */
    private void increaseFireRateAndMovementSpeed() {
        movePattern.clear(); // Reinitialize move pattern with increased speed
        for (int i = 0; i < MOVE_FREQUENCY_PER_CYCLE; i++) {
            movePattern.add(INCREASED_VERTICAL_VELOCITY);
            movePattern.add(-INCREASED_VERTICAL_VELOCITY);
            movePattern.add(ZERO);
        }
        Collections.shuffle(movePattern);
    }

    /**
     * Checks if the boss plane's shield should be activated based on its current health.
     * The shield is activated at 50% and 20% health thresholds, and each threshold is triggered only once.
     *
     * @return {@code true} if the shield should be activated; {@code false} otherwise.
     */
    private boolean shieldShouldBeActivated() {
        int currentHealth = getHealth();
        if (!shieldActivatedAt50 && currentHealth <= HEALTH * 0.5) {
            shieldActivatedAt50 = true;
            return true;
        }
        if (!shieldActivatedAt20 && currentHealth <= HEALTH * 0.2) {
            shieldActivatedAt20 = true;
            return true;
        }
        return false;
    }

    /**
     * Checks whether the shield has been active for its maximum allowed duration.
     *
     * @return {@code true} if the shield has been active for the maximum duration; {@code false} otherwise.
     */
    private boolean shieldExhausted() {
        return framesWithShieldActivated == MAX_FRAMES_WITH_SHIELD;
    }

    /**
     * Activates the boss plane's shield, providing temporary immunity to damage.
     * Plays a sound effect to indicate shield activation.
     */
    private void activateShield() {
        isShielded = true;
        shieldActivateSound.playSound();
    }

    /**
     * Deactivates the boss plane's shield after its duration has expired.
     * Resets the duration counter and updates the shield status.
     */
    private void deactivateShield() {
        isShielded = false;
        framesWithShieldActivated = 0;
    }

    /**
     * Retrieves the current shield status of the boss plane.
     *
     * @return {@code true} if the shield is currently active; {@code false} otherwise.
     */
    public boolean getIsShielded() {
        return isShielded;
    }

    /**
     * Retrieves the current movement pattern of the boss plane. The pattern defines the sequence
     * of vertical movements (up, down, and stationary) for the plane.
     *
     * @return a {@link List} of integers representing the movement pattern.
     */
    public List<Integer> getMovePattern() {
        return movePattern;
    }
}

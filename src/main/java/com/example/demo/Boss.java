package com.example.demo;

import java.util.*;

public class Boss extends FighterPlane {

	private static final String IMAGE_NAME = "bossplane.png";
	private static final double INITIAL_X_POSITION = 1150.0;
	private static final double INITIAL_Y_POSITION = 400;
	private static final double PROJECTILE_Y_POSITION_OFFSET = 35.0;
	private static final double BOSS_FIRE_RATE = 0.04;
	private static final double INCREASED_FIRE_RATE = 0.08;
	private static final int IMAGE_HEIGHT = 85;
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
	private boolean isShielded;
	private boolean shieldActivatedAt50;
	private boolean shieldActivatedAt20;
	private boolean fireRateAndSpeedBoosted;
	private int consecutiveMovesInSameDirection;
	private int indexOfCurrentMove;
	private int framesWithShieldActivated;

	public Boss() {
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
	}

	@Override
	public void updatePosition() {
		double initialTranslateY = getTranslateY();
		moveVertically(getNextMove());
		double currentPosition = getLayoutY() + getTranslateY();
		if (currentPosition < Y_POSITION_UPPER_BOUND || currentPosition > Y_POSITION_LOWER_BOUND) {
			setTranslateY(initialTranslateY);
		}
	}
	
	@Override
	public void updateActor() {
		updatePosition();
		updateShield();
		checkAndApplyBoosts();
	}

	@Override
	public ActiveActorDestructible fireProjectile() {
		return bossFiresInCurrentFrame() ? new BossProjectile(getProjectileInitialPosition()) : null;
	}
	
	@Override
	public void takeDamage() {
		if (!isShielded) {
			super.takeDamage();
		}
	}

	private void initializeMovePattern() {
		for (int i = 0; i < MOVE_FREQUENCY_PER_CYCLE; i++) {
			movePattern.add(VERTICAL_VELOCITY);
			movePattern.add(-VERTICAL_VELOCITY);
			movePattern.add(ZERO);
		}
		Collections.shuffle(movePattern);
	}

	private void updateShield() {
		if (isShielded) framesWithShieldActivated++;
		else if (shieldShouldBeActivated()) activateShield();	
		if (shieldExhausted()) deactivateShield();
	}

	private void checkAndApplyBoosts() {
		// Apply fire rate and speed boost if health is 20% or below
		if (!fireRateAndSpeedBoosted && getHealth() <= HEALTH * 0.2) {
			fireRateAndSpeedBoosted = true;
			increaseFireRateAndMovementSpeed();
		}
	}

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

	private boolean bossFiresInCurrentFrame() {
		double currentFireRate = fireRateAndSpeedBoosted ? INCREASED_FIRE_RATE : BOSS_FIRE_RATE;
		return Math.random() < currentFireRate;
	}

	private double getProjectileInitialPosition() {
		return getLayoutY() + getTranslateY() + PROJECTILE_Y_POSITION_OFFSET;
	}

	private void increaseFireRateAndMovementSpeed() {
		movePattern.clear(); // Reinitialize move pattern with increased speed
		for (int i = 0; i < MOVE_FREQUENCY_PER_CYCLE; i++) {
			movePattern.add(INCREASED_VERTICAL_VELOCITY);
			movePattern.add(-INCREASED_VERTICAL_VELOCITY);
			movePattern.add(ZERO);
		}
		Collections.shuffle(movePattern);
	}

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

	private boolean shieldExhausted() {
		return framesWithShieldActivated == MAX_FRAMES_WITH_SHIELD;
	}

	private void activateShield() {
		isShielded = true;
	}

	private void deactivateShield() {
		isShielded = false;
		framesWithShieldActivated = 0;
	}

	public boolean getIsShielded() {
		return isShielded;
	}

}

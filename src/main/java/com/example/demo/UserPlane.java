package com.example.demo;

import java.util.List;
import java.util.ArrayList;

public class UserPlane extends FighterPlane {

	private static final String IMAGE_NAME = "userplane.png";
	private static final double X_LEFT_BOUND = 0.0;
	private static final double X_RIGHT_BOUND = 1000.0;
	private static final double Y_UPPER_BOUND_OFFSET = 400.0;
	private static final double Y_UPPER_BOUND = -25 - Y_UPPER_BOUND_OFFSET;
	private static final double Y_LOWER_BOUND = 775.0 - Y_UPPER_BOUND_OFFSET;
	private static final double INITIAL_X_POSITION = 5.0;
	private static final double INITIAL_Y_POSITION = 400.0;
	private static final int IMAGE_HEIGHT = 60;
	private static final int HORIZONTAL_VELOCITY = 8;
	private static final int VERTICAL_VELOCITY = 8;
	private static final long FIRE_RATE = 100; // Fire rate in milliseconds
	private long lastFiredTime = 0; // Stores the last time the player fired a projectile
	private int velocityMultiplierY = 0;
	private double horizontalVelocity = 0;
	private int numberOfKills;
	public enum FiringMode {
		SINGLE, SPREAD
	}

	private static final double SPEED_BOOST_MULTIPLIER = 1.5;
	private static final double BOOST_DRAIN_RATE = 1; // Energy drained per frame while boosting
	private static final double BOOST_RECHARGE_RATE = 1.5; // Energy recharged per frame
	private static final int MAX_BOOST_ENERGY = 100;
	private static final int BOOST_COOLDOWN_TIME = 1500; // Cooldown time in milliseconds

	private int currentBoostEnergy = MAX_BOOST_ENERGY;
	private boolean isSpeedBoostActive = false;
	private long lastDepletedTime = 0; // Track the last time boost was depleted

	private FiringMode currentFiringMode = FiringMode.SINGLE;

	public UserPlane(int initialHealth) {
		super(IMAGE_NAME, IMAGE_HEIGHT, INITIAL_X_POSITION, INITIAL_Y_POSITION, initialHealth);
	}

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

	@Override
	public void updateActor() {
		updatePosition();
		drainBoostEnergy();  // Deplete boost when active
		rechargeBoostEnergy();  // Handle boost recharge
	}

	@Override
	public ActiveActorDestructible fireProjectile() {
		long currentTime = System.currentTimeMillis(); // Get the current time in milliseconds

		// Check if enough time has passed since the last shot
		if (currentTime - lastFiredTime >= FIRE_RATE) {
			// Fire the projectile
			double projectileX = getTranslateX() + getBoundsInLocal().getWidth();
			double projectileY = getTranslateY() + Y_UPPER_BOUND_OFFSET + (getBoundsInLocal().getHeight() / 2);

			// Update last fired time
			lastFiredTime = currentTime;

			return new UserProjectile(projectileX, projectileY);
		}

		// Do nothing if the player tries to fire before the cooldown
		return null;
	}

	public List<ActiveActorDestructible> fireSpreadProjectile() {
		long currentTime = System.currentTimeMillis();


		// Check if enough time has passed since the last shot
		if (currentTime - lastFiredTime >= FIRE_RATE) {
			// Fire the spread projectiles
			List<ActiveActorDestructible> spreadBullets = new ArrayList<>();
			double baseX = getTranslateX() + getBoundsInLocal().getWidth();
			double baseY = getTranslateY() + Y_UPPER_BOUND_OFFSET + getBoundsInLocal().getHeight() / 2;

			spreadBullets.add(new SpreadProjectile(baseX, baseY, 10, 0)); // Center bullet
			spreadBullets.add(new SpreadProjectile(baseX, baseY, 10, -5)); // Upward bullet
			spreadBullets.add(new SpreadProjectile(baseX, baseY, 10, 5)); // Downward bullet

			// Update last fired time
			lastFiredTime = currentTime;

			return spreadBullets;
		}

		// Do nothing if the player tries to fire before the cooldown
		return null;
	}

	public List<ActiveActorDestructible> fire() {
		if (currentFiringMode == FiringMode.SINGLE) {
			ActiveActorDestructible singleProjectile = fireProjectile();
			return singleProjectile != null ? List.of(singleProjectile) : List.of();
		} else if (currentFiringMode == FiringMode.SPREAD) {
			return fireSpreadProjectile();
		}
		return List.of();
	}

	public void setFiringMode(FiringMode mode) {
		this.currentFiringMode = mode;
	}

	public FiringMode getFiringMode() {
		return currentFiringMode;
	}

	public void move(int direction, boolean isVertical) {
		if (isVertical) {
			velocityMultiplierY = direction; // -1 for up, 1 for down, 0 to stop
		} else {
			horizontalVelocity = direction * HORIZONTAL_VELOCITY; // -1 for left, 1 for right, 0 to stop
		}
	}

	public void moveHorizontally(double velocity) {
		horizontalVelocity = velocity; // Set the velocity
	}

	public void setSpeedBoost(boolean isActive) {
		if (isActive && currentBoostEnergy > 0) {
			isSpeedBoostActive = true;
		} else {
			isSpeedBoostActive = false;
			lastDepletedTime = System.currentTimeMillis(); // Track the time when boost was depleted
		}
	}

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

	private void drainBoostEnergy() {
		if (isSpeedBoostActive && currentBoostEnergy > 0) {
			currentBoostEnergy -= BOOST_DRAIN_RATE;
			if (currentBoostEnergy <= 0) {
				currentBoostEnergy = 0;
				setSpeedBoost(false); // Disable boost if energy is depleted
			}
		}
	}

	public int getNumberOfKills() {
		return numberOfKills;
	}

	public void incrementKillCount() {
		numberOfKills++;
	}

	public static double getMaxBoostEnergy() {
		return MAX_BOOST_ENERGY;
	}

	public int getBoostEnergy() {
		return currentBoostEnergy;
	}

}

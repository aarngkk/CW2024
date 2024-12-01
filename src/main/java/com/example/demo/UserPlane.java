package com.example.demo;

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
	private int velocityMultiplierY = 0;
	private double horizontalVelocity = 0;
	private int numberOfKills;

	public UserPlane(int initialHealth) {
		super(IMAGE_NAME, IMAGE_HEIGHT, INITIAL_X_POSITION, INITIAL_Y_POSITION, initialHealth);
	}

	@Override
	public void updatePosition() {
		// Handle vertical movement
		double newYPosition = getTranslateY() + (VERTICAL_VELOCITY * velocityMultiplierY);
		if (newYPosition >= Y_UPPER_BOUND && newYPosition <= Y_LOWER_BOUND) {
			setTranslateY(newYPosition);
		}

		// Handle horizontal movement
		double newXPosition = getTranslateX() + horizontalVelocity;
		if (newXPosition >= X_LEFT_BOUND && newXPosition <= X_RIGHT_BOUND) {
			setTranslateX(newXPosition);
		}
	}

	@Override
	public void updateActor() {
		updatePosition();
	}
	
	@Override
	public ActiveActorDestructible fireProjectile() {
		// Get the current position of the user plane
		double projectileX = getTranslateX() + getBoundsInLocal().getWidth();
		double projectileY = getTranslateY() + Y_UPPER_BOUND_OFFSET + (getBoundsInLocal().getHeight() / 2); // Center vertically with offset
		return new UserProjectile(projectileX, projectileY);
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

	public int getNumberOfKills() {
		return numberOfKills;
	}

	public void incrementKillCount() {
		numberOfKills++;
	}

}

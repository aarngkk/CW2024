package com.finalflight.game.ui;

import javafx.scene.Group;

public class LevelView {
	
	private static final double HEART_DISPLAY_X_POSITION = 5;
	private static final double HEART_DISPLAY_Y_POSITION = 5;
	private static final int WIN_IMAGE_X_POSITION = 450;
	private static final int WIN_IMAGE_Y_POSITION = 200;
	private static final int LOSS_SCREEN_X_POSITION = 450;
	private static final int LOSS_SCREEN_Y_POSITION = 100;
	private final Group root;
	private final WinImage winImage;
	private final GameOverImage gameOverImage;
	private final HeartDisplay heartDisplay;
	
	public LevelView(Group root, int heartsToDisplay) {
		this.root = root;
		this.heartDisplay = new HeartDisplay(HEART_DISPLAY_X_POSITION, HEART_DISPLAY_Y_POSITION, heartsToDisplay);
		this.winImage = new WinImage(WIN_IMAGE_X_POSITION, WIN_IMAGE_Y_POSITION);
		this.gameOverImage = new GameOverImage(LOSS_SCREEN_X_POSITION, LOSS_SCREEN_Y_POSITION);
	}

	/**
	 * Dynamically displays hearts based on the current health value.
	 * @param currentHealth The player's current health.
	 */
	public void showHeartDisplay(int currentHealth) {
		// Clear any existing hearts
		heartDisplay.clearHearts();

		// Add hearts based on the current health
		for (int i = 0; i < currentHealth; i++) {
			heartDisplay.addHeart();
		}

		// Add the heart display to the root if not already added
		if (!root.getChildren().contains(heartDisplay.getContainer())) {
			root.getChildren().add(heartDisplay.getContainer());
		}
	}

	public void showWinImage() {
		root.getChildren().add(winImage);
		winImage.showWinImage();
	}
	
	public void showGameOverImage() {
		root.getChildren().add(gameOverImage);
	}
	
	public void removeHearts(int heartsRemaining) {
		int currentNumberOfHearts = heartDisplay.getContainer().getChildren().size();
		for (int i = 0; i < currentNumberOfHearts - heartsRemaining; i++) {
			heartDisplay.removeHeart();
		}
	}

}

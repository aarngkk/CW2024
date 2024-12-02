package com.example.demo;

import javafx.scene.control.ProgressBar;

public class LevelTwo extends LevelParent {

	private static final String BACKGROUND_IMAGE_NAME = "/com/example/demo/images/background2.gif";
	private static final int PLAYER_INITIAL_HEALTH = 5;
	private static final int KILLS_TO_ADVANCE = 0;
	private final Boss boss;
	private LevelViewLevelTwo levelView;
	private ProgressBar bossHealthBar;

	public LevelTwo(double screenHeight, double screenWidth) {
		super(BACKGROUND_IMAGE_NAME, screenHeight, screenWidth, PLAYER_INITIAL_HEALTH, KILLS_TO_ADVANCE);
		boss = new Boss();
	}

	@Override
	protected void initializeGameObjects() {
		initializeBossHealthBar();
	}

	@Override
	protected void initializeFriendlyUnits() {
		getRoot().getChildren().add(getUser());
	}

	private void initializeBossHealthBar() {
		bossHealthBar = new ProgressBar();
		bossHealthBar.setPrefWidth(500);
		bossHealthBar.setPrefHeight(25);
		bossHealthBar.getStyleClass().add("health-bar");

		// Position the health bar at the top center of the screen
		bossHealthBar.setLayoutX((getScreenWidth() - bossHealthBar.getPrefWidth()) / 2);
		bossHealthBar.setLayoutY(30);

		// Set the initial value based on the boss's health
		updateBossHealthBar();

		// Add the health bar to the root
		getRoot().getChildren().add(bossHealthBar);
	}

	private void updateBossHealthBar() {
		// Set progress based on boss's health as a percentage
		double healthPercentage = (double) boss.getHealth() / boss.getMaxHealth();
		bossHealthBar.setProgress(healthPercentage);
	}

	@Override
	protected void checkIfGameOver() {
		if (userIsDestroyed()) {
			loseGame();
		}
		else if (boss.isDestroyed()) {
			winGame();
		}
	}

	@Override
	protected void spawnEnemyUnits() {
		if (getCurrentNumberOfEnemies() == 0) {
			addEnemyUnit(boss);
		}
	}

	@Override
	protected LevelView instantiateLevelView() {
		levelView = new LevelViewLevelTwo(getRoot(), PLAYER_INITIAL_HEALTH);
		return levelView;
	}

	@Override
	protected void updateHUD() {
		updateBossHealthBar();;
	}

}

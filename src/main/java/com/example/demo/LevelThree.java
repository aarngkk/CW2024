package com.example.demo;

import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LevelThree extends LevelParent {

	private static final String BACKGROUND_IMAGE_NAME = "/com/example/demo/images/background2.gif";
	private static final int PLAYER_INITIAL_HEALTH = 5;
	private static final int KILLS_TO_ADVANCE = 0;
	private static final int INITIAL_TOTAL_ENEMIES = 2;
	private static final double INITIAL_ENEMY_SPAWN_PROBABILITY = 0.10;
	private int totalEnemies = INITIAL_TOTAL_ENEMIES;
	private double enemySpawnProbability = INITIAL_ENEMY_SPAWN_PROBABILITY;
	private final Boss boss;
	private LevelViewLevelTwo levelView;
	private ProgressBar bossHealthBar;

	public LevelThree(double screenHeight, double screenWidth) {
		super(BACKGROUND_IMAGE_NAME, screenHeight, screenWidth, PLAYER_INITIAL_HEALTH, KILLS_TO_ADVANCE);
		boss = new Boss();
	}

	@Override
	protected void initializeHUD() {
		initializeFiringModeText();
		initializeBoostBar();
		initializeBossHealthBar();
	}

	@Override
	protected double getFiringModeTextYPosition() {
		return 80; // Adjusted Y position for firing mode text
	}

	@Override
	protected double getBoostBarYPosition() {
		return 100; // Adjusted Y position for boost bar
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

		bossHealthBar.getStyleClass().removeAll("normal-bar", "shielded-bar");

		// Set the appropriate CSS when the boss is shielded
		if (boss.getIsShielded()) {
			bossHealthBar.getStyleClass().add("shielded-bar");
		} else {
			bossHealthBar.getStyleClass().add("normal-bar");
		}
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
		// Update enemy spawn rate based on the boss's health
		updateEnemySpawnRate();

		// Add the boss if no enemies exist
		if (getCurrentNumberOfEnemies() == 0) {
			addEnemyUnit(boss);
		}

		// Spawn regular enemy planes
		int currentNumberOfEnemies = getCurrentNumberOfEnemies() - 1;
		for (int i = 0; i < totalEnemies - currentNumberOfEnemies; i++) {
			if (Math.random() < enemySpawnProbability) {
				double newEnemyInitialYPosition = Math.random() * getEnemyMaximumYPosition();
				ActiveActorDestructible newEnemy = new EnemyPlane(getScreenWidth(), newEnemyInitialYPosition);
				addEnemyUnit(newEnemy);
			}
		}
	}

	private void updateEnemySpawnRate() {
		// Increase the number of enemies and spawn probability based on boss health
		double bossHealthPercentage = (double) boss.getHealth() / boss.getMaxHealth();

		if (bossHealthPercentage <= 0.2) {
			// Spawn even more enemies at 20% health
			totalEnemies = 4;  // Increase the total enemies even more
			enemySpawnProbability = 0.2;  // Increase enemy spawn probability more
		} else if (bossHealthPercentage <= 0.5) {
			// Spawn more enemies at 50% health
			totalEnemies = 3;  // Increase the total enemies
			enemySpawnProbability = 0.15;  // Increase enemy spawn probability slightly
		} else {
			// Default values above 50% health
			totalEnemies = INITIAL_TOTAL_ENEMIES;
			enemySpawnProbability = INITIAL_ENEMY_SPAWN_PROBABILITY;
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

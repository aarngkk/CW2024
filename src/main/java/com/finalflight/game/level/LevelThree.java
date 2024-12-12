package com.finalflight.game.level;

import com.finalflight.game.gameobjects.ActiveActorDestructible;
import com.finalflight.game.gameobjects.AdvancedEnemyPlane;
import com.finalflight.game.gameobjects.Boss;
import com.finalflight.game.gameobjects.EnemyPlane;
import com.finalflight.game.ui.BossExplosion;
import com.finalflight.game.ui.LevelView;
import com.finalflight.game.ui.LevelViewLevelThree;
import com.finalflight.game.ui.ShieldImage;
import javafx.scene.control.ProgressBar;

public class LevelThree extends LevelParent {

	private static final String BOSS_LEVEL_MUSIC = "/com/finalflight/game/audio/bosslevel.mp3";
	private static final String BACKGROUND_IMAGE_NAME = "/com/finalflight/game/images/background3.gif";
	private static final int PLAYER_INITIAL_HEALTH = 5;
	private static final int KILLS_TO_ADVANCE = 0;
	private static final int INITIAL_TOTAL_ENEMIES = 2;
	private static final double INITIAL_ENEMY_SPAWN_PROBABILITY = 0.02;
	private int totalEnemies = INITIAL_TOTAL_ENEMIES;
	private double enemySpawnProbability = INITIAL_ENEMY_SPAWN_PROBABILITY;
	private final Boss boss;
	private LevelViewLevelThree levelView;
	private ProgressBar bossHealthBar;
	private ShieldImage shieldImage;
	private BossExplosion bossExplosion;

	public LevelThree(double screenHeight, double screenWidth) {
		super(BACKGROUND_IMAGE_NAME, screenHeight, screenWidth, PLAYER_INITIAL_HEALTH, KILLS_TO_ADVANCE);
		boss = new Boss();
		shieldImage = new ShieldImage(boss.getLayoutX(), boss.getLayoutY());
		switchMusic(BOSS_LEVEL_MUSIC, true);
	}

	@Override
	protected void updateScene() {
		super.updateScene();
		updateShield();
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

	private void initializeBossShield() {
		getRoot().getChildren().add(shieldImage);
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
			bossExplode();
			winGame();
		}
	}

	private void bossExplode() {
		bossExplosion = new BossExplosion(boss.getLayoutX() + boss.getTranslateX(), boss.getLayoutY() + boss.getTranslateY());
		getRoot().getChildren().add(bossExplosion);
		bossExplosion.playExplosionSound();
	}

	@Override
	protected void spawnEnemyUnits() {
		// Update enemy spawn rate based on the boss's health
		updateEnemySpawnRate();

		// Add the boss if no enemies exist
		if (getCurrentNumberOfEnemies() == 0) {
			addEnemyUnit(boss);
			initializeBossShield();
		}

		// Spawn enemy planes
		int currentNumberOfEnemies = getCurrentNumberOfEnemies() - 1;
		for (int i = 0; i < totalEnemies - currentNumberOfEnemies; i++) {
			if (Math.random() < enemySpawnProbability) {
				double newEnemyInitialYPosition = Math.random() * getEnemyMaximumYPosition();
				double spawnProbability = Math.random();

				if (spawnProbability < 0.65) {
					// Spawn a regular enemy
					ActiveActorDestructible newEnemy = new EnemyPlane(getScreenWidth(), newEnemyInitialYPosition);
					addEnemyUnit(newEnemy);
				} else {
					// Spawn an advanced enemy
					ActiveActorDestructible advancedEnemy = new AdvancedEnemyPlane(getScreenWidth(), newEnemyInitialYPosition);
					addEnemyUnit(advancedEnemy);
				}
			}
		}
	}

	private void updateEnemySpawnRate() {
		// Increase the number of enemies and spawn probability based on boss health
		double bossHealthPercentage = (double) boss.getHealth() / boss.getMaxHealth();

		if (bossHealthPercentage <= 0.2) {
			// Spawn even more enemies at 20% health
			totalEnemies = 4;  // Increase the total enemies even more
			enemySpawnProbability = 0.025;  // Increase enemy spawn probability more
		} else if (bossHealthPercentage <= 0.5) {
			// Spawn more enemies at 50% health
			totalEnemies = 3;  // Increase the total enemies
			enemySpawnProbability = 0.0225;  // Increase enemy spawn probability slightly
		} else {
			// Default values above 50% health
			totalEnemies = INITIAL_TOTAL_ENEMIES;
			enemySpawnProbability = INITIAL_ENEMY_SPAWN_PROBABILITY;
		}
	}

	private void updateShield(){
		if (shieldImage != null && boss != null) {
			// Update shield image position to match the boss's position
			shieldImage.setTranslateX(boss.getTranslateX() + shieldImage.getShieldXOffset());
			shieldImage.setTranslateY(boss.getTranslateY() + shieldImage.getShieldYOffset());

			// Show or hide the shield image based on the boss's shield state
			if (boss.getIsShielded()) {
				shieldImage.showShield();
			} else {
				shieldImage.hideShield();
			}
		}
	}

	@Override
	protected LevelView instantiateLevelView() {
		levelView = new LevelViewLevelThree(getRoot(), PLAYER_INITIAL_HEALTH);
		return levelView;
	}

	@Override
	protected void updateHUD() {
		updateBossHealthBar();;
	}

}

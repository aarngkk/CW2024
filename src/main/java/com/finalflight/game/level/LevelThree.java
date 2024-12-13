package com.finalflight.game.level;

import com.finalflight.game.gameobjects.*;
import com.finalflight.game.ui.BaseLevelView;
import com.finalflight.game.ui.LevelThreeView;
import javafx.scene.Scene;

public class LevelThree extends BaseLevel {

    private static final String BOSS_LEVEL_MUSIC = "/com/finalflight/game/audio/bosslevel.mp3";
    private static final String BACKGROUND_IMAGE_NAME = "/com/finalflight/game/images/background3.gif";
    private static final int PLAYER_INITIAL_HEALTH = 5;
    private static final int KILLS_TO_ADVANCE = 0;
    private static final int INITIAL_TOTAL_ENEMIES = 2;
    private static final double INITIAL_ENEMY_SPAWN_PROBABILITY = 0.02;
    private static final double LEVEL_THREE_HUD_BOOST_BAR_Y = 100;
    private static final double LEVEL_THREE_HUD_FIRING_MODE_Y = 80;
    private int totalEnemies = INITIAL_TOTAL_ENEMIES;
    private double enemySpawnProbability = INITIAL_ENEMY_SPAWN_PROBABILITY;
    private final BossPlane boss;
    private LevelThreeView levelView;

    public LevelThree(double screenHeight, double screenWidth) {
        super(BACKGROUND_IMAGE_NAME, screenHeight, screenWidth, PLAYER_INITIAL_HEALTH, KILLS_TO_ADVANCE);
        boss = new BossPlane();
        switchMusic(BOSS_LEVEL_MUSIC, true);
    }

    @Override
    public Scene initializeScene() {
        getScene().getStylesheets().add(getClass().getResource("/com/finalflight/game/css/styles.css").toExternalForm());
        initializeBackground();
        initializeFriendlyUnits();
        levelView.showHeartDisplay(getUser().getHealth());
        levelView.initializeHUD(getKillsToAdvance(), LEVEL_THREE_HUD_BOOST_BAR_Y, LEVEL_THREE_HUD_FIRING_MODE_Y);
        levelView.initializePauseMenu();
        getBackground().requestFocus();
        return getScene();
    }

    @Override
    protected void updateScene() {
        super.updateScene();
        levelView.updateShield(boss);
        updateHUD();
    }

    @Override
    protected void initializeFriendlyUnits() {
        getRoot().getChildren().add(getUser());
    }

    @Override
    protected void checkIfGameOver() {
        if (userIsDestroyed()) {
            loseGame();
        } else if (boss.isDestroyed()) {
            bossExplode();
            winGame();
        }
    }

    private void bossExplode() {
        double explosionX = boss.getLayoutX() + boss.getTranslateX();
        double explosionY = boss.getLayoutY() + boss.getTranslateY();
        levelView.showBossExplosion(explosionX, explosionY);
    }

    @Override
    protected void spawnEnemyUnits() {
        // Update enemy spawn rate based on the boss's health
        updateEnemySpawnRate();

        // Add the boss if no enemies exist
        if (getCurrentNumberOfEnemies() == 0) {
            addEnemyUnit(boss);
            levelView.initializeShield(boss);
        }

        // Spawn enemy planes
        int currentNumberOfEnemies = getCurrentNumberOfEnemies() - 1;
        for (int i = 0; i < totalEnemies - currentNumberOfEnemies; i++) {
            if (Math.random() < enemySpawnProbability) {
                double newEnemyInitialYPosition = Math.random() * getEnemyMaximumYPosition();
                double spawnProbability = Math.random();

                if (spawnProbability < 0.65) {
                    // Spawn a regular enemy
                    DestructibleGameObject newEnemy = new EnemyPlane(getScreenWidth(), newEnemyInitialYPosition);
                    addEnemyUnit(newEnemy);
                } else {
                    // Spawn an advanced enemy
                    DestructibleGameObject advancedEnemy = new AdvancedEnemyPlane(getScreenWidth(), newEnemyInitialYPosition);
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

    @Override
    protected BaseLevelView instantiateLevelView() {
        levelView = new LevelThreeView(getRoot(), PLAYER_INITIAL_HEALTH, this); // Save the view
        return levelView;
    }

    @Override
    protected void updateHUD() {
        double bossHealthPercentage = (double) boss.getHealth() / boss.getMaxHealth();
        boolean isBossShielded = boss.getIsShielded();
        levelView.updateBossHealthBar(bossHealthPercentage, isBossShielded);
        levelView.updateBoostBar(getUser().getBoostEnergy() / UserPlane.getMaxBoostEnergy());
        levelView.updateFiringMode(getUser().getFiringMode().toString());
    }

    @Override
    protected void updateKillCount() {
        // Do nothing since LevelThree does not need to track kill count
    }

}

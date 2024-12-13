/**
 * The {@code LevelThree} class represents the final level of the game, featuring
 * a boss fight and more challenging gameplay mechanics. It extends the {@link BaseLevel}
 * class and defines behaviors specific to Level Three, such as spawning enemies
 * based on the boss's health and managing the boss's unique characteristics.
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/level/LevelThree.java">LevelThree.java</a></p>
 */
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

    /**
     * Constructs a new {@code LevelThree} instance with the specified screen dimensions.
     * Initializes the boss and sets the music for the level.
     *
     * @param screenHeight the height of the game screen.
     * @param screenWidth  the width of the game screen.
     */
    public LevelThree(double screenHeight, double screenWidth) {
        super(BACKGROUND_IMAGE_NAME, screenHeight, screenWidth, PLAYER_INITIAL_HEALTH, KILLS_TO_ADVANCE);
        boss = new BossPlane();
        switchMusic(BOSS_LEVEL_MUSIC, true);
    }

    /**
     * Initializes the scene for Level Three. This includes setting the background,
     * adding stylesheets, initializing friendly units, and configuring the HUD.
     *
     * @return the {@link Scene} object representing the game scene for Level Three.
     */
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

    /**
     * Updates the scene during gameplay, including HUD updates and boss shield updates.
     */
    @Override
    protected void updateScene() {
        super.updateScene();
        levelView.updateShield(boss);
        updateHUD();
    }

    /**
     * Initializes the player's plane and adds it to the scene graph.
     */
    @Override
    protected void initializeFriendlyUnits() {
        getRoot().getChildren().add(getUser());
    }

    /**
     * Checks if the game is over. The game ends if the player's plane is destroyed,
     * resulting in a loss, or if the boss is destroyed, resulting in a win.
     */
    @Override
    protected void checkIfGameOver() {
        if (userIsDestroyed()) {
            loseGame();
        } else if (boss.isDestroyed()) {
            bossExplode();
            winGame();
        }
    }

    /**
     * Handles the boss's explosion effect upon being destroyed.
     * Calculates the position of the explosion and displays it.
     */
    private void bossExplode() {
        double explosionX = boss.getLayoutX() + boss.getTranslateX();
        double explosionY = boss.getLayoutY() + boss.getTranslateY();
        levelView.showBossExplosion(explosionX, explosionY);
    }

    /**
     * Spawns enemy units during the level. Adjusts the spawn rate and total enemies
     * based on the boss's remaining health. Also ensures the boss is added to the scene.
     */
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

    /**
     * Updates the enemy spawn rate and the total number of enemies based on the boss's health.
     * As the boss's health decreases, the difficulty increases by spawning more enemies.
     */
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

    /**
     * Creates and returns the level view for Level Three, including HUD and boss-specific UI.
     *
     * @return a {@link BaseLevelView} object for Level Three.
     */
    @Override
    protected BaseLevelView instantiateLevelView() {
        levelView = new LevelThreeView(getRoot(), PLAYER_INITIAL_HEALTH, this); // Save the view
        return levelView;
    }

    /**
     * Updates the HUD elements during gameplay, such as the boss's health bar,
     * the boost bar, and the player's firing mode.
     */
    @Override
    protected void updateHUD() {
        double bossHealthPercentage = (double) boss.getHealth() / boss.getMaxHealth();
        boolean isBossShielded = boss.getIsShielded();
        levelView.updateBossHealthBar(bossHealthPercentage, isBossShielded);
        levelView.updateBoostBar(getUser().getBoostEnergy() / UserPlane.getMaxBoostEnergy());
        levelView.updateFiringMode(getUser().getFiringMode().toString());
    }

    /**
     * Overrides the default kill count update behavior, as Level Three does not track kill counts.
     */
    @Override
    protected void updateKillCount() {
        // Do nothing since LevelThree does not need to track kill count
    }

}

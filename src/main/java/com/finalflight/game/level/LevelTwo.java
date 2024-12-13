/**
 * The {@code LevelTwo} class represents the second level of the game. It extends
 * the {@link BaseLevel} class and defines the behavior and characteristics specific
 * to Level Two, including enemy spawn logic, background image, and progression conditions.
 *
 * <p>This class is responsible for managing the gameplay elements in Level Two,
 * such as spawning regular and advanced enemies, checking game-over conditions,
 * and transitioning to the next level.</p>
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/level/LevelTwo.java">LevelTwo.java</a></p>
 */
package com.finalflight.game.level;

import com.finalflight.game.gameobjects.AdvancedEnemyPlane;
import com.finalflight.game.gameobjects.DestructibleGameObject;
import com.finalflight.game.gameobjects.EnemyPlane;
import com.finalflight.game.ui.BaseLevelView;

public class LevelTwo extends BaseLevel {

    private static final String BACKGROUND_IMAGE_NAME = "/com/finalflight/game/images/background2.gif";
    private static final String NEXT_LEVEL = "com.finalflight.game.level.LevelThree";
    private static final int TOTAL_ENEMIES = 7;
    private static final int KILLS_TO_ADVANCE = 15;
    private static final double ENEMY_SPAWN_PROBABILITY = 0.0175;
    private static final int PLAYER_INITIAL_HEALTH = 5;

    /**
     * Constructs a new {@code LevelTwo} instance with the specified screen dimensions.
     *
     * @param screenHeight the height of the game screen.
     * @param screenWidth  the width of the game screen.
     */
    public LevelTwo(double screenHeight, double screenWidth) {
        super(BACKGROUND_IMAGE_NAME, screenHeight, screenWidth, PLAYER_INITIAL_HEALTH, KILLS_TO_ADVANCE);
    }

    /**
     * Checks if the game is over based on the player's state.
     * If the player's plane is destroyed, the game is lost. If the kill target is reached,
     * the player advances to the next level.
     */
    @Override
    protected void checkIfGameOver() {
        if (userIsDestroyed()) {
            loseGame();
        } else if (userHasReachedKillTarget()) {
            goToNextLevel(NEXT_LEVEL);
        }
    }

    /**
     * Initializes friendly units in the level, such as the player's plane.
     * The player's plane is added to the scene graph at the start of the level.
     */
    @Override
    protected void initializeFriendlyUnits() {
        getRoot().getChildren().add(getUser());
    }

    /**
     * Spawns enemy units in the level. Regular enemies and advanced enemies are spawned
     * based on predefined probabilities and the current number of enemies on screen.
     * Regular enemies have an 80% spawn probability, while advanced enemies have a 20% probability.
     */
    @Override
    protected void spawnEnemyUnits() {
        int currentNumberOfEnemies = getCurrentNumberOfEnemies();
        for (int i = 0; i < TOTAL_ENEMIES - currentNumberOfEnemies; i++) {
            if (Math.random() < ENEMY_SPAWN_PROBABILITY) {
                double newEnemyInitialYPosition = Math.random() * getEnemyMaximumYPosition();
                double regularEnemySpawnProbability = Math.random();

                if (regularEnemySpawnProbability < 0.8) {
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
     * Instantiates the level view for Level Two, providing the necessary configuration
     * for the player's health, screen dimensions, and root pane.
     *
     * @return a {@link BaseLevelView} instance configured for Level Two.
     */
    @Override
    protected BaseLevelView instantiateLevelView() {
        return new BaseLevelView(getRoot(), PLAYER_INITIAL_HEALTH, getScreenWidth(), getScreenHeight(), this);
    }

    /**
     * Checks if the player has reached the required number of kills to advance to the next level.
     *
     * @return {@code true} if the player's kill count meets or exceeds the target,
     *         {@code false} otherwise.
     */
    private boolean userHasReachedKillTarget() {
        return getUser().getNumberOfKills() >= KILLS_TO_ADVANCE;
    }
}

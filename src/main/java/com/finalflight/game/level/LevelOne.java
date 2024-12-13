/**
 * Represents the first level in the game, introducing the player to basic mechanics and enemy interactions.
 * This level includes spawning enemy planes, checking for game over conditions, and transitioning to the next level.
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/level/LevelOne.java">LevelOne.java</a></p>
 */
package com.finalflight.game.level;

import com.finalflight.game.gameobjects.DestructibleGameObject;
import com.finalflight.game.gameobjects.EnemyPlane;
import com.finalflight.game.ui.BaseLevelView;

public class LevelOne extends BaseLevel {

    private static final String BACKGROUND_IMAGE_NAME = "/com/finalflight/game/images/background1.gif";
    private static final String NEXT_LEVEL = "com.finalflight.game.level.LevelTwo";
    private static final int TOTAL_ENEMIES = 5;
    private static final int KILLS_TO_ADVANCE = 10;
    private static final double ENEMY_SPAWN_PROBABILITY = 0.015;
    private static final int PLAYER_INITIAL_HEALTH = 5;

    /**
     * Constructs a new LevelOne instance with specified screen dimensions.
     *
     * @param screenHeight the height of the screen
     * @param screenWidth  the width of the screen
     */
    public LevelOne(double screenHeight, double screenWidth) {
        super(BACKGROUND_IMAGE_NAME, screenHeight, screenWidth, PLAYER_INITIAL_HEALTH, KILLS_TO_ADVANCE);
    }

    /**
     * Checks if the game is over by determining if the user plane has been destroyed
     * or if the kill target has been reached. Transitions to the next level or ends
     * the game accordingly.
     */
    @Override
    protected void checkIfGameOver() {
        if (userIsDestroyed()) {
            loseGame();
        } else if (userHasReachedKillTarget())
            goToNextLevel(NEXT_LEVEL);
    }

    /**
     * Initializes friendly units, adding the user's plane to the game scene.
     */
    @Override
    protected void initializeFriendlyUnits() {
        getRoot().getChildren().add(getUser());
    }

    /**
     * Spawns enemy units randomly based on a probability, ensuring the total number
     * of enemies does not exceed the defined limit.
     */
    @Override
    protected void spawnEnemyUnits() {
        int currentNumberOfEnemies = getCurrentNumberOfEnemies();
        for (int i = 0; i < TOTAL_ENEMIES - currentNumberOfEnemies; i++) {
            if (Math.random() < ENEMY_SPAWN_PROBABILITY) {
                double newEnemyInitialYPosition = Math.random() * getEnemyMaximumYPosition();
                DestructibleGameObject newEnemy = new EnemyPlane(getScreenWidth(), newEnemyInitialYPosition);
                addEnemyUnit(newEnemy);
            }
        }
    }

    /**
     * Creates and returns a view specific to this level, configured with initial settings.
     *
     * @return a {@link BaseLevelView} instance for LevelOne
     */
    @Override
    protected BaseLevelView instantiateLevelView() {
        return new BaseLevelView(getRoot(), PLAYER_INITIAL_HEALTH, getScreenWidth(), getScreenHeight(), this);
    }

    /**
     * Determines whether the user has reached the required number of kills to advance to the next level.
     *
     * @return true if the user has reached the kill target, false otherwise
     */
    private boolean userHasReachedKillTarget() {
        return getUser().getNumberOfKills() >= KILLS_TO_ADVANCE;
    }

}

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

    public LevelTwo(double screenHeight, double screenWidth) {
        super(BACKGROUND_IMAGE_NAME, screenHeight, screenWidth, PLAYER_INITIAL_HEALTH, KILLS_TO_ADVANCE);
    }

    @Override
    protected void checkIfGameOver() {
        if (userIsDestroyed()) {
            loseGame();
        } else if (userHasReachedKillTarget()) {
            goToNextLevel(NEXT_LEVEL);
        }
    }

    @Override
    protected void initializeFriendlyUnits() {
        getRoot().getChildren().add(getUser());
    }

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

    @Override
    protected BaseLevelView instantiateLevelView() {
        return new BaseLevelView(getRoot(), PLAYER_INITIAL_HEALTH);
    }

    private boolean userHasReachedKillTarget() {
        return getUser().getNumberOfKills() >= KILLS_TO_ADVANCE;
    }
}

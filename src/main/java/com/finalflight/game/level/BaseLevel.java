package com.finalflight.game.level;

import com.finalflight.game.audio.MusicPlayer;
import com.finalflight.game.gameobjects.DestructibleGameObject;
import com.finalflight.game.gameobjects.FighterPlane;
import com.finalflight.game.gameobjects.UserPlane;
import com.finalflight.game.visual.BaseLevelView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

/**
 * Represents an abstract base class for levels in the Final Flight game.
 * Handles core gameplay mechanics such as spawning enemies, handling collisions,
 * updating the scene, managing the game state, and rendering the level.
 *
 * <p>This class provides methods for initializing game elements, managing the game loop,
 * updating actors, and handling user interactions. Subclasses must provide specific
 * implementations for level-specific logic and UI components.</p>
 *
 * This class extends {@code Observable} to allow game state transitions to be observed.
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/level/BaseLevel.java</p>
 */
public abstract class BaseLevel extends Observable {

    private static final String LEVEL_MUSIC = "/com/finalflight/game/audio/levelmusic.mp3";
    private static final String WON_GAME_MUSIC = "/com/finalflight/game/audio/wongame.mp3";
    private static final String LOST_GAME_MUSIC = "/com/finalflight/game/audio/lostgame.mp3";
    private static final double SCREEN_HEIGHT_ADJUSTMENT = 100;
    private static final double HUD_BOOST_BAR_Y = 140;
    private static final double HUD_FIRING_MODE_Y = 120;
    private static final int MILLISECOND_DELAY = 50;
    private final double screenHeight;
    private final double screenWidth;
    private final double enemyMaximumYPosition;
    private final int killsToAdvance;

    private final Group root;
    private final Timeline timeline;
    private final UserPlane user;
    private final Scene scene;
    private final ImageView background;
    private final Set<KeyCode> activeKeys = new HashSet<>();

    private final List<DestructibleGameObject> friendlyUnits;
    private final List<DestructibleGameObject> enemyUnits;
    private final List<DestructibleGameObject> userProjectiles;
    private final List<DestructibleGameObject> enemyProjectiles;

    private int currentNumberOfEnemies;
    private final BaseLevelView levelView;
    private boolean isPaused = false;

    private EventHandler<KeyEvent> escapeKeyHandler;

    protected Text firingModeText;
    private MusicPlayer musicPlayer;

    /**
     * Creates a new BaseLevel instance.
     *
     * @param backgroundImageName the path to the background image for the level.
     * @param screenHeight        the height of the game screen.
     * @param screenWidth         the width of the game screen.
     * @param playerInitialHealth the initial health of the player.
     * @param killsToAdvance      the number of kills required to advance to the next level.
     */
    public BaseLevel(String backgroundImageName, double screenHeight, double screenWidth, int playerInitialHealth, int killsToAdvance) {
        this.root = new Group();
        this.scene = new Scene(root, screenWidth, screenHeight);
        this.timeline = new Timeline();
        this.user = new UserPlane(playerInitialHealth);
        this.killsToAdvance = killsToAdvance;
        this.friendlyUnits = new ArrayList<>();
        this.enemyUnits = new ArrayList<>();
        this.userProjectiles = new ArrayList<>();
        this.enemyProjectiles = new ArrayList<>();
        this.background = new ImageView(new Image(getClass().getResource(backgroundImageName).toExternalForm()));
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.enemyMaximumYPosition = screenHeight - SCREEN_HEIGHT_ADJUSTMENT;
        this.levelView = instantiateLevelView();
        this.currentNumberOfEnemies = 0;

        switchMusic(LEVEL_MUSIC, true);

        initializeTimeline();
        friendlyUnits.add(user);
    }

    /**
     * Abstract method to initialize friendly units for the level.
     * Must be implemented by subclasses.
     */
    protected abstract void initializeFriendlyUnits();

    /**
     * Abstract method to check if the game is over (win or lose condition).
     * Must be implemented by subclasses.
     */
    protected abstract void checkIfGameOver();

    /**
     * Abstract method to spawn enemy units specific to the level.
     * Must be implemented by subclasses.
     */
    protected abstract void spawnEnemyUnits();

    /**
     * Abstract method to create and return the {@code BaseLevelView} for the level.
     * Must be implemented by subclasses.
     *
     * @return the level view instance.
     */
    protected abstract BaseLevelView instantiateLevelView();

    /**
     * Initializes the scene for the level by setting up the background, friendly units,
     * HUD, and other visual elements. Also applies CSS styling to the scene.
     *
     * @return the initialized {@code Scene} for the level.
     */
    public Scene initializeScene() {
        scene.getStylesheets().add(getClass().getResource("/com/finalflight/game/css/styles.css").toExternalForm());
        initializeBackground();
        initializeFriendlyUnits();
        levelView.showHeartDisplay(user.getHealth());
        levelView.initializeHUD(killsToAdvance, HUD_BOOST_BAR_Y, HUD_FIRING_MODE_Y);
        levelView.initializePauseMenu();
        background.requestFocus();
        return scene;
    }

    /**
     * Starts the game by playing the game loop.
     */
    public void startGame() {
        background.requestFocus();
        timeline.play();
    }

    /**
     * Transitions to the next level by stopping the game loop and notifying observers.
     *
     * @param levelName the name of the next level to load.
     */
    public void goToNextLevel(String levelName) {
        timeline.stop();
        setChanged();
        UserPlane.resetHealth(user.getHealth());
        notifyObservers(levelName);
    }

    /**
     * Updates the game scene, including spawning enemies, handling collisions,
     * updating actors, and checking game-over conditions.
     */
    protected void updateScene() {
        spawnEnemyUnits();
        updateActors();
        generateEnemyFire();
        updateNumberOfEnemies();
        handleEnemyPenetration();
        handleUserProjectileCollisions();
        handleEnemyProjectileCollisions();
        handlePlaneCollisions();
        removeAllDestroyedActors();
        updateKillCount();
        updateHUD();
        updateLevelView();
        checkIfGameOver();
    }

    /**
     * Handles updating the HUD with the player's kill count, boost bar status,
     * and firing mode.
     */
    protected void updateHUD() {
        levelView.updateKillCounter(user.getNumberOfKills(), killsToAdvance);
        levelView.updateBoostBar(user.getBoostEnergy() / UserPlane.getMaxBoostEnergy());
        levelView.updateFiringMode(user.getFiringMode().toString());
    }

    /**
     * Updates the user's kill count by comparing the number of enemies at the start of the frame
     * with the current number of enemies. For each enemy destroyed, the user's kill count is incremented.
     */
    protected void updateKillCount() {
        int killsThisFrame = currentNumberOfEnemies - enemyUnits.size();
        for (int i = 0; i < killsThisFrame; i++) {
            user.incrementKillCount();
        }
    }

    /**
     * Initializes the game timeline, setting it to run indefinitely with a fixed delay
     * for each frame. The timeline is linked to the game loop, which updates the scene
     * on each tick.
     */
    private void initializeTimeline() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame gameLoop = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> updateScene());
        timeline.getKeyFrames().add(gameLoop);
    }

    /**
     * Configures the game background, including dimensions, key handlers for user interaction,
     * and event listeners for pause functionality. The background is added to the scene graph.
     */
    protected void initializeBackground() {
        background.setFocusTraversable(true);
        background.setFitHeight(screenHeight);
        background.setFitWidth(screenWidth);

        // ESC handler for pause
        escapeKeyHandler = e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                togglePause();
            }
        };
        background.addEventHandler(KeyEvent.KEY_PRESSED, escapeKeyHandler);

        background.setOnKeyPressed(e -> {
            activeKeys.add(e.getCode());
            if (e.getCode() == KeyCode.SHIFT) {
                user.setSpeedBoost(true);
            }
            processKeyPress();
        });

        background.setOnKeyReleased(e -> {
            activeKeys.remove(e.getCode());
            if (e.getCode() == KeyCode.SHIFT) {
                user.setSpeedBoost(false);
            }
            stop(); // Stop movement when key is released
        });

        root.getChildren().add(background);
    }

    /**
     * Processes user key presses and maps them to corresponding game actions,
     * such as movement, firing projectiles, toggling speed boost, or switching firing modes.
     */
    private void processKeyPress() {
        for (KeyCode key : activeKeys) {
            switch (key) {
                case UP, W -> user.move(-1, true);   // UP or W for moving up
                case DOWN, S -> user.move(1, true);   // DOWN or S for moving down
                case LEFT, A -> user.move(-1, false); // LEFT or A for moving left
                case RIGHT, D -> user.move(1, false); // RIGHT or D for moving right
                case SPACE -> fireProjectile();       // SPACE for firing
                case DIGIT1 -> {
                    user.setFiringMode(UserPlane.FiringMode.SINGLE);
                    firingModeText.setText("Mode: SINGLE");
                }
                case DIGIT2 -> {
                    user.setFiringMode(UserPlane.FiringMode.SPREAD);
                    firingModeText.setText("Mode: SPREAD");
                }
                case SHIFT -> user.setSpeedBoost(true); // Enable speed boost
                case DIGIT3 -> {
                    user.setFiringMode(UserPlane.FiringMode.HEAVY);
                    firingModeText.setText("Mode: HEAVY");
                }
            }
        }
    }

    /**
     * Stops user movement in all directions by resetting the movement states
     * based on the currently active keys.
     */
    private void stop() {
        // Prioritize vertical movement based on active keys
        if (activeKeys.contains(KeyCode.UP) || activeKeys.contains(KeyCode.W)) {
            user.move(-1, true); // Move up
        } else if (activeKeys.contains(KeyCode.DOWN) || activeKeys.contains(KeyCode.S)) {
            user.move(1, true); // Move down
        } else {
            user.move(0, true); // Stop vertical movement
        }

        // Prioritize horizontal movement based on active keys
        if (activeKeys.contains(KeyCode.LEFT) || activeKeys.contains(KeyCode.A)) {
            user.move(-1, false); // Move left
        } else if (activeKeys.contains(KeyCode.RIGHT) || activeKeys.contains(KeyCode.D)) {
            user.move(1, false); // Move right
        } else {
            user.move(0, false); // Stop horizontal movement
        }
    }


    /**
     * Toggles the game's pause state. If the game is paused, it resumes; otherwise, it pauses.
     * Updates the level view and applies visual effects such as blur to indicate the pause state.
     */
    private void togglePause() {
        if (isPaused) {
            resumeGame();  // Resume the game if it's currently paused
            levelView.hidePauseMenu();
            levelView.applyPauseBlurEffect(false); // Remove blur effect
        } else {
            pauseGame();   // Pause the game if it's running
            levelView.showPauseMenu();
            levelView.applyPauseBlurEffect(true); // Apply blur effect
            levelView.bringPauseElementsToFront();
        }
    }

    /**
     * Pauses the game by stopping the timeline and fading out the background music.
     */
    private void pauseGame() {
        isPaused = true;
        timeline.pause();  // Stop the game loop

        // Fade out and pause music
        musicPlayer.fadeOutMusic(1.0, 0.05, null);
    }

    /**
     * Resumes the game by restarting the timeline, fading in the background music,
     * and re-enabling the ESC key functionality.
     */
    private void resumeGame() {
        isPaused = false;
        timeline.play();  // Restart the game loop

        // Fade music in
        musicPlayer.fadeInMusic(1.0, 0.2);
        background.requestFocus();

        // Re-enable ESC key functionality
        enableEscapeKey();
    }

    /**
     * Fires a projectile from the user's plane. Adds the projectile to the scene graph
     * and tracks it in the list of user projectiles.
     */
    private void fireProjectile() {
        List<DestructibleGameObject> projectiles = user.fire();
        for (DestructibleGameObject projectile : projectiles) {
            root.getChildren().add(projectile);
            userProjectiles.add(projectile);
        }
    }

    /**
     * Generates enemy projectiles by invoking the fire method for each enemy plane
     * and adding the projectiles to the scene.
     */
    private void generateEnemyFire() {
        enemyUnits.forEach(enemy -> spawnEnemyProjectile(((FighterPlane) enemy).fireProjectile()));
    }

    /**
     * Spawns a single enemy projectile by adding it to the scene graph and tracking it
     * in the list of enemy projectiles.
     *
     * @param projectile The projectile to spawn.
     */
    private void spawnEnemyProjectile(DestructibleGameObject projectile) {
        if (projectile != null) {
            root.getChildren().add(projectile);
            enemyProjectiles.add(projectile);
        }
    }

    /**
     * Updates all actors in the game, including friendly units, enemy units, and projectiles.
     * Each actor's state is refreshed.
     */
    private void updateActors() {
        friendlyUnits.forEach(DestructibleGameObject::updateActor);
        enemyUnits.forEach(DestructibleGameObject::updateActor);
        userProjectiles.forEach(DestructibleGameObject::updateActor);
        enemyProjectiles.forEach(DestructibleGameObject::updateActor);
    }

    /**
     * Removes all destroyed actors from the scene graph and the respective tracking lists.
     */
    private void removeAllDestroyedActors() {
        removeDestroyedActors(friendlyUnits);
        removeDestroyedActors(enemyUnits);
        removeDestroyedActors(userProjectiles);
        removeDestroyedActors(enemyProjectiles);
    }

    /**
     * Removes all destroyed actors from a given list and their corresponding nodes
     * from the scene graph.
     *
     * @param actors The list of actors to process.
     */
    private void removeDestroyedActors(List<DestructibleGameObject> actors) {
        List<DestructibleGameObject> destroyedActors = actors.stream().filter(DestructibleGameObject::isDestroyed)
                .toList();
        root.getChildren().removeAll(destroyedActors);
        actors.removeAll(destroyedActors);
    }

    /**
     * Handles collisions between friendly units and enemy units, applying damage
     * to both upon collision.
     */
    private void handlePlaneCollisions() {
        handleCollisions(friendlyUnits, enemyUnits);
    }

    /**
     * Handles collisions between user projectiles and enemy units, applying damage
     * to both upon collision.
     */
    private void handleUserProjectileCollisions() {
        handleCollisions(userProjectiles, enemyUnits);
    }

    /**
     * Handles collisions between enemy projectiles and friendly units, applying damage
     * to both upon collision.
     */
    private void handleEnemyProjectileCollisions() {
        handleCollisions(enemyProjectiles, friendlyUnits);
    }

    /**
     * Processes collisions between two groups of actors. If two actors collide,
     * both take damage.
     *
     * @param actors1 The first list of actors.
     * @param actors2 The second list of actors.
     */
    private void handleCollisions(List<DestructibleGameObject> actors1,
                                  List<DestructibleGameObject> actors2) {
        for (DestructibleGameObject actor : actors2) {
            for (DestructibleGameObject otherActor : actors1) {
                if (actor.getBoundsInParent().intersects(otherActor.getBoundsInParent())) {
                    actor.takeDamage();
                    otherActor.takeDamage();
                }
            }
        }
    }

    /**
     * Handles enemy penetration by applying damage to the user and destroying the enemy
     * if it moves beyond the user's defenses.
     */
    private void handleEnemyPenetration() {
        for (DestructibleGameObject enemy : enemyUnits) {
            if (enemyHasPenetratedDefenses(enemy)) {
                user.takeDamage();
                enemy.destroy();
            }
        }
    }

    /**
     * Updates the level view, such as removing hearts from the user's health indicator.
     */
    private void updateLevelView() {
        levelView.removeHearts(user.getHealth());
    }

    /**
     * Checks if an enemy has penetrated the user's defenses.
     *
     * @param enemy The enemy to check.
     * @return True if the enemy has penetrated the defenses, false otherwise.
     */
    private boolean enemyHasPenetratedDefenses(DestructibleGameObject enemy) {
        return Math.abs(enemy.getTranslateX()) > screenWidth;
    }

    /**
     * Displays the win screen and stops the game loop.
     */
    protected void winGame() {
        timeline.stop();
        levelView.clearActorsAfterWin(enemyUnits, enemyProjectiles, userProjectiles);
        levelView.displayGameWin();
        switchMusic(WON_GAME_MUSIC, false);

        // Disable ESC key functionality
        disableEscapeKey();
    }

    /**
     * Displays the lose screen and stops the game loop.
     */
    protected void loseGame() {
        timeline.stop();
        levelView.displayGameOver();
        switchMusic(LOST_GAME_MUSIC, false);

        // Disable ESC key functionality
        disableEscapeKey();
    }

    /**
     * Restarts the game by resetting the state, clearing actors, and transitioning to LevelOne.
     */
    protected void restartGame() {
        timeline.stop(); // Stop the current game loop
        if (user != null) {
            UserPlane.resetHealth(5); // Reset health to default
        }
        friendlyUnits.clear();
        enemyUnits.clear();
        userProjectiles.clear();
        enemyProjectiles.clear();
        root.getChildren().clear();
        if (musicPlayer != null) {
            musicPlayer.fadeInMusic(1.0, 0.2);
        }
        setChanged();
        notifyObservers("com.finalflight.game.level.LevelOne"); // Transition to LevelOne
    }

    /**
     * Exits the game.
     */
    private void quitGame() {
        System.exit(0);
    }

    /**
     * Disables the ESC key functionality by removing its event handler.
     */
    private void disableEscapeKey() {
        background.removeEventHandler(KeyEvent.KEY_PRESSED, escapeKeyHandler);
    }

    /**
     * Enables the ESC key functionality by adding its event handler.
     */
    private void enableEscapeKey() {
        background.addEventHandler(KeyEvent.KEY_PRESSED, escapeKeyHandler);
    }

    /**
     * Switches the background music for the level.
     *
     * @param newMusicFile the path to the new music file.
     * @param shouldLoop   {@code true} if the music should loop; {@code false} otherwise.
     */
    protected void switchMusic(String newMusicFile, boolean shouldLoop) {
        if (musicPlayer != null) {
            musicPlayer.stopMusic(); // Stop the current music
        }
        musicPlayer = MusicPlayer.getInstance(newMusicFile);
        musicPlayer.playMusic(shouldLoop); // Play the new music
    }

    /**
     * Retrieves the current player's {@code UserPlane} instance.
     *
     * @return the player's plane.
     */
    protected UserPlane getUser() {
        return user;
    }

    /**
     * Retrieves the game root group that contains all scene nodes.
     *
     * @return the root group.
     */
    protected Group getRoot() {
        return root;
    }

    /**
     * Gets the current number of enemies in the game.
     *
     * @return The number of active enemies.
     */
    protected int getCurrentNumberOfEnemies() {
        return enemyUnits.size();
    }

    /**
     * Adds an enemy unit to the level and displays it in the game scene.
     *
     * @param enemy the enemy unit to add.
     */
    protected void addEnemyUnit(DestructibleGameObject enemy) {
        enemyUnits.add(enemy);
        root.getChildren().add(enemy);
    }

    /**
     * Retrieves the maximum Y-coordinate position for enemies.
     *
     * @return the maximum Y-coordinate for enemy movement.
     */
    protected double getEnemyMaximumYPosition() {
        return enemyMaximumYPosition;
    }

    /**
     * Retrieves the width of the game screen.
     *
     * @return the screen width.
     */
    public double getScreenWidth() {
        return screenWidth;
    }

    /**
     * Retrieves the height of the game screen.
     *
     * @return the screen height.
     */
    public double getScreenHeight() {
        return screenHeight;
    }

    /**
     * Retrieves the scene for the level.
     *
     * @return the scene instance.
     */
    protected Scene getScene() {
        return scene;
    }

    /**
     * Retrieves the number of kills required to advance to the next level.
     *
     * @return the number of kills required to advance.
     */
    protected int getKillsToAdvance() {
        return killsToAdvance;
    }

    /**
     * Retrieves the background image view for the level.
     *
     * @return the background image view.
     */
    protected ImageView getBackground() {
        return background;
    }

    /**
     * Checks if the user's plane has been destroyed.
     *
     * @return True if the user's plane is destroyed, false otherwise.
     */
    protected boolean userIsDestroyed() {
        return user.isDestroyed();
    }

    /**
     * Updates the number of active enemies in the level.
     */
    private void updateNumberOfEnemies() {
        currentNumberOfEnemies = enemyUnits.size();
    }

    /**
     * Toggles the game state between paused and running.
     */
    public void publicTogglePause() {
        togglePause();
    }

    /**
     * Restarts the game by resetting the state and transitioning to the starting level.
     */
    public void publicRestartGame() {
        restartGame();
    }

    /**
     * Exits the game application.
     */
    public void publicQuitGame() {
        quitGame();
    }

}

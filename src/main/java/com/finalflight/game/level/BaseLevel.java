package com.finalflight.game.level;

import com.finalflight.game.audio.MusicPlayer;
import com.finalflight.game.gameobjects.DestructibleGameObject;
import com.finalflight.game.gameobjects.FighterPlane;
import com.finalflight.game.gameobjects.UserPlane;
import com.finalflight.game.ui.BaseLevelView;
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

    protected abstract void initializeFriendlyUnits();

    protected abstract void checkIfGameOver();

    protected abstract void spawnEnemyUnits();

    protected abstract BaseLevelView instantiateLevelView();

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

    public void startGame() {
        background.requestFocus();
        timeline.play();
    }

    public void goToNextLevel(String levelName) {
        timeline.stop();
        setChanged();
        UserPlane.resetHealth(user.getHealth());
        notifyObservers(levelName);
    }

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

    protected void updateHUD() {
        levelView.updateKillCounter(user.getNumberOfKills(), killsToAdvance);
        levelView.updateBoostBar(user.getBoostEnergy() / UserPlane.getMaxBoostEnergy());
        levelView.updateFiringMode(user.getFiringMode().toString());
    }

    protected void updateKillCount() {
        int killsThisFrame = currentNumberOfEnemies - enemyUnits.size();
        for (int i = 0; i < killsThisFrame; i++) {
            user.incrementKillCount();
        }
    }

    private void initializeTimeline() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame gameLoop = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> updateScene());
        timeline.getKeyFrames().add(gameLoop);
    }

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


    // Method to toggle pause and resume the game
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

    // Method to pause the game
    private void pauseGame() {
        isPaused = true;
        timeline.pause();  // Stop the game loop

        // Fade out and pause music
        musicPlayer.fadeOutMusic(1.0, 0.05, null);
    }

    // Method to resume the game
    private void resumeGame() {
        isPaused = false;
        timeline.play();  // Restart the game loop

        // Fade music in
        musicPlayer.fadeInMusic(1.0, 0.2);
        background.requestFocus();

        // Re-enable ESC key functionality
        enableEscapeKey();
    }

    private void fireProjectile() {
        List<DestructibleGameObject> projectiles = user.fire();
        for (DestructibleGameObject projectile : projectiles) {
            root.getChildren().add(projectile);
            userProjectiles.add(projectile);
        }
    }

    private void generateEnemyFire() {
        enemyUnits.forEach(enemy -> spawnEnemyProjectile(((FighterPlane) enemy).fireProjectile()));
    }

    private void spawnEnemyProjectile(DestructibleGameObject projectile) {
        if (projectile != null) {
            root.getChildren().add(projectile);
            enemyProjectiles.add(projectile);
        }
    }

    private void updateActors() {
        friendlyUnits.forEach(DestructibleGameObject::updateActor);
        enemyUnits.forEach(DestructibleGameObject::updateActor);
        userProjectiles.forEach(DestructibleGameObject::updateActor);
        enemyProjectiles.forEach(DestructibleGameObject::updateActor);
    }

    private void removeAllDestroyedActors() {
        removeDestroyedActors(friendlyUnits);
        removeDestroyedActors(enemyUnits);
        removeDestroyedActors(userProjectiles);
        removeDestroyedActors(enemyProjectiles);
    }

    private void removeDestroyedActors(List<DestructibleGameObject> actors) {
        List<DestructibleGameObject> destroyedActors = actors.stream().filter(DestructibleGameObject::isDestroyed)
                .toList();
        root.getChildren().removeAll(destroyedActors);
        actors.removeAll(destroyedActors);
    }

    private void handlePlaneCollisions() {
        handleCollisions(friendlyUnits, enemyUnits);
    }

    private void handleUserProjectileCollisions() {
        handleCollisions(userProjectiles, enemyUnits);
    }

    private void handleEnemyProjectileCollisions() {
        handleCollisions(enemyProjectiles, friendlyUnits);
    }

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

    private void handleEnemyPenetration() {
        for (DestructibleGameObject enemy : enemyUnits) {
            if (enemyHasPenetratedDefenses(enemy)) {
                user.takeDamage();
                enemy.destroy();
            }
        }
    }

    private void updateLevelView() {
        levelView.removeHearts(user.getHealth());
    }

    private boolean enemyHasPenetratedDefenses(DestructibleGameObject enemy) {
        return Math.abs(enemy.getTranslateX()) > screenWidth;
    }

    protected void winGame() {
        timeline.stop();
        levelView.clearActorsAfterWin(enemyUnits, enemyProjectiles, userProjectiles);
        levelView.displayWinText();
        switchMusic(WON_GAME_MUSIC, false);

        // Disable ESC key functionality
        disableEscapeKey();
    }

    protected void loseGame() {
        timeline.stop();
        levelView.displayLoseText();
        switchMusic(LOST_GAME_MUSIC, false);

        // Disable ESC key functionality
        disableEscapeKey();
    }

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

    private void quitGame() {
        System.exit(0);
    }

    private void disableEscapeKey() {
        background.removeEventHandler(KeyEvent.KEY_PRESSED, escapeKeyHandler);
    }

    private void enableEscapeKey() {
        background.addEventHandler(KeyEvent.KEY_PRESSED, escapeKeyHandler);
    }

    protected void switchMusic(String newMusicFile, boolean shouldLoop) {
        if (musicPlayer != null) {
            musicPlayer.stopMusic(); // Stop the current music
        }
        musicPlayer = MusicPlayer.getInstance(newMusicFile);
        musicPlayer.playMusic(shouldLoop); // Play the new music
    }

    protected UserPlane getUser() {
        return user;
    }

    protected Group getRoot() {
        return root;
    }

    protected int getCurrentNumberOfEnemies() {
        return enemyUnits.size();
    }

    protected void addEnemyUnit(DestructibleGameObject enemy) {
        enemyUnits.add(enemy);
        root.getChildren().add(enemy);
    }

    protected double getEnemyMaximumYPosition() {
        return enemyMaximumYPosition;
    }

    public double getScreenWidth() {
        return screenWidth;
    }

    public double getScreenHeight() {
        return screenHeight;
    }

    protected Scene getScene() {
        return scene;
    }

    protected int getKillsToAdvance() {
        return killsToAdvance;
    }

    protected ImageView getBackground() {
        return background;
    }

    protected boolean userIsDestroyed() {
        return user.isDestroyed();
    }

    private void updateNumberOfEnemies() {
        currentNumberOfEnemies = enemyUnits.size();
    }

    public void publicTogglePause() {
        togglePause();
    }

    public void publicRestartGame() {
        restartGame();
    }

    public void publicQuitGame() {
        quitGame();
    }

}

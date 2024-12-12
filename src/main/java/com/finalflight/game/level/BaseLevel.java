package com.finalflight.game.level;

import com.finalflight.game.audio.MusicPlayer;
import com.finalflight.game.gameobjects.DestructibleGameObject;
import com.finalflight.game.gameobjects.FighterPlane;
import com.finalflight.game.gameobjects.UserPlane;
import com.finalflight.game.ui.BaseLevelView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseLevel extends Observable {

    private static final String LEVEL_MUSIC = "/com/finalflight/game/audio/levelmusic.mp3";
    private static final String WON_GAME_MUSIC = "/com/finalflight/game/audio/wongame.mp3";
    private static final String LOST_GAME_MUSIC = "/com/finalflight/game/audio/lostgame.mp3";
    private static final double SCREEN_HEIGHT_ADJUSTMENT = 100;
    private static final int MILLISECOND_DELAY = 50;
    private static final double BOOST_BAR_WIDTH = 200;
    private static final double BOOST_BAR_HEIGHT = 20;
    private final double screenHeight;
    private final double screenWidth;
    private final double enemyMaximumYPosition;
    private final int killsToAdvance;
    private final GaussianBlur blurEffect = new GaussianBlur(10);

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
    private VBox pauseMenu;
    private Button pauseButton;
    private Text killCountText;
    private Rectangle pauseOverlay;
    private EventHandler<KeyEvent> escapeKeyHandler;
    private Rectangle boostBar;
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
        initializeHUD();
        initializePauseMenu();
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
        updateHUD();
        updateLevelView();
        checkIfGameOver();
        updateBoostBar();
    }

    private void initializeTimeline() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame gameLoop = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> updateScene());
        timeline.getKeyFrames().add(gameLoop);
    }

    private void initializeBackground() {
        background.setFocusTraversable(true);
        background.setFitHeight(screenHeight);
        background.setFitWidth(screenWidth);

        // ESC handler for pause
        escapeKeyHandler = e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                togglePause(pauseButton);
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

    private void initializePauseMenu() {
        double buttonWidth = 300;

        // Create a semi-transparent black overlay
        pauseOverlay = new Rectangle(screenWidth, screenHeight, Color.BLACK);
        pauseOverlay.setOpacity(0.5); // Set transparency
        pauseOverlay.setVisible(false); // Initially hidden
        root.getChildren().add(pauseOverlay);

        // Initialize pause menu VBox
        pauseMenu = new VBox(30); // Create VBox with spacing between items
        pauseMenu.setAlignment(Pos.CENTER);

        // Initialize pause text
        Text pausedText = new Text("PAUSED");
        pausedText.setFont(Font.font("Trebuchet MS", 120)); // Set font and size
        pausedText.setFill(Color.WHITE); // Text color
        pausedText.setStroke(Color.BLACK); // Add a stroke for visibility
        pausedText.setStrokeWidth(2);

        // Initialize resume button
        Button resumeButton = new Button("Resume");
        resumeButton.getStyleClass().add("button");
        resumeButton.setStyle("-fx-font-size: 30px;");
        resumeButton.setPrefWidth(buttonWidth);
        resumeButton.setOnAction(e -> togglePause(pauseButton));

        // Initialize restart game button
        Button restartButton = new Button("Restart Game");
        restartButton.getStyleClass().add("button");
        restartButton.setStyle("-fx-font-size: 30px;");
        restartButton.setPrefWidth(buttonWidth);
        restartButton.setOnAction(e -> restartGame());

        // Initialize quit game button
        Button quitButton = new Button("Quit Game");
        quitButton.getStyleClass().add("button");
        quitButton.setStyle("-fx-font-size: 30px;");
        quitButton.setPrefWidth(buttonWidth);
        quitButton.setOnAction(e -> quitGame());

        // Initialize pause button in the top right
        pauseButton = new Button("Pause");
        pauseButton.getStyleClass().add("button");
        pauseButton.setStyle("-fx-font-size: 20px;");
        pauseButton.setFocusTraversable(false);
        pauseButton.setOnAction(e -> togglePause(pauseButton));
        StackPane pauseButtonContainer = new StackPane();
        pauseButtonContainer.getChildren().add(pauseButton);
        pauseButtonContainer.setAlignment(Pos.TOP_RIGHT); // Align the button to the top-right corner
        pauseButtonContainer.setLayoutX(screenWidth - 100); // Adjust X position
        pauseButtonContainer.setLayoutY(20);    // Adjust Y position
        root.getChildren().add(pauseButtonContainer);

        pauseMenu.getChildren().addAll(pausedText, resumeButton, restartButton, quitButton);
        pauseMenu.setVisible(false);
        pauseMenu.setLayoutX(screenWidth / 2 - 210);
        pauseMenu.setLayoutY(screenHeight / 2 - 250);
        root.getChildren().add(pauseMenu);
    }

    protected void initializeHUD() {
        initializeKillCounter();
        initializeFiringModeText();
        initializeBoostBar();
    }

    protected void initializeKillCounter() {
        killCountText = new Text("Kills: 0 / " + killsToAdvance);
        killCountText.setFont(Font.font("Trebuchet MS", 30));
        killCountText.setFill(Color.WHITE);
        killCountText.setX(8);
        killCountText.setY(80);
        root.getChildren().add(killCountText);
    }

    protected void initializeFiringModeText() {
        firingModeText = new Text("Mode: SINGLE");
        firingModeText.setFont(Font.font("Trebuchet MS", 30));
        firingModeText.setFill(Color.WHITE);
        firingModeText.setX(8);
        firingModeText.setY(getFiringModeTextYPosition());
        root.getChildren().add(firingModeText);
    }

    protected double getFiringModeTextYPosition() {
        return 120; // Default Y position of firing mode text
    }

    protected void initializeBoostBar() {
        boostBar = new Rectangle(BOOST_BAR_WIDTH, BOOST_BAR_HEIGHT, Color.LAWNGREEN);
        boostBar.setTranslateX(8);
        boostBar.setTranslateY(getBoostBarYPosition());

        // Set rounded corners
        boostBar.setArcWidth(15);
        boostBar.setArcHeight(15);

        // Set outline
        boostBar.setStroke(Color.BLACK);
        boostBar.setStrokeWidth(1);

        root.getChildren().add(boostBar);
    }

    protected double getBoostBarYPosition() {
        return 140; // Default Y position of boost bar
    }

    private void updateBoostBar() {
        double energyPercentage = user.getBoostEnergy() / UserPlane.getMaxBoostEnergy();
        boostBar.setWidth(BOOST_BAR_WIDTH * energyPercentage);

        // Set colour based on percentage
        if (energyPercentage > 0.5) {
            boostBar.setFill(Color.LAWNGREEN);
        } else if (energyPercentage > 0.2) {
            boostBar.setFill(Color.ORANGE);
        } else {
            boostBar.setFill(Color.RED);
        }
    }

    // Method to toggle pause and resume the game
    private void togglePause(Button pauseButton) {
        if (isPaused) {
            resumeGame();  // Resume the game if it's currently paused
            pauseButton.setVisible(true);
            pauseMenu.setVisible(false);
            pauseOverlay.setVisible(false); // Hide overlay
            root.getChildren().forEach(node -> node.setEffect(null)); // Remove blur effect from all elements
        } else {
            pauseGame();   // Pause the game if it's running
            pauseButton.setVisible(false); // Update button text to "Unpause"
            pauseOverlay.setVisible(true); // Show overlay
            pauseMenu.setVisible(true);

            // Apply blur effect to all non-pause elements
            root.getChildren().forEach(node -> {
                if (node != pauseOverlay && node != pauseMenu) {
                    node.setEffect(blurEffect);
                }
            });

            // Bring pause elements to the front
            pauseOverlay.toFront();
            pauseMenu.toFront();
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

    protected void updateHUD() {
        if (killCountText != null) {
            for (int i = 0; i < currentNumberOfEnemies - enemyUnits.size(); i++) {
                user.incrementKillCount();
            }
            killCountText.setText("Kills: " + user.getNumberOfKills() + " / " + killsToAdvance);
        }
    }


    private boolean enemyHasPenetratedDefenses(DestructibleGameObject enemy) {
        return Math.abs(enemy.getTranslateX()) > screenWidth;
    }

    protected void winGame() {
        timeline.stop();
        clearActorsAfterWin();
        Text youWinText = new Text("YOU WIN!");
        youWinText.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 200)); // Set font and size
        youWinText.setFill(Color.ORANGE);
        youWinText.setStroke(Color.BLACK); // Add outline for visibility
        youWinText.setStrokeWidth(2);
        youWinText.setLayoutX(screenWidth / 2 - 450);
        youWinText.setLayoutY(screenHeight / 2 + 50);
        root.getChildren().add(youWinText);
        switchMusic(WON_GAME_MUSIC, false);

        // Remove the pause button from view
        if (pauseButton != null) {
            root.getChildren().remove(pauseButton.getParent());
        }

        // Disable ESC key functionality
        disableEscapeKey();
    }

    private void clearActorsAfterWin() {
        if (enemyUnits != null) root.getChildren().removeAll(enemyUnits);
        if (enemyProjectiles != null) root.getChildren().removeAll(enemyProjectiles);
        if (userProjectiles != null) root.getChildren().removeAll(userProjectiles);
    }

    protected void loseGame() {
        double buttonWidth = 300;
        timeline.stop();
        switchMusic(LOST_GAME_MUSIC, false);

        // Remove the pause button from view
        if (pauseButton != null) {
            root.getChildren().remove(pauseButton.getParent());
        }

        // Disable ESC key functionality
        disableEscapeKey();

        pauseOverlay.setVisible(true);

        // Create a VBox container for the game-over screen
        VBox gameOverContainer = new VBox();
        gameOverContainer.setSpacing(30); // Set spacing between elements
        gameOverContainer.setAlignment(Pos.CENTER); // Center align the contents
        gameOverContainer.setLayoutX(screenWidth / 2 - 425);
        gameOverContainer.setLayoutY(screenHeight / 2 - 200);

        // Create the game-over text
        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 150)); // Set font and size
        gameOverText.setFill(Color.RED);
        gameOverText.setStroke(Color.BLACK); // Add outline for visibility
        gameOverText.setStrokeWidth(2);

        // Create a restart button
        Button restartButton = new Button("Restart");
        restartButton.getStyleClass().add("button");
        restartButton.setStyle("-fx-font-size: 30px;");
        restartButton.setPrefWidth(buttonWidth);
        restartButton.setOnAction(e -> restartGame());

        Button quitButton = new Button("Quit Game");
        quitButton.getStyleClass().add("button");
        quitButton.setStyle("-fx-font-size: 30px;");
        quitButton.setPrefWidth(buttonWidth);
        quitButton.setOnAction(e -> quitGame());

        // Add the game-over text and restart button to the VBox
        gameOverContainer.getChildren().addAll(gameOverText, restartButton, quitButton);

        // Add game over VBox to the root
        root.getChildren().add(gameOverContainer);

        root.getChildren().forEach(node -> {
            if (node != gameOverContainer) {
                node.setEffect(blurEffect);
            }
        });
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

    protected double getScreenWidth() {
        return screenWidth;
    }

    protected boolean userIsDestroyed() {
        return user.isDestroyed();
    }

    private void updateNumberOfEnemies() {
        currentNumberOfEnemies = enemyUnits.size();
    }

}

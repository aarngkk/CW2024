package com.example.demo;

import java.util.*;
import java.util.stream.Collectors;

import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class LevelParent extends Observable {

	private static final String LEVEL_MUSIC = "/com/example/demo/audio/levelmusic.mp3";
	private static final String WON_GAME_MUSIC = "/com/example/demo/audio/wongame.mp3";
	private static final String LOST_GAME_MUSIC = "/com/example/demo/audio/lostgame.mp3";
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

	private final List<ActiveActorDestructible> friendlyUnits;
	private final List<ActiveActorDestructible> enemyUnits;
	private final List<ActiveActorDestructible> userProjectiles;
	private final List<ActiveActorDestructible> enemyProjectiles;
	
	private int currentNumberOfEnemies;
	private LevelView levelView;
	private boolean isPaused = false;
	private Button pauseButton;
	private Text pausedText;
	private Text killCountText;
	private Rectangle pauseOverlay;
	private EventHandler<KeyEvent> escapeKeyHandler;
	private Rectangle boostBar;
	protected Text firingModeText;
	private MusicPlayer musicPlayer;

	public LevelParent(String backgroundImageName, double screenHeight, double screenWidth, int playerInitialHealth, int killsToAdvance) {
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

	protected abstract LevelView instantiateLevelView();

	public Scene initializeScene() {
		scene.getStylesheets().add(getClass().getResource("/com/example/demo/css/styles.css").toExternalForm());
		initializeBackground();
		initializeFriendlyUnits();
		levelView.showHeartDisplay(user.getHealth());
		initializeHUD();
		initializePauseControls();
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

	private void updateScene() {
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

	private void initializePauseControls() {
		// Create a semi-transparent black overlay
		pauseOverlay = new Rectangle(screenWidth, screenHeight, Color.BLACK);
		pauseOverlay.setOpacity(0.5); // Set transparency
		pauseOverlay.setVisible(false); // Initially hidden
		root.getChildren().add(pauseOverlay);

		// Initialize pause text
		pausedText = new Text("PAUSED");
		pausedText.setFont(Font.font("Trebuchet MS", 120)); // Set font and size
		pausedText.setFill(Color.WHITE); // Text color
		pausedText.setStroke(Color.BLACK); // Add a stroke for visibility
		pausedText.setStrokeWidth(2);
		pausedText.setVisible(false); // Make text initially hidden
		pausedText.setX(screenWidth / 2 - 210); // Center horizontally
		pausedText.setY(screenHeight / 2); // Center vertically
		root.getChildren().add(pausedText); // Add to the root group

		// Initialize pause button
		pauseButton = new Button("Pause");
		pauseButton.getStyleClass().add("button");
		pauseButton.setStyle("-fx-font-size: 20px;");
		pauseButton.setFocusTraversable(false);
		pauseButton.setOnAction(e -> togglePause(pauseButton));

		StackPane pauseButtonContainer = new StackPane();
		pauseButtonContainer.getChildren().add(pauseButton);
		pauseButtonContainer.setAlignment(Pos.TOP_RIGHT); // Align the button to the top-right corner
		pauseButtonContainer.setLayoutX(screenWidth - 120); // Adjust X position
		pauseButtonContainer.setLayoutY(15);    // Adjust Y position
		root.getChildren().add(pauseButtonContainer);
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
		double energyPercentage = (double) user.getBoostEnergy() / UserPlane.getMaxBoostEnergy();
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
			pauseButton.setText("Pause"); // Update button text to "Pause"
			pausedText.setVisible(false); // Hide "PAUSED" text
			pauseOverlay.setVisible(false); // Hide overlay
			root.getChildren().forEach(node -> node.setEffect(null)); // Remove blur effect from all elements
		} else {
			pauseGame();   // Pause the game if it's running
			pauseButton.setText("Unpause"); // Update button text to "Unpause"
			pausedText.setVisible(true); // Show "PAUSED" text
			pauseOverlay.setVisible(true); // Show overlay

			// Apply blur effect to all non-pause elements
			root.getChildren().forEach(node -> {
				if (node != pauseOverlay && node != pausedText && node != pauseButton.getParent()) {
					node.setEffect(blurEffect);
				}
			});

			// Bring pause elements to the front
			pauseOverlay.toFront();
			pausedText.toFront();
			pauseButton.getParent().toFront();
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
		List<ActiveActorDestructible> projectiles = user.fire();
		for (ActiveActorDestructible projectile : projectiles) {
			root.getChildren().add(projectile);
			userProjectiles.add(projectile);
		}
	}

	private void generateEnemyFire() {
		enemyUnits.forEach(enemy -> spawnEnemyProjectile(((FighterPlane) enemy).fireProjectile()));
	}

	private void spawnEnemyProjectile(ActiveActorDestructible projectile) {
		if (projectile != null) {
			root.getChildren().add(projectile);
			enemyProjectiles.add(projectile);
		}
	}

	private void updateActors() {
		friendlyUnits.forEach(plane -> plane.updateActor());
		enemyUnits.forEach(enemy -> enemy.updateActor());
		userProjectiles.forEach(projectile -> projectile.updateActor());
		enemyProjectiles.forEach(projectile -> projectile.updateActor());
	}

	private void removeAllDestroyedActors() {
		removeDestroyedActors(friendlyUnits);
		removeDestroyedActors(enemyUnits);
		removeDestroyedActors(userProjectiles);
		removeDestroyedActors(enemyProjectiles);
	}

	private void removeDestroyedActors(List<ActiveActorDestructible> actors) {
		List<ActiveActorDestructible> destroyedActors = actors.stream().filter(actor -> actor.isDestroyed())
				.collect(Collectors.toList());
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

	private void handleCollisions(List<ActiveActorDestructible> actors1,
			List<ActiveActorDestructible> actors2) {
		for (ActiveActorDestructible actor : actors2) {
			for (ActiveActorDestructible otherActor : actors1) {
				if (actor.getBoundsInParent().intersects(otherActor.getBoundsInParent())) {
					actor.takeDamage();
					otherActor.takeDamage();
				}
			}
		}
	}

	private void handleEnemyPenetration() {
		for (ActiveActorDestructible enemy : enemyUnits) {
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


	private boolean enemyHasPenetratedDefenses(ActiveActorDestructible enemy) {
		return Math.abs(enemy.getTranslateX()) > screenWidth;
	}

	protected void winGame() {
		timeline.stop();
		levelView.showWinImage();
		switchMusic(WON_GAME_MUSIC, false);

		// Remove the pause button from view
		if (pauseButton != null && root.getChildren().contains(pauseButton.getParent())) {
			root.getChildren().remove(pauseButton.getParent());
		}

		// Disable ESC key functionality
		disableEscapeKey();
	}

	protected void loseGame() {
		timeline.stop();
		levelView.showGameOverImage();
		switchMusic(LOST_GAME_MUSIC, false);

		// Remove the pause button from view
		if (pauseButton != null && root.getChildren().contains(pauseButton.getParent())) {
			root.getChildren().remove(pauseButton.getParent());
		}

		// Disable ESC key functionality
		disableEscapeKey();
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

	protected void addEnemyUnit(ActiveActorDestructible enemy) {
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

package com.example.demo;

import java.util.*;
import java.util.stream.Collectors;

import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class LevelParent extends Observable {

	private static final double SCREEN_HEIGHT_ADJUSTMENT = 100;
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
	private EventHandler<KeyEvent> escapeKeyHandler;
	protected Text firingModeText;

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
		initializePauseControls();
		initializeFriendlyUnits();
		levelView.showHeartDisplay(user.getHealth());
		initializeHUD();
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
			processKeyPress();
		});

		background.setOnKeyReleased(e -> {
			activeKeys.remove(e.getCode());
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
		// Kill count text
		killCountText = new Text("Kills: 0 / " + killsToAdvance);
		killCountText.setFont(Font.font("Trebuchet MS", 30));
		killCountText.setFill(Color.WHITE);
		killCountText.setX(8);
		killCountText.setY(80);
		root.getChildren().add(killCountText);

		// Firing mode text
		firingModeText = new Text("Mode: SINGLE");
		firingModeText.setFont(Font.font("Trebuchet MS", 30));
		firingModeText.setFill(Color.WHITE);
		firingModeText.setX(8);
		firingModeText.setY(120);
		root.getChildren().add(firingModeText);
	}

	// Method to toggle pause and resume the game
	private void togglePause(Button pauseButton) {
		if (isPaused) {
			resumeGame();  // Resume the game if it's currently paused
			pauseButton.setText("Pause"); // Update button text to "Pause"
			pausedText.setVisible(false); // Hide "PAUSED" text
		} else {
			pauseGame();   // Pause the game if it's running
			pauseButton.setText("Unpause"); // Update button text to "Unpause"
			pausedText.setVisible(true); // Show "PAUSED" text
		}
	}

	// Method to pause the game
	private void pauseGame() {
		isPaused = true;
		timeline.pause();  // Stop the game loop
	}

	// Method to resume the game
	private void resumeGame() {
		isPaused = false;
		timeline.play();  // Restart the game loop
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

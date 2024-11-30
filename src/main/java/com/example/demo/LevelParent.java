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
		initializePauseText();
		initializeFriendlyUnits();
		levelView.showHeartDisplay();
		addPauseButton();
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

		// Store the ESC key handler in a variable
		escapeKeyHandler = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				if (e.getCode() == KeyCode.ESCAPE) {
					togglePause(pauseButton);
				}
			}
		};

		background.addEventHandler(KeyEvent.KEY_PRESSED, escapeKeyHandler);

		background.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				KeyCode kc = e.getCode();
				if (kc == KeyCode.UP) user.moveUp();
				if (kc == KeyCode.DOWN) user.moveDown();
				if (kc == KeyCode.SPACE) fireProjectile();
			}
		});
		background.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				KeyCode kc = e.getCode();
				if (kc == KeyCode.UP || kc == KeyCode.DOWN) user.stop();
			}
		});
		root.getChildren().add(background);
	}

	private void initializePauseText() {
		pausedText = new Text("PAUSED");
		pausedText.setFont(Font.font("Trebuchet MS", 120)); // Set font and size
		pausedText.setFill(Color.WHITE); // Text color
		pausedText.setStroke(Color.BLACK); // Add a stroke for visibility
		pausedText.setStrokeWidth(2);
		pausedText.setVisible(false); // Make text initially hidden
		pausedText.setX(screenWidth / 2 - 210); // Center horizontally
		pausedText.setY(screenHeight / 2); // Center vertically

		root.getChildren().add(pausedText); // Add to the root group
	}

	private void initializeHUD() {
		killCountText = new Text("Kills: 0 / " + killsToAdvance);
		killCountText.setFont(Font.font("Trebuchet MS", 30));
		killCountText.setFill(Color.WHITE);
		killCountText.setX(8);
		killCountText.setY(80);
		root.getChildren().add(killCountText);
	}

	public void addPauseButton() {
		// Create a button for pausing the game
		pauseButton = new Button("Pause");

		// Set the button's visual appearance
		pauseButton.getStyleClass().add("button");
		pauseButton.setStyle("-fx-font-size: 20px;");

		// Prevent the button from gaining focus
		pauseButton.setFocusTraversable(false);

		// Set the button's action
		pauseButton.setOnAction(e -> togglePause(pauseButton));

		// Create a StackPane to position the button
		StackPane pauseButtonContainer = new StackPane();
		pauseButtonContainer.getChildren().add(pauseButton);
		pauseButtonContainer.setAlignment(Pos.TOP_RIGHT); // Align the button to the top-right corner
		pauseButtonContainer.setLayoutX(screenWidth - 120); // Adjust X position
		pauseButtonContainer.setLayoutY(15);    // Adjust Y position

		// Add the pause button to the root container
		root.getChildren().add(pauseButtonContainer);
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
		ActiveActorDestructible projectile = user.fireProjectile();
		root.getChildren().add(projectile);
		userProjectiles.add(projectile);
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
		for (int i = 0; i < currentNumberOfEnemies - enemyUnits.size(); i++) {
			user.incrementKillCount();
		}
		killCountText.setText("Kills: " + user.getNumberOfKills() + " / " + killsToAdvance);
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

	protected Text getKillCountText() {
		return killCountText;
	}

	private void updateNumberOfEnemies() {
		currentNumberOfEnemies = enemyUnits.size();
	}

}

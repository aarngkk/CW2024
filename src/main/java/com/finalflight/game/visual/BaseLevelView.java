package com.finalflight.game.visual;

import com.finalflight.game.gameobjects.DestructibleGameObject;
import com.finalflight.game.level.BaseLevel;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Handles the UI elements and interactions for a base game level, including HUD elements
 * such as health, boost energy, and firing mode, as well as pause and game-over menus.
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/visual/BaseLevelView.java</p>
 */
public class BaseLevelView {

    private static final double HEART_DISPLAY_X_POSITION = 5;
    private static final double HEART_DISPLAY_Y_POSITION = 5;
    private static final double BOOST_BAR_WIDTH = 200;
    private static final double BOOST_BAR_HEIGHT = 20;
    private final double screenWidth;
    private final double screenHeight;
    private final GaussianBlur blurEffect = new GaussianBlur(10);
    private final BaseLevel baseLevel;
    private final Group root;
    private final HeartDisplay heartDisplay;
    private VBox pauseMenu;
    private Button pauseButton;
    private Rectangle pauseOverlay;
    private Text killCountText;
    private Text firingModeText;
    private Rectangle boostBar;

    /**
     * Constructs a BaseLevelView instance.
     *
     * @param root            The root JavaFX Group where UI elements are added.
     * @param screenWidth     The width of the screen in pixels.
     * @param screenHeight    The height of the screen in pixels.
     * @param baseLevel       The BaseLevel instance to interact with game logic.
     * @param heartsToDisplay The initial number of hearts to display on the Player HUD.
     */
    public BaseLevelView(Group root, int heartsToDisplay, double screenWidth, double screenHeight, BaseLevel baseLevel) {
        this.root = root;
        this.heartDisplay = new HeartDisplay(HEART_DISPLAY_X_POSITION, HEART_DISPLAY_Y_POSITION, heartsToDisplay);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.baseLevel = baseLevel;
    }

    /**
     * Dynamically displays hearts based on the current health value.
     *
     * @param currentHealth The player's current health.
     */
    public void showHeartDisplay(int currentHealth) {
        // Clear any existing hearts
        heartDisplay.clearHearts();

        // Add hearts based on the current health
        for (int i = 0; i < currentHealth; i++) {
            heartDisplay.addHeart();
        }

        // Add the heart display to the root if not already added
        if (!root.getChildren().contains(heartDisplay.getContainer())) {
            root.getChildren().add(heartDisplay.getContainer());
        }
    }

    /**
     * Removes hearts from the display based on the remaining health.
     *
     * @param heartsRemaining The number of hearts to remain displayed.
     */
    public void removeHearts(int heartsRemaining) {
        int currentNumberOfHearts = heartDisplay.getContainer().getChildren().size();
        for (int i = 0; i < currentNumberOfHearts - heartsRemaining; i++) {
            heartDisplay.removeHeart();
        }
    }

    /**
     * Initializes the pause menu and its elements, including buttons for resuming, restarting, and quitting the game.
     */
    public void initializePauseMenu() {
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
        resumeButton.setOnAction(e -> baseLevel.publicTogglePause());

        // Initialize restart game button
        Button restartButton = new Button("Restart Game");
        restartButton.getStyleClass().add("button");
        restartButton.setStyle("-fx-font-size: 30px;");
        restartButton.setPrefWidth(buttonWidth);
        restartButton.setOnAction(e -> baseLevel.publicRestartGame());

        // Initialize quit game button
        Button quitButton = new Button("Quit Game");
        quitButton.getStyleClass().add("button");
        quitButton.setStyle("-fx-font-size: 30px;");
        quitButton.setPrefWidth(buttonWidth);
        quitButton.setOnAction(e -> baseLevel.publicQuitGame());

        // Initialize pause button in the top right
        pauseButton = new Button("Pause");
        pauseButton.getStyleClass().add("button");
        pauseButton.setStyle("-fx-font-size: 20px;");
        pauseButton.setFocusTraversable(false);
        pauseButton.setOnAction(e -> baseLevel.publicTogglePause());
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

    /**
     * Displays the pause menu on the screen.
     */
    public void showPauseMenu() {
        pauseButton.setVisible(false);
        pauseOverlay.setVisible(true);
        pauseMenu.setVisible(true);
    }

    /**
     * Hides the pause menu from the screen.
     */
    public void hidePauseMenu() {
        pauseButton.setVisible(true);
        pauseMenu.setVisible(false);
        pauseOverlay.setVisible(false);
    }

    /**
     * Removes the pause button from the screen.
     */
    public void removePauseButton() {
        if (pauseButton != null) {
            root.getChildren().remove(pauseButton.getParent());
        }
    }

    /**
     * Brings pause-related UI elements to the front of the screen.
     */
    public void bringPauseElementsToFront() {
        if (pauseOverlay != null) {
            pauseOverlay.toFront();
        }
        if (pauseMenu != null) {
            pauseMenu.toFront();
        }
    }

    /**
     * Displays the pause overlay without showing the pause menu.
     */
    public void showPauseOverlay() {
        if (pauseOverlay != null) {
            pauseOverlay.setVisible(true);
        }
    }

    /**
     * Applies or removes a blur effect to non-pause elements of the game.
     *
     * @param apply True to apply the blur effect, false to remove it.
     */
    public void applyPauseBlurEffect(boolean apply) {
        root.getChildren().forEach(node -> {
            if (node != pauseMenu && node != pauseOverlay) {
                node.setEffect(apply ? blurEffect : null);
            }
        });
    }

    /**
     * Initializes HUD elements such as the kill counter, boost bar, and firing mode display.
     *
     * @param killsToAdvance The total number of kills required to advance to the next level.
     * @param boostbarY      The Y-coordinate for placing the boost bar.
     * @param firingModeY    The Y-coordinate for placing the firing mode display.
     */
    public void initializeHUD(int killsToAdvance, double boostbarY, double firingModeY) {
        initializeKillCounter(killsToAdvance);
        initializeFiringModeText(firingModeY);
        initializeBoostBar(boostbarY);
    }

    /**
     * Initializes the kill counter display on the HUD.
     *
     * @param killsToAdvance The total number of kills required to advance to the next level.
     */
    private void initializeKillCounter(int killsToAdvance) {
        killCountText = new Text("Kills: 0 / " + killsToAdvance);
        killCountText.setFont(Font.font("Trebuchet MS", 30));
        killCountText.setFill(Color.WHITE);
        killCountText.setX(8);
        killCountText.setY(80);
        root.getChildren().add(killCountText);
    }

    /**
     * Initializes the firing mode text display on the HUD.
     *
     * @param yPosition The Y-coordinate position where the firing mode text is placed.
     */
    protected void initializeFiringModeText(double yPosition) {
        firingModeText = new Text("Mode: SINGLE");
        firingModeText.setFont(Font.font("Trebuchet MS", 30));
        firingModeText.setFill(Color.WHITE);
        firingModeText.setX(8);
        firingModeText.setY(yPosition);
        root.getChildren().add(firingModeText);
    }

    /**
     * Initializes the boost bar on the HUD.
     *
     * @param yPosition The Y-coordinate position where the boost bar is placed.
     */
    protected void initializeBoostBar(double yPosition) {
        boostBar = new Rectangle(BOOST_BAR_WIDTH, BOOST_BAR_HEIGHT, Color.LAWNGREEN);
        boostBar.setTranslateX(8);
        boostBar.setTranslateY(yPosition);

        // Set rounded corners
        boostBar.setArcWidth(15);
        boostBar.setArcHeight(15);

        // Set outline
        boostBar.setStroke(Color.BLACK);
        boostBar.setStrokeWidth(1);

        root.getChildren().add(boostBar);
    }

    /**
     * Updates the kill counter displayed on the HUD.
     *
     * @param kills          The current number of kills.
     * @param killsToAdvance The total number of kills required to advance.
     */
    public void updateKillCounter(int kills, int killsToAdvance) {
        if (killCountText != null) {
            killCountText.setText("Kills: " + kills + " / " + killsToAdvance);
        }
    }

    /**
     * Updates the firing mode text displayed on the HUD.
     *
     * @param mode The current firing mode of the player's weapon.
     */
    public void updateFiringMode(String mode) {
        if (firingModeText != null) {
            firingModeText.setText("Mode: " + mode);
        }
    }

    /**
     * Updates the boost bar's visual representation based on the player's boost energy.
     *
     * @param energyPercentage The percentage of boost energy remaining (0 to 1).
     */
    public void updateBoostBar(double energyPercentage) {
        if (boostBar != null) {
            boostBar.setWidth(BOOST_BAR_WIDTH * energyPercentage);
            boostBar.setFill(energyPercentage > 0.5 ? Color.LAWNGREEN : energyPercentage > 0.2 ? Color.ORANGE : Color.RED);
        }
    }

    /**
     * Displays a "Victory" screen with options to restart or quit the game.
     */
    public void displayGameWin() {
        double buttonWidth = 300;
        removePauseButton();

        // Create a VBox container for the game win screen
        VBox gameWinContainer = new VBox();
        gameWinContainer.setSpacing(30); // Set spacing between elements
        gameWinContainer.setAlignment(Pos.CENTER); // Center align the contents
        gameWinContainer.setLayoutX(screenWidth / 2 - 485);
        gameWinContainer.setLayoutY(screenHeight / 2 - 235);

        // Create the "VICTORY!" text
        Text victoryText = new Text("VICTORY!");
        victoryText.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 225)); // Set font and size
        victoryText.setFill(Color.ORANGE);
        victoryText.setStroke(Color.BLACK); // Add outline for visibility
        victoryText.setStrokeWidth(2);

        Button restartButton = new Button("Restart Game");
        restartButton.getStyleClass().add("button");
        restartButton.setStyle("-fx-font-size: 30px;");
        restartButton.setPrefWidth(buttonWidth);
        restartButton.setOnAction(e -> baseLevel.publicRestartGame());

        Button quitButton = new Button("Quit Game");
        quitButton.getStyleClass().add("button");
        quitButton.setStyle("-fx-font-size: 30px;");
        quitButton.setPrefWidth(buttonWidth);
        quitButton.setOnAction(e -> baseLevel.publicQuitGame());

        // Add the victory text, restart game button, and quit button to the VBox
        gameWinContainer.getChildren().addAll(victoryText, restartButton, quitButton);

        // Add game win VBox to the root
        root.getChildren().add(gameWinContainer);
    }

    /**
     * Clears all game actors such as enemies and projectiles after a win condition.
     *
     * @param enemyUnits        The list of enemy game objects to remove.
     * @param userProjectiles   The list of user projectiles to remove.
     * @param enemyProjectiles  The list of enemy projectiles to remove.
     */
    public void clearActorsAfterWin(List<DestructibleGameObject> enemyUnits, List<DestructibleGameObject> userProjectiles, List<DestructibleGameObject> enemyProjectiles) {
        if (enemyUnits != null) root.getChildren().removeAll(enemyUnits);
        if (enemyProjectiles != null) root.getChildren().removeAll(enemyProjectiles);
        if (userProjectiles != null) root.getChildren().removeAll(userProjectiles);
    }

    /**
     * Displays a "Game Over" screen with options to restart or quit the game.
     */
    public void displayGameOver() {
        double buttonWidth = 300;
        removePauseButton();
        showPauseOverlay();

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
        restartButton.setOnAction(e -> baseLevel.publicRestartGame());

        Button quitButton = new Button("Quit Game");
        quitButton.getStyleClass().add("button");
        quitButton.setStyle("-fx-font-size: 30px;");
        quitButton.setPrefWidth(buttonWidth);
        quitButton.setOnAction(e -> baseLevel.publicQuitGame());

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

    /**
     * Returns the root JavaFX Group containing all UI elements.
     *
     * @return The root JavaFX Group.
     */
    public Group getRoot() {
        return root;
    }

    /**
     * Returns the screen width in pixels.
     *
     * @return The screen width.
     */
    public double getScreenWidth() {
        return screenWidth;
    }

}

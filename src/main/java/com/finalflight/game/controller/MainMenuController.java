/**
 * The {@code MainMenuController} class is responsible for managing the main menu of the game.
 * It initializes the game controller, handles main menu actions such as starting the game,
 * and manages background music playback for the main menu.
 *
 * <p>This class is connected to the main menu FXML file and provides functionality for the
 * start and quit buttons.</p>
 *
 * <p>Original Source Code: <a href="com/finalflight/game/controller/MainMenuController.java">MainMenuController.java</a></p>
 */
package com.finalflight.game.controller;

import com.finalflight.game.audio.MusicPlayer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainMenuController {

    private GameController gameController;
    private static final String MAIN_MENU_MUSIC = "/com/finalflight/game/audio/mainmenu.mp3";

    /**
     * Sets the {@link Stage} for the game and initializes the {@link GameController}.
     * This method also starts playing the main menu background music in a loop.
     *
     * @param stage the primary stage of the application.
     */
    public void setStage(Stage stage) {
        this.gameController = new GameController(stage);

        MusicPlayer musicPlayer = MusicPlayer.getInstance(MAIN_MENU_MUSIC);
        musicPlayer.playMusic(true);
    }

    /**
     * Starts the game by invoking the {@link GameController#launchGame()} method.
     * If an error occurs while launching the game, an error alert is displayed.
     */
    @FXML
    public void startGame() {
        try {
            gameController.launchGame();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to start game.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Quits the game by terminating the application.
     * This method is invoked when the quit button in the main menu is clicked.
     */
    @FXML
    public void quitGame() {
        System.exit(0);
    }
}

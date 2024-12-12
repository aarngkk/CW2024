package com.finalflight.game.controller;

import com.finalflight.game.audio.MusicPlayer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainMenuController {

    private GameController gameController;
    private Stage stage;
    private MusicPlayer musicPlayer;
    private static final String MAIN_MENU_MUSIC= "/com/finalflight/game/audio/mainmenu.mp3";

    public void setStage(Stage stage) {
        this.stage = stage;
        this.gameController = new GameController(stage);

        musicPlayer = MusicPlayer.getInstance(MAIN_MENU_MUSIC);
        musicPlayer.playMusic(true);
    }

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

    @FXML
    public void quitGame() {
        System.exit(0);
    }
}

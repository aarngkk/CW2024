package com.example.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainMenuController {

    private Controller controller;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
        this.controller = new Controller(stage);
    }

    @FXML
    public void startGame() {
        try {
            controller.launchGame();
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

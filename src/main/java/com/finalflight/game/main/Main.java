/**
 * The {@code Main} class serves as the entry point for the Final Flight game application.
 * It initializes the primary JavaFX stage, sets up the main menu, and applies essential
 * configurations such as the application icon and window properties.
 *
 * <p>This class extends {@link Application} and overrides the {@link #start(Stage)} method
 * to load and display the main menu scene.</p>
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/main/Main.java">Main.java</a></p>
 */
package com.finalflight.game.main;

import com.finalflight.game.controller.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static final String ICON_IMAGE_NAME = "/com/finalflight/game/images/icon.png";
    private static final String FXML_FILE_NAME = "/com/finalflight/game/fxml/MainMenu.fxml";
    private static final String TITLE = "Final Flight";
    private static final double SCENE_WIDTH = 1540.0;
    private static final double SCENE_HEIGHT = 870.0;

    /**
     * The entry point for the JavaFX application. Initializes the primary stage with
     * the main menu scene and applies application-level configurations.
     *
     * @param stage the primary stage for this application.
     * @throws IOException if the FXML file cannot be loaded.
     * @throws SecurityException if there is a security issue accessing resources.
     * @throws IllegalArgumentException if the resources (icon or FXML) are invalid.
     */
    @Override
    public void start(Stage stage) throws IOException, SecurityException,
            IllegalArgumentException {
        Image icon = new Image(Main.class.getResource(ICON_IMAGE_NAME).toExternalForm());
        stage.getIcons().add(icon);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE_NAME));
        Scene scene = new Scene(loader.load(), SCENE_WIDTH, SCENE_HEIGHT);

        MainMenuController mainMenuController = loader.getController();
        mainMenuController.setStage(stage);

        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * The main method, which serves as the application launcher.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch();
    }
}
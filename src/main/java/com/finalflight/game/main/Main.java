package com.finalflight.game.main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.finalflight.game.controller.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	private static final String ICON_IMAGE_NAME = "/com/finalflight/game/images/icon.png";
	private static final String FXML_FILE_NAME = "/com/finalflight/game/fxml/MainMenu.fxml";
	private static final String TITLE = "Final Flight";

	@Override
	public void start(Stage stage) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		Image icon = new Image(Main.class.getResource(ICON_IMAGE_NAME).toExternalForm());
		stage.getIcons().add(icon);

		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE_NAME));
		Scene scene = new Scene(loader.load());

		MainMenuController mainMenuController = loader.getController();
		mainMenuController.setStage(stage);

		stage.setScene(scene);
		stage.setTitle(TITLE);
		stage.setResizable(false);
		stage.setMaximized(true);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
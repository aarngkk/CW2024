package com.example.demo.controller;

import java.lang.reflect.InvocationTargetException;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	private static final String ICON_IMAGE_NAME = "/com/example/demo/images/icon.png";
	private static final String TITLE = "Sky Battle";
	private Controller myController;

	@Override
	public void start(Stage stage) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Image icon = new Image(Main.class.getResource(ICON_IMAGE_NAME).toExternalForm());
		stage.getIcons().add(icon);

		stage.setTitle(TITLE);
		stage.setResizable(false);
		stage.setMaximized(true);

		myController = new Controller(stage);
		myController.launchGame();
	}

	public static void main(String[] args) {
		launch();
	}
}
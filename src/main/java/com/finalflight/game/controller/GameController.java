package com.finalflight.game.controller;

import com.finalflight.game.level.BaseLevel;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;

public class GameController implements Observer {

    private static final String LEVEL_ONE_CLASS_NAME = "com.finalflight.game.level.LevelOne";
    private final Stage stage;

    public GameController(Stage stage) {
        this.stage = stage;
    }

    public void launchGame() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        stage.show();
        goToLevel(LEVEL_ONE_CLASS_NAME);
    }

    private void goToLevel(String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // Get the dimensions of the screen
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        Class<?> myClass = Class.forName(className);
        Constructor<?> constructor = myClass.getConstructor(double.class, double.class);
        BaseLevel myLevel = (BaseLevel) constructor.newInstance(screenHeight, screenWidth);
        myLevel.addObserver(this);
        Scene scene = myLevel.initializeScene();
        stage.setScene(scene);
        myLevel.startGame();

    }

    @Override
    public void update(Observable arg0, Object arg1) {
        try {
            goToLevel((String) arg1);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                 | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText(e.getClass().toString());
            alert.show();
        }
    }

}

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

/**
 * The {@code GameController} class is responsible for managing the game's flow and handling transitions
 * between different levels. It observes changes in the game state and updates the current level accordingly.
 *
 * <p>This class uses reflection to dynamically load and transition between levels in the game.</p>
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/controller/GameController.java</p>
 */

public class GameController implements Observer {

    private static final String LEVEL_ONE_CLASS_NAME = "com.finalflight.game.level.LevelOne";
    private static final double SCENE_WIDTH = 1540.0;
    private static final double SCENE_HEIGHT = 870.0;
    private final Stage stage;

    /**
     * Constructs a {@code GameController} instance with the specified primary stage.
     *
     * @param stage the primary {@link Stage} for the game.
     */
    public GameController(Stage stage) {
        this.stage = stage;
    }

    /**
     * Launches the game by showing the primary stage and navigating to the first level.
     *
     * @throws ClassNotFoundException    if the class for the first level cannot be found.
     * @throws NoSuchMethodException     if the constructor of the level class cannot be found.
     * @throws SecurityException         if access to the constructor is denied.
     * @throws InstantiationException    if the level class cannot be instantiated.
     * @throws IllegalAccessException    if the constructor of the level class is inaccessible.
     * @throws IllegalArgumentException  if invalid arguments are passed to the constructor.
     * @throws InvocationTargetException if the constructor throws an exception.
     */
    public void launchGame() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        stage.show();
        goToLevel(LEVEL_ONE_CLASS_NAME);
    }

    /**
     * Transitions the game to the specified level by dynamically loading and initializing the level class.
     *
     * @param className the fully qualified name of the level class to transition to.
     * @throws ClassNotFoundException    if the class for the specified level cannot be found.
     * @throws NoSuchMethodException     if the constructor of the level class cannot be found.
     * @throws SecurityException         if access to the constructor is denied.
     * @throws InstantiationException    if the level class cannot be instantiated.
     * @throws IllegalAccessException    if the constructor of the level class is inaccessible.
     * @throws IllegalArgumentException  if invalid arguments are passed to the constructor.
     * @throws InvocationTargetException if the constructor throws an exception.
     */
    private void goToLevel(String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> myClass = Class.forName(className);
        Constructor<?> constructor = myClass.getConstructor(double.class, double.class);
        BaseLevel myLevel = (BaseLevel) constructor.newInstance(SCENE_HEIGHT, SCENE_WIDTH);
        myLevel.addObserver(this);
        Scene scene = myLevel.initializeScene();
        stage.setScene(scene);
        myLevel.startGame();

    }

    /**
     * Updates the game controller when notified of a change in the game state.
     * This method handles transitions between levels.
     *
     * @param arg0 the observable object (not used).
     * @param arg1 the new level's class name as a {@code String}.
     */
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

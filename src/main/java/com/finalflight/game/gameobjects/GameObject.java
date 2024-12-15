package com.finalflight.game.gameobjects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The {@code GameObject} class represents a generic game object in the game world.
 * It extends {@link ImageView} and provides basic properties such as image, position, and size.
 * This class is designed to be subclassed for specific game object behavior.
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/gameobjects/GameObject.java</p>
 */
public abstract class GameObject extends ImageView {

    private static final String IMAGE_LOCATION = "/com/finalflight/game/images/";

    /**
     * Constructs a {@code GameObject} with the specified image, size, and position.
     *
     * @param imageName    the name of the image file for the game object.
     * @param imageHeight  the height of the image in pixels.
     * @param initialXPos  the initial X position of the game object.
     * @param initialYPos  the initial Y position of the game object.
     */
    public GameObject(String imageName, int imageHeight, double initialXPos, double initialYPos) {
        this.setImage(new Image(getClass().getResource(IMAGE_LOCATION + imageName).toExternalForm()));
        this.setLayoutX(initialXPos);
        this.setLayoutY(initialYPos);
        this.setFitHeight(imageHeight);
        this.setPreserveRatio(true);
    }

    /**
     * Overloaded constructor for testing purposes. Allows skipping image loading.
     *
     * @param imageName         the name of the image file for the game object.
     * @param imageHeight       the height of the image in pixels.
     * @param initialXPos       the initial X position of the game object.
     * @param initialYPos       the initial Y position of the game object.
     * @param skipImageLoading  {@code true} to skip image loading, {@code false} otherwise.
     */
    public GameObject(String imageName, int imageHeight, double initialXPos, double initialYPos, boolean skipImageLoading) {
        if (!skipImageLoading) {
            this.setImage(new Image(getClass().getResource(IMAGE_LOCATION + imageName).toExternalForm()));
        }
        this.setLayoutX(initialXPos);
        this.setLayoutY(initialYPos);
        this.setFitHeight(imageHeight);
        this.setPreserveRatio(true);
    }

    /**
     * Updates the position of the game object. Must be implemented by subclasses.
     */
    public abstract void updatePosition();

    /**
     * Moves the game object horizontally by the specified amount.
     *
     * @param horizontalMove the horizontal displacement.
     */
    protected void moveHorizontally(double horizontalMove) {
        this.setTranslateX(getTranslateX() + horizontalMove);
    }

    /**
     * Moves the game object vertically by the specified amount.
     *
     * @param verticalMove the vertical displacement.
     */
    protected void moveVertically(double verticalMove) {
        this.setTranslateY(getTranslateY() + verticalMove);
    }

}

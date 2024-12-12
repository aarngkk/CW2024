package com.finalflight.game.gameobjects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class GameObject extends ImageView {

    private static final String IMAGE_LOCATION = "/com/finalflight/game/images/";

    public GameObject(String imageName, int imageHeight, double initialXPos, double initialYPos) {
        this.setImage(new Image(getClass().getResource(IMAGE_LOCATION + imageName).toExternalForm()));
        this.setLayoutX(initialXPos);
        this.setLayoutY(initialYPos);
        this.setFitHeight(imageHeight);
        this.setPreserveRatio(true);
    }

    // Overloaded constructor for testing (skips image loading)
    public GameObject(String imageName, int imageHeight, double initialXPos, double initialYPos, boolean skipImageLoading) {
        if (!skipImageLoading) {
            this.setImage(new Image(getClass().getResource(IMAGE_LOCATION + imageName).toExternalForm()));
        }
        this.setLayoutX(initialXPos);
        this.setLayoutY(initialYPos);
        this.setFitHeight(imageHeight);
        this.setPreserveRatio(true);
    }

    public abstract void updatePosition();

    protected void moveHorizontally(double horizontalMove) {
        this.setTranslateX(getTranslateX() + horizontalMove);
    }

    protected void moveVertically(double verticalMove) {
        this.setTranslateY(getTranslateY() + verticalMove);
    }

}

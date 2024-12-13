/**
 * The {@code HeartDisplay} class represents a graphical display of hearts in the game,
 * which is typically used to represent a player's health. It manages the creation, addition,
 * removal, and clearing of heart icons in an {@link HBox} container.
 *
 * <p>The display is positioned at a specified X and Y coordinate, and the number of hearts
 * is configurable.</p>
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/ui/HeartDisplay.java">HeartDisplay.java</a></p>
 */
package com.finalflight.game.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class HeartDisplay {

    private static final String HEART_IMAGE_NAME = "/com/finalflight/game/images/heart.png";
    private static final int HEART_HEIGHT = 50;
    private static final int INDEX_OF_FIRST_ITEM = 0;
    private HBox container;
    private final double containerXPosition;
    private final double containerYPosition;
    private final int numberOfHeartsToDisplay;

    /**
     * Constructs a {@code HeartDisplay} at the specified position with the specified
     * number of hearts to display initially.
     *
     * @param xPosition the X-coordinate for the heart display container.
     * @param yPosition the Y-coordinate for the heart display container.
     * @param heartsToDisplay the initial number of hearts to display.
     */
    public HeartDisplay(double xPosition, double yPosition, int heartsToDisplay) {
        this.containerXPosition = xPosition;
        this.containerYPosition = yPosition;
        this.numberOfHeartsToDisplay = heartsToDisplay;
        initializeContainer();
        initializeHearts();
    }

    /**
     * Initializes the container for the hearts, positioning it at the specified coordinates.
     */
    private void initializeContainer() {
        container = new HBox();
        container.setLayoutX(containerXPosition);
        container.setLayoutY(containerYPosition);
    }

    /**
     * Initializes the hearts by creating and adding the specified number of heart icons
     * to the container.
     */
    private void initializeHearts() {
        for (int i = 0; i < numberOfHeartsToDisplay; i++) {
            ImageView heart = new ImageView(new Image(getClass().getResource(HEART_IMAGE_NAME).toExternalForm()));

            heart.setFitHeight(HEART_HEIGHT);
            heart.setPreserveRatio(true);
            container.getChildren().add(heart);
        }
    }

    /**
     * Clears all hearts from the display.
     */
    public void clearHearts() {
        container.getChildren().clear();
    }

    /**
     * Adds a single heart to the display.
     */
    public void addHeart() {
        ImageView heart = new ImageView(new Image(getClass().getResource(HEART_IMAGE_NAME).toExternalForm()));
        heart.setFitHeight(HEART_HEIGHT);
        heart.setPreserveRatio(true);
        container.getChildren().add(heart);
    }

    /**
     * Removes the last heart from the display, if any exist.
     */
    public void removeHeart() {
        if (!container.getChildren().isEmpty())
            container.getChildren().remove(INDEX_OF_FIRST_ITEM);
    }

    /**
     * Retrieves the heart container.
     *
     * @return the {@link HBox} containing the heart icons.
     */
    public HBox getContainer() {
        return container;
    }

}

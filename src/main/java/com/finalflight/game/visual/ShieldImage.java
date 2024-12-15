package com.finalflight.game.visual;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The {@code ShieldImage} class represents a graphical shield component in the game.
 * It extends {@link ImageView} to display a shield image and provides methods to
 * control its visibility and position.
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/visual/ShieldImage.java</p>
 */
public class ShieldImage extends ImageView {

    private static final String SHIELD_IMAGE = "/com/finalflight/game/images/bossshield.png";
    private static final int SHIELD_IMAGE_WIDTH = 500;
    private static final int SHIELD_IMAGE_HEIGHT = 250;
    private static final double SHIELD_IMAGE_X_OFFSET = -25;
    private static final double SHIELD_IMAGE_Y_OFFSET = -25;

    /**
     * Constructs a {@code ShieldImage} instance at the specified position.
     *
     * @param xPosition the x-coordinate for the shield's position.
     * @param yPosition the y-coordinate for the shield's position.
     */
    public ShieldImage(double xPosition, double yPosition) {
        this.setLayoutX(xPosition);
        this.setLayoutY(yPosition);
        this.setImage(new Image(getClass().getResource(SHIELD_IMAGE).toExternalForm()));
        this.setVisible(false);
        this.setFitWidth(SHIELD_IMAGE_WIDTH);
        this.setFitHeight(SHIELD_IMAGE_HEIGHT);
    }

    /**
     * Makes the shield visible.
     */
    public void showShield() {
        this.setVisible(true);
    }

    /**
     * Hides the shield, making it invisible.
     */
    public void hideShield() {
        this.setVisible(false);
    }

    /**
     * Gets the x-offset for the shield's position.
     *
     * @return the x-offset as a {@code double}.
     */
    public double getShieldXOffset() {
        return SHIELD_IMAGE_X_OFFSET;
    }

    /**
     * Gets the y-offset for the shield's position.
     *
     * @return the y-offset as a {@code double}.
     */
    public double getShieldYOffset() {
        return SHIELD_IMAGE_Y_OFFSET;
    }

}

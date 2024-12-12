package com.finalflight.game.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ShieldImage extends ImageView {

	private static final String SHIELD_IMAGE = "/com/finalflight/game/images/shield.png";
	private static final int SHIELD_IMAGE_WIDTH = 500;
	private static final int SHIELD_IMAGE_HEIGHT = 250;
	private static final double SHIELD_IMAGE_X_OFFSET = -25;
	private static final double SHIELD_IMAGE_Y_OFFSET = -25;

	public ShieldImage(double xPosition, double yPosition) {
		this.setLayoutX(xPosition);
		this.setLayoutY(yPosition);
		this.setImage(new Image(getClass().getResource(SHIELD_IMAGE).toExternalForm()));
		this.setVisible(false);
		this.setFitWidth(SHIELD_IMAGE_WIDTH);
		this.setFitHeight(SHIELD_IMAGE_HEIGHT);
	}

	public void showShield() {
		this.setVisible(true);
	}

	public void hideShield() {
		this.setVisible(false);
	}

	public double getShieldXOffset() {
		return SHIELD_IMAGE_X_OFFSET;
	}

	public double getShieldYOffset() {
		return SHIELD_IMAGE_Y_OFFSET;
	}

}

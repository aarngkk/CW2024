package com.finalflight.game.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameOverImage extends ImageView {

	private static final String IMAGE_NAME = "/com/finalflight/game/images/gameover.png";

	public GameOverImage(double xPosition, double yPosition) {
		Image image = new Image(getClass().getResource(IMAGE_NAME).toExternalForm());
		setImage(image);


		setFitWidth(image.getWidth() / 2);
		setFitHeight(image.getHeight() / 2);

		setLayoutX(xPosition);
		setLayoutY(yPosition);
	}

}

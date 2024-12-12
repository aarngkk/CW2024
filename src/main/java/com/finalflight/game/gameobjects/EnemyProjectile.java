package com.finalflight.game.gameobjects;

public class EnemyProjectile extends AbstractProjectile {

    private static final String IMAGE_NAME = "enemyfire.png";
    private static final int IMAGE_HEIGHT = 16;
    private static final int HORIZONTAL_VELOCITY = -10;

    public EnemyProjectile(double initialXPos, double initialYPos) {
        super(IMAGE_NAME, IMAGE_HEIGHT, initialXPos, initialYPos);
    }

    @Override
    public void updatePosition() {
        moveHorizontally(HORIZONTAL_VELOCITY);
    }

    @Override
    public void updateActor() {
        updatePosition();
    }


}

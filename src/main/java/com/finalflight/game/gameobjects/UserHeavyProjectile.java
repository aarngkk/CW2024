package com.finalflight.game.gameobjects;

public class UserHeavyProjectile extends Projectile {

    private static final String IMAGE_NAME = "heavyfire.png";
    private static final int IMAGE_HEIGHT = 50;
    private static final double HORIZONTAL_VELOCITY = 8;
    private static final double VERTICAL_VELOCITY = 10;
    private static final int PROJECTILE_HEALTH = 6;
    private int health;

    public UserHeavyProjectile(double initialXPos, double initialYPos) {
        super(IMAGE_NAME, IMAGE_HEIGHT, initialXPos, initialYPos);
        this.health = PROJECTILE_HEALTH;
    }

    @Override
    public void updatePosition() {
        setTranslateX(getTranslateX() + HORIZONTAL_VELOCITY);
        setTranslateY(getTranslateY() + VERTICAL_VELOCITY);
    }

    @Override
    public void takeDamage() {
        health--; // Decrease health by 1
        if (health <= 0) {
            health = 0;
            this.destroy(); // Destroy the projectile if health reaches 0
        }
    }

    @Override
    public void updateActor() {
        updatePosition();
    }

    public int getHealth() {
        return health;
    }
}

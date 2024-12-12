package com.finalflight.game.gameobjects;

public abstract class DestructibleGameObject extends GameObject implements Destructible {

    private boolean isDestroyed;

    public DestructibleGameObject(String imageName, int imageHeight, double initialXPos, double initialYPos) {
        super(imageName, imageHeight, initialXPos, initialYPos);
        isDestroyed = false;
    }

    // Overloaded constructor for testing (skips image loading)
    public DestructibleGameObject(String imageName, int imageHeight, double initialXPos, double initialYPos, boolean skipImageLoading) {
        super(imageName, imageHeight, initialXPos, initialYPos, skipImageLoading);
        isDestroyed = false;
    }

    @Override
    public abstract void updatePosition();

    public abstract void updateActor();

    @Override
    public abstract void takeDamage();

    @Override
    public void destroy() {
        setDestroyed();
    }

    protected void setDestroyed() {
        this.isDestroyed = true;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

}

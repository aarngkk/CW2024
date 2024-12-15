package com.finalflight.game.gameobjects;

/**
 * The {@code DestructibleGameObject} class extends {@link GameObject} and implements
 * {@link Destructible}, providing properties and methods for handling destructible
 * game objects.
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/gameobjects/DestructibleGameObject.java</p>
 */
public abstract class DestructibleGameObject extends GameObject implements Destructible {

    private boolean isDestroyed;

    /**
     * Constructs a destructible game object with the specified properties.
     *
     * @param imageName    the name of the image file for the game object.
     * @param imageHeight  the height of the image in pixels.
     * @param initialXPos  the initial X position of the game object.
     * @param initialYPos  the initial Y position of the game object.
     */
    public DestructibleGameObject(String imageName, int imageHeight, double initialXPos, double initialYPos) {
        super(imageName, imageHeight, initialXPos, initialYPos);
        isDestroyed = false;
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
    public DestructibleGameObject(String imageName, int imageHeight, double initialXPos, double initialYPos, boolean skipImageLoading) {
        super(imageName, imageHeight, initialXPos, initialYPos, skipImageLoading);
        isDestroyed = false;
    }

    /**
     * Updates the position of the destructible game object. Must be implemented by subclasses.
     */
    @Override
    public abstract void updatePosition();

    /**
     * Updates the game object's state during the game loop. Must be implemented by subclasses.
     */
    public abstract void updateActor();

    /**
     * Applies damage to the game object. Implementation determines how damage affects the object.
     */
    @Override
    public abstract void takeDamage();

    /**
     * Marks the game object as destroyed and performs cleanup if necessary.
     */
    @Override
    public void destroy() {
        setDestroyed();
    }

    /**
     * Sets the object's state to destroyed.
     */
    protected void setDestroyed() {
        this.isDestroyed = true;
    }

    /**
     * Checks if the object is destroyed.
     *
     * @return {@code true} if the object is destroyed, {@code false} otherwise.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

}

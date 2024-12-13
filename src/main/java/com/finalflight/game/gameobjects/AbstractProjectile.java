/**
 * The {@code AbstractProjectile} class serves as a base class for all projectile objects
 * in the game. It provides core functionality such as maintaining image properties,
 * handling damage, and defining abstract methods for position updates.
 *
 * <p>This class is intended to be extended by specific projectile types to implement
 * their unique behaviors, such as movement patterns and collision logic.</p>
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/gameobjects/AbstractProjectile.java">AbstractProjectile.java</a></p>
 */
package com.finalflight.game.gameobjects;

public abstract class AbstractProjectile extends DestructibleGameObject {

    private final String imageName;
    private final int imageHeight;

    /**
     * Constructs an {@code AbstractProjectile} with the specified image and initial position.
     *
     * @param imageName   the name of the image representing the projectile.
     * @param imageHeight the height of the image in pixels.
     * @param initialXPos the initial X-coordinate of the projectile.
     * @param initialYPos the initial Y-coordinate of the projectile.
     */
    public AbstractProjectile(String imageName, int imageHeight, double initialXPos, double initialYPos) {
        super(imageName, imageHeight, initialXPos, initialYPos);
        this.imageName = imageName;
        this.imageHeight = imageHeight;
    }

    /**
     * Overloaded constructor for testing purposes, allowing optional skipping of image loading.
     *
     * @param imageName        the name of the image representing the projectile.
     * @param imageHeight      the height of the image in pixels.
     * @param initialXPos      the initial X-coordinate of the projectile.
     * @param initialYPos      the initial Y-coordinate of the projectile.
     * @param skipImageLoading {@code true} to skip loading the image resource, {@code false} otherwise.
     */
    public AbstractProjectile(String imageName, int imageHeight, double initialXPos, double initialYPos, boolean skipImageLoading) {
        super(imageName, imageHeight, initialXPos, initialYPos, skipImageLoading);
        this.imageName = imageName;
        this.imageHeight = imageHeight;
    }

    /**
     * Applies damage to the projectile, triggering its destruction.
     */
    @Override
    public void takeDamage() {
        this.destroy();
    }

    /**
     * Updates the position of the projectile. Specific movement behavior
     * should be implemented by subclasses.
     */
    @Override
    public abstract void updatePosition();

    /**
     * Retrieves the name of the image representing the projectile.
     *
     * @return the image name as a {@code String}.
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Retrieves the height of the image representing the projectile.
     *
     * @return the image height in pixels as an {@code int}.
     */
    public int getImageHeight() {
        return imageHeight;
    }

}

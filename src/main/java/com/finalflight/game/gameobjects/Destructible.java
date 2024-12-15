package com.finalflight.game.gameobjects;

/**
 * The {@code Destructible} interface defines behavior for game objects that can take damage
 * and be destroyed.
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/gameobjects/Destructible.java</p>
 */
public interface Destructible {

    /**
     * Applies damage to the object. Implementation determines how damage affects the object.
     */
    void takeDamage();

    /**
     * Destroys the object, marking it as removed from the game world.
     */
    void destroy();

}

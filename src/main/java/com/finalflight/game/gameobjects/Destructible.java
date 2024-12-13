/**
 * The {@code Destructible} interface defines behavior for game objects that can take damage
 * and be destroyed.
 *
 * <p>Original Source Code:
 * <a href="com/finalflight/game/gameobjects/Destructible.java">Destructible.java</a></p>
 */
package com.finalflight.game.gameobjects;

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

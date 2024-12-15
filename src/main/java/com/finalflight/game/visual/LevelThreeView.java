package com.finalflight.game.visual;

import com.finalflight.game.gameobjects.BossPlane;
import com.finalflight.game.level.BaseLevel;
import javafx.scene.Group;
import javafx.scene.control.ProgressBar;

/**
 * The {@code LevelThreeView} class extends {@link BaseLevelView} and provides
 * the visual components and functionality specific to the third level of the game.
 * This includes managing the boss's health bar, shield visuals, and explosion effects.
 *
 * <p>This class is responsible for initializing and updating the Heads-Up Display (HUD)
 * and managing visual elements like the boss's health bar and shield during gameplay.</p>
 *
 * <p>Original Source Code: (Project Root)/src/main/java/com/finalflight/game/visual/LevelThreeView.java</p>
 */
public class LevelThreeView extends BaseLevelView {

    private final Group root;
    private ProgressBar bossHealthBar;
    private ShieldImage shieldImage;

    /**
     * Constructs a {@code LevelThreeView} object for the third level of the game.
     *
     * @param root           the root {@link Group} that contains all the visual elements.
     * @param heartsToDisplay the number of hearts to display in the HUD.
     * @param baseLevel      the {@link BaseLevel} object containing the level configuration.
     */
    public LevelThreeView(Group root, int heartsToDisplay, BaseLevel baseLevel) {
        super(root, heartsToDisplay, baseLevel.getScreenWidth(), baseLevel.getScreenHeight(), baseLevel);
        this.root = root;
    }

    /**
     * Initializes the Heads-Up Display (HUD) for Level Three, including the firing mode text,
     * boost bar, and the boss's health bar.
     *
     * @param unusedKillsToAdvance unused parameter, as kills to advance is not relevant in this level.
     * @param boostBarY           the Y-coordinate for positioning the boost bar.
     * @param firingModeY         the Y-coordinate for positioning the firing mode text.
     */
    @Override
    public void initializeHUD(int unusedKillsToAdvance, double boostBarY, double firingModeY) {
        // Initialize only the firing mode text and boost bar
        initializeFiringModeText(firingModeY);
        initializeBoostBar(boostBarY);
        initializeBossHealthBar();
    }

    /**
     * Initializes the boss's health bar and positions it at the top center of the screen.
     */
    public void initializeBossHealthBar() {
        bossHealthBar = new ProgressBar();
        bossHealthBar.setPrefWidth(500);
        bossHealthBar.setPrefHeight(25);
        bossHealthBar.getStyleClass().add("health-bar");

        // Position the health bar at the top center of the screen
        bossHealthBar.setLayoutX((getScreenWidth() - bossHealthBar.getPrefWidth()) / 2);
        bossHealthBar.setLayoutY(30);

        // Add the health bar to the root
        getRoot().getChildren().add(bossHealthBar);
    }

    /**
     * Updates the boss's health bar progress and style based on the boss's current health
     * and shield status.
     *
     * @param healthPercentage the current health of the boss as a percentage (0.0 to 1.0).
     * @param isShielded       {@code true} if the boss is shielded, {@code false} otherwise.
     */
    public void updateBossHealthBar(double healthPercentage, boolean isShielded) {
        bossHealthBar.setProgress(healthPercentage);

        bossHealthBar.getStyleClass().removeAll("normal-bar", "shielded-bar");

        if (isShielded) {
            bossHealthBar.getStyleClass().add("shielded-bar");
        } else {
            bossHealthBar.getStyleClass().add("normal-bar");
        }
    }

    /**
     * Initializes the visual representation of the boss's shield.
     *
     * @param boss the {@link BossPlane} object representing the boss in the game.
     */
    public void initializeShield(BossPlane boss) {
        shieldImage = new ShieldImage(boss.getLayoutX(), boss.getLayoutY());
        root.getChildren().add(shieldImage);
    }

    /**
     * Updates the position and visibility of the shield image based on the boss's state.
     *
     * @param boss the {@link BossPlane} object representing the boss in the game.
     */
    public void updateShield(BossPlane boss) {
        if (shieldImage != null) {
            shieldImage.setTranslateX(boss.getTranslateX() + shieldImage.getShieldXOffset());
            shieldImage.setTranslateY(boss.getTranslateY() + shieldImage.getShieldYOffset());

            if (boss.getIsShielded()) {
                shieldImage.showShield();
            } else {
                shieldImage.hideShield();
            }
        }
    }

    /**
     * Displays the boss explosion effect at the specified position and plays the associated sound effect.
     *
     * @param x the X-coordinate of the explosion's center.
     * @param y the Y-coordinate of the explosion's center.
     */
    public void showBossExplosion(double x, double y) {
        BossExplosionEffect bossExplosionEffect = new BossExplosionEffect(x, y);
        root.getChildren().add(bossExplosionEffect);
        bossExplosionEffect.playExplosionSound();
    }

}

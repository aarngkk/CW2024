package com.finalflight.game.ui;

import com.finalflight.game.gameobjects.BossPlane;
import com.finalflight.game.level.BaseLevel;
import javafx.scene.Group;
import javafx.scene.control.ProgressBar;

public class LevelThreeView extends BaseLevelView {

    private final Group root;
    private ProgressBar bossHealthBar;
    private ShieldImage shieldImage;

    public LevelThreeView(Group root, int heartsToDisplay, BaseLevel baseLevel) {
        super(root, heartsToDisplay, baseLevel.getScreenWidth(), baseLevel.getScreenHeight(), baseLevel);
        this.root = root;
    }

    @Override
    public void initializeHUD(int unusedKillsToAdvance, double boostBarY, double firingModeY) {
        // Initialize only the firing mode text and boost bar
        initializeFiringModeText(firingModeY);
        initializeBoostBar(boostBarY);
        initializeBossHealthBar();
    }

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

    public void updateBossHealthBar(double healthPercentage, boolean isShielded) {
        bossHealthBar.setProgress(healthPercentage);

        bossHealthBar.getStyleClass().removeAll("normal-bar", "shielded-bar");

        if (isShielded) {
            bossHealthBar.getStyleClass().add("shielded-bar");
        } else {
            bossHealthBar.getStyleClass().add("normal-bar");
        }
    }

    public void initializeShield(BossPlane boss) {
        shieldImage = new ShieldImage(boss.getLayoutX(), boss.getLayoutY());
        root.getChildren().add(shieldImage);
    }

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

    public void showBossExplosion(double x, double y) {
        BossExplosionEffect bossExplosionEffect = new BossExplosionEffect(x, y);
        root.getChildren().add(bossExplosionEffect);
        bossExplosionEffect.playExplosionSound();
    }

}

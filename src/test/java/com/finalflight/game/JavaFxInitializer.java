package com.finalflight.game;

import javafx.application.Platform;

class JavaFxInitializer {
    private static boolean initialized = false;

    static void initializeToolkit() {
        if (!initialized) {
            Platform.startup(() -> {});
            initialized = true;
        }
    }
}
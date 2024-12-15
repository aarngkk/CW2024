package com.finalflight.game;

import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    static void setupJavaFxToolkit() {
        JavaFxInitializer.initializeToolkit();
    }
}

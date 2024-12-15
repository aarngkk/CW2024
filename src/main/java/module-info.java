module com.finalflight.game {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    exports com.finalflight.game.controller;
    exports com.finalflight.game.main;
    exports com.finalflight.game.audio;
    exports com.finalflight.game.gameobjects;
    exports com.finalflight.game.level;
    exports com.finalflight.game.visual;
    opens com.finalflight.game.fxml to javafx.fxml;
    opens com.finalflight.game.controller to javafx.fxml;
    opens com.finalflight.game.main to javafx.fxml;
    opens com.finalflight.game.level to javafx.fxml;
    opens com.finalflight.game.gameobjects to javafx.fxml;
    opens com.finalflight.game.visual to javafx.fxml;
    opens com.finalflight.game.audio to javafx.fxml;
}
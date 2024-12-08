module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.finalflight.game.fxml to javafx.fxml;
    opens com.finalflight.game.controller to javafx.fxml;
    exports com.finalflight.game.controller;
    exports com.finalflight.game.main;
    opens com.finalflight.game.main to javafx.fxml;
    opens com.finalflight.game.level to javafx.fxml;
    opens com.finalflight.game.gameobjects to javafx.fxml;
    opens com.finalflight.game.ui to javafx.fxml;
    opens com.finalflight.game.audio to javafx.fxml;
}
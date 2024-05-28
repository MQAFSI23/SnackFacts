package utils;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import java.util.Objects;

public class MyPopup extends Popup {
    public void showPopup(String message, Stage stage, boolean isFailure) {
        StackPane popupContent = new StackPane();
        popupContent.setPrefSize(message.length() * 8, 30);
        popupContent.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());

        if (isFailure) {
            popupContent.getStyleClass().add("popupFailBackground");
        } else {
            popupContent.getStyleClass().add("popupSuccessBackground");
        }

        Label label = new Label(message);
        label.getStyleClass().add("popupLabel");

        popupContent.getChildren().add(label);
        this.getContent().add(popupContent);
        this.show(stage);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> this.hide());
        delay.play();
    }
}
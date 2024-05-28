package app;

import utils.DatabaseInitialization;

import scenes.LoginScene;
import javafx.stage.Stage;
import javafx.application.Application;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginScene loginScene = new LoginScene(primaryStage);
        primaryStage.setScene(loginScene.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        DatabaseInitialization.initialize();
        launch(args);
    }
}
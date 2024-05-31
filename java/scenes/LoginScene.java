package scenes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import dao.UserDao;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import models.AbstractUser;
import models.User;
import utils.MyPopup;
import java.util.Objects;
import java.util.regex.Pattern;

public class LoginScene {
    private AbstractUser abstractUser;
    private Scene scene;
    private Stage stage;
    private AnchorPane root;
    private JFXButton toggleButton;
    private double xOffset = 0, yOffset = 0;
    private final Stage homeStage = new Stage();
    private JFXPasswordField passwordFieldMasked;
    private JFXTextField usernameField, passwordField, nicknameField;
    private Tooltip usernameTooltip, passwordTooltip, nicknameTooltip;
    @SuppressWarnings("unused")
    private boolean signUpPress = false, signInPress = true, isGuest, usernameShow, passwordShow, nicknameShow;

    public LoginScene(Stage stage) {
        this.stage = stage;
        init();
    }

    private void init() {
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);

        root = new AnchorPane();
        root.setPrefSize(600, 300);
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());
        root.getStyleClass().add("loginBackground");

        AnchorPane loginPane = new AnchorPane();
        loginPane.setLayoutX(224);
        loginPane.setLayoutY(73);
        loginPane.setPrefSize(310, 250);
        loginPane.getStyleClass().add("loginPane");

        usernameField = new JFXTextField();
        usernameField.setLayoutX(72);
        usernameField.setLayoutY(85);
        usernameField.setPrefSize(174, 25);
        usernameField.setPromptText("Enter Username");
        usernameField.getStyleClass().add("textField");
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> { clearTooltip(usernameTooltip); usernameShow = false; });

        passwordFieldMasked = new JFXPasswordField();
        passwordFieldMasked.setLayoutX(72);
        passwordFieldMasked.setLayoutY(135);
        passwordFieldMasked.setPromptText("Enter Password");
        passwordFieldMasked.getStyleClass().add("textField");
        passwordFieldMasked.setPrefSize(174, 25);

        passwordField = new JFXTextField();
        passwordField.setLayoutX(72);
        passwordField.setLayoutY(135);
        passwordField.setVisible(false);
        passwordField.setPromptText("Enter Password");
        passwordField.getStyleClass().add("textField");
        passwordField.setPrefSize(174, 25);
        passwordField.textProperty().bindBidirectional(passwordFieldMasked.textProperty());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> { clearTooltip(passwordTooltip); passwordShow = false; });

        toggleButton = new JFXButton("🫣");
        toggleButton.setLayoutX(250);
        toggleButton.setLayoutY(135);
        toggleButton.getStyleClass().add("toggleButton");
        toggleButton.setPrefSize(38, 38);
        toggleButton.setOnAction(event -> togglePasswordVisibility());

        JFXButton loginButton = new JFXButton("Sign In");
        loginButton.setButtonType(JFXButton.ButtonType.RAISED);
        loginButton.setLayoutX(72);
        loginButton.setLayoutY(176);
        loginButton.setPrefSize(75, 30);
        loginButton.getStyleClass().add("loginButton");
        loginButton.setOnAction(event -> {
            if (signInPress) {
                abstractUser = new User(usernameField.getText(), passwordField.getText());
                
                if (abstractUser.getUsername().equals("@dm1N") && abstractUser.authenticate()) {
                    new MyPopup().showPopup("Successfully signed in as " + new UserDao().getNicknameByUsername(abstractUser.getUsername()), stage, false);
                    PauseTransition delay = new PauseTransition(Duration.seconds(2));
                    delay.setOnFinished(event2 -> switchToHome());
                    delay.play();
                } else if (validateInputs()) {
                    if (abstractUser.authenticate()) {
                        new MyPopup().showPopup("Successfully signed in as " + new UserDao().getNicknameByUsername(abstractUser.getUsername()), stage, false);
                        PauseTransition delay = new PauseTransition(Duration.seconds(2));
                        delay.setOnFinished(event2 -> switchToHome());
                        delay.play();
                    } else {
                        new MyPopup().showPopup("Invalid username or password", stage, true);
                    }
                }
            } else {
                clearAllTooltips();
                moveForSignIn();
                if (loginPane.getChildren().contains(nicknameField)) loginPane.getChildren().remove(nicknameField);
            }
        });

        JFXButton guestButton = new JFXButton("Guest");
        guestButton.setLayoutX(120);
        guestButton.setLayoutY(210);
        guestButton.setPrefSize(70, 30);
        guestButton.getStyleClass().add("guestButton");
        guestButton.setOnAction(event -> {
            new MyPopup().showPopup("Successfully signed in as Guest", stage, false);
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event2 -> { isGuest = true; switchToHome(); });
            delay.play();
        });

        JFXButton signUpButton = new JFXButton("Sign Up");
        signUpButton.setButtonType(JFXButton.ButtonType.RAISED);
        signUpButton.setLayoutX(171);
        signUpButton.setLayoutY(176);
        signUpButton.setPrefSize(75, 30);
        signUpButton.getStyleClass().add("signUpButton");
        signUpButton.setOnAction(event -> {
            if (signUpPress) {
                nicknameField = (JFXTextField) loginPane.getChildren().get(loginPane.getChildren().indexOf(nicknameField));
                if (validateInputs()) {
                    abstractUser = new User(usernameField.getText(), passwordField.getText(), nicknameField.getText());
                    if (abstractUser.isUsernameAvailable()){
                        if (abstractUser.isNicknameAvailable()) {
                            new UserDao().addUser(abstractUser);
                            new MyPopup().showPopup("Successfully signed up as " + usernameField.getText(), stage, false);

                            PauseTransition delay = new PauseTransition(Duration.seconds(2));
                            delay.setOnFinished(event2 -> moveForSignIn());
                            delay.play();
                        } else { 
                            new MyPopup().showPopup("Nickname already exists", stage, true);
                        }
                    } else {
                        new MyPopup().showPopup("Username already exists", stage, true);
                    }
                }
            } else {
                clearAllTooltips();
                moveForSignUp();
            }
        });

        loginPane.getChildren().addAll(usernameField, passwordField, passwordFieldMasked, toggleButton, loginButton, signUpButton, guestButton);
        loginPane.centerShapeProperty().setValue(true);

        AnchorPane sideBar = new AnchorPane();
        sideBar.setLayoutX(31);
        sideBar.setLayoutY(48);
        sideBar.setPrefSize(220, 300);
        sideBar.getStyleClass().add("loginSideBar");
        sideBar.centerShapeProperty().setValue(true);

        ImageView logo = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Logo.png"))));
        logo.setLayoutX(35);
        logo.setLayoutY(70);
        logo.setSmooth(true);
        logo.setFitWidth(150);
        logo.setPreserveRatio(true);

        Button closeButton = new Button("⛌");
        closeButton.getStyleClass().add("closeButton");
        closeButton.setLayoutX(182);
        closeButton.setLayoutY(14);
        closeButton.setOnAction(event -> System.exit(0));

        sideBar.getChildren().addAll(closeButton, logo);

        root.getChildren().addAll(loginPane, sideBar);
        scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        // Add title bar dragging functionality
        scene.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        stage.setScene(scene);
    }

    private void addNicknameField(AnchorPane loginPane) {
        nicknameField = new JFXTextField();
        nicknameField.setLayoutX(72);
        nicknameField.setLayoutY(35);
        nicknameField.setPrefSize(174, 25);
        nicknameField.setPromptText("Enter Nickname");
        nicknameField.getStyleClass().add("textField");
        nicknameField.textProperty().addListener((observable, oldValue, newValue) -> { clearTooltip(nicknameTooltip); nicknameShow = false; });

        loginPane.getChildren().add(nicknameField);
    }

    private void togglePasswordVisibility() {
        if (passwordFieldMasked.isVisible()) {
            toggleButton.setText("🧐");
            passwordFieldMasked.setVisible(false);
            passwordField.setVisible(true);
        } else {
            toggleButton.setText("🫣");
            passwordFieldMasked.setVisible(true);
            passwordField.setVisible(false);
        }
    }

    private void moveForSignUp() {
        if (!signUpPress) {
            usernameField.clear();
            passwordField.clear();
            if (nicknameField != null) nicknameField.clear();

            AnchorPane loginPane = (AnchorPane) root.getChildren().get(0);
            AnchorPane sideBar = (AnchorPane) root.getChildren().get(1);

            TranslateTransition loginTrans = new TranslateTransition(Duration.seconds(0.5), loginPane);
            TranslateTransition sidePaneTrans = new TranslateTransition(Duration.seconds(0.5), sideBar);
            
            loginTrans.setToX(-197);
            sidePaneTrans.setToX(287);
            loginTrans.play();
            sidePaneTrans.play();

            addNicknameField(loginPane);
            signInPress = false;
            signUpPress = true;
        }
    }

    private void moveForSignIn() {
        if (!signInPress) {
            usernameField.clear();
            passwordField.clear();
            if (nicknameField != null) nicknameField.clear();

            AnchorPane loginPane = (AnchorPane) root.getChildren().get(0);
            AnchorPane sideBar = (AnchorPane) root.getChildren().get(1);
            
            TranslateTransition loginTrans = new TranslateTransition(Duration.seconds(0.5), loginPane);
            TranslateTransition sidePaneTrans = new TranslateTransition(Duration.seconds(0.5), sideBar);
            
            loginTrans.setToX(0);
            sidePaneTrans.setToX(0);
            loginTrans.play();
            sidePaneTrans.play();
            
            loginPane.getChildren().remove(nicknameField);
            signInPress = true;
            signUpPress = false;
        }
    }

    private void switchToHome() {
        stage.close();
        if (isGuest) {
            HomeScene homeScene = new HomeScene(homeStage, abstractUser, true);
            homeStage.setScene(homeScene.getScene());
            homeStage.setResizable(false);
            homeStage.show();
        } else {
            HomeScene homeScene = new HomeScene(homeStage, abstractUser, false);
            homeStage.setScene(homeScene.getScene());
            homeStage.setResizable(false);
            homeStage.show();
        }
    }

    private Tooltip showTooltip(JFXTextField field, String message, String type) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.getStyleClass().add("tooltip");
        if (type.equals("empty")) {
            if (signUpPress) { tooltip.show(stage,
                field.getScene().getWindow().getX() + field.getLayoutX() - 118,
                field.getScene().getWindow().getY() + field.getLayoutY() + field.getHeight() + 40); }
            else { tooltip.show(stage,
                field.getScene().getWindow().getX() + field.getLayoutX() + 405,
                field.getScene().getWindow().getY() + field.getLayoutY() + field.getHeight() + 40); }
        } else if (type.equals("invalid")) {
            if (signUpPress) {
                if (field == usernameField) { tooltip.show(stage, field.getScene().getWindow().getX() + field.getLayoutX() - 218, // username at sign up
                    field.getScene().getWindow().getY() + field.getLayoutY() + field.getHeight() + 20); }
                else if(field == passwordField) { tooltip.show(stage, field.getScene().getWindow().getX() + field.getLayoutX() - 260, // password at sign up
                    field.getScene().getWindow().getY() + field.getLayoutY() + field.getHeight() + 35); }
                else { tooltip.show(stage, field.getScene().getWindow().getX() + field.getLayoutX() - 219, // nickname at sign up
                    field.getScene().getWindow().getY() + field.getLayoutY() + field.getHeight() + 20); }
            } else {
                if (field == usernameField) { tooltip.show(stage, field.getScene().getWindow().getX() + field.getLayoutX() + 405, // username at sign in
                    field.getScene().getWindow().getY() + field.getLayoutY() + field.getHeight() + 20); }
                else if (field == passwordField) { tooltip.show(stage, field.getScene().getWindow().getX() + field.getLayoutX() + 405, // password at sign in
                    field.getScene().getWindow().getY() + field.getLayoutY() + field.getHeight() + 35); }
            }
        }
        return tooltip;
    }

    private void clearTooltip(Tooltip tooltip) {
        if (tooltip != null) {
            tooltip.hide();
        }
    }

    private void clearAllTooltips() {
        clearTooltip(usernameTooltip);
        clearTooltip(passwordTooltip);
        clearTooltip(nicknameTooltip);
        usernameShow = false; passwordShow = false; nicknameShow = false;
    }

    private boolean validateInputs() {
        boolean valid = true;
        String usernamePattern = "^[a-zA-Z0-9._]{4,15}$";
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,25}$";
        String nicknamePattern = "^[a-zA-Z0-9]{4,15}$";
        clearAllTooltips();

        // Validate username field
        if (usernameField.getText().strip().isEmpty()) {
            usernameTooltip = showTooltip(usernameField, "Please fill out this field.", "empty");
            usernameShow = true;
            valid = false;
        } else if (!Pattern.matches(usernamePattern, usernameField.getText().strip())) {
            usernameTooltip = showTooltip(usernameField, "Username must be 4—15 characters long\nand can only contain letters, numbers,\nunderscores, and dots.", "invalid");
            usernameShow = true;
            valid = false;
        }

        // Validate password field
        if (passwordField.getText().strip().isEmpty()) {
            passwordTooltip = showTooltip(passwordField, "Please fill out this field.", "empty");
            passwordShow = true;
            valid = false;
        } else if (!Pattern.matches(passwordPattern, passwordField.getText().strip())) {
            passwordTooltip = showTooltip(passwordField, "Password must be 8—25 characters long and can\nonly contain at least one uppercase letter, one\nlowercase letter, and one number.", "invalid");
            passwordShow = true;
            valid = false;
        }

        // Validate nickname field if signing up
        if (signUpPress && nicknameField != null) {
            if (nicknameField.getText().strip().isEmpty()) {
                nicknameTooltip = showTooltip(nicknameField, "Please fill out this field.", "empty");
                nicknameShow = true;
                valid = false;
            } else if (!Pattern.matches(nicknamePattern, nicknameField.getText().strip())) {
                nicknameTooltip = showTooltip(nicknameField, "Nickname must be 4—15 characters long\nand can only contain letters and numbers.", "invalid");
                nicknameShow = true;
                valid = false;
            }
        }

        return valid;
    }

    public Scene getScene() {
        return this.scene;
    }
}

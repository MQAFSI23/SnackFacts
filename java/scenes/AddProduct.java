package scenes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.NutritionFacts;
import models.Product;
import models.User;
import utils.MyPopup;
import utils.BarcodeScanner;
import dao.NutritionFactsDao;
import dao.ProductDao;
import dao.UserDao;

public class AddProduct {
    private User user;
    private Stage stage;
    private Scene scene;
    private BorderPane root;

    public AddProduct(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        init();
    }

    private void init() {
        root = new BorderPane();
        root.getStyleClass().add("homeBackground");
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());

        VBox addProductVBox = new VBox();
        addProductVBox.setPadding(new Insets(10));
        addProductVBox.setSpacing(10);
        
        HBox idBox = new HBox();
        idBox.setSpacing(10);

        JFXTextField idField = new JFXTextField();
        idField.setPromptText("Product ID (scan the product barcode)");
        idField.setPrefWidth(413);
        idField.getStyleClass().add("searchField");

        JFXButton scanButton = new JFXButton("Scan");
        scanButton.getStyleClass().add("categoryButton");
        scanButton.setPrefWidth(77);
        scanButton.setOnAction(e -> new BarcodeScanner().scanBarcode(idField));

        idBox.getChildren().addAll(idField, scanButton);
        idBox.setAlignment(Pos.CENTER);

        JFXTextField nameField = new JFXTextField();
        nameField.setPromptText("Product Name");
        nameField.getStyleClass().add("searchField");
        nameField.setMaxWidth(500);

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Category");
        categoryComboBox.setMaxWidth(500);
        categoryComboBox.setItems(FXCollections.observableArrayList("Food", "Beverage"));
        categoryComboBox.getStyleClass().add("categoryComboBox");

        JFXTextField servingSizeField = new JFXTextField();
        servingSizeField.setMaxWidth(500);
        servingSizeField.setPromptText("Serving Size");
        servingSizeField.getStyleClass().add("searchField");

        JFXTextField caloriesField = new JFXTextField();
        caloriesField.setMaxWidth(500);
        caloriesField.setPromptText("Calories");
        caloriesField.getStyleClass().add("searchField");

        JFXTextField fatField = new JFXTextField();
        fatField.setMaxWidth(500);
        fatField.setPromptText("Fat");
        fatField.getStyleClass().add("searchField");

        JFXTextField carbsField = new JFXTextField();
        carbsField.setMaxWidth(500);
        carbsField.setPromptText("Carbs");
        carbsField.getStyleClass().add("searchField");

        JFXTextField proteinField = new JFXTextField();
        proteinField.setMaxWidth(500);
        proteinField.setPromptText("Protein");
        proteinField.getStyleClass().add("searchField");

        JFXTextField fiberField = new JFXTextField();
        fiberField.setMaxWidth(500);
        fiberField.setPromptText("Fiber");
        fiberField.getStyleClass().add("searchField");

        JFXTextField sugarField = new JFXTextField();
        sugarField.setMaxWidth(500);
        sugarField.setPromptText("Sugar");
        sugarField.getStyleClass().add("searchField");

        JFXButton uploadImageButton = new JFXButton("Upload Image");
        Label imagePathLabel = new Label("No image selected");
        uploadImageButton.getStyleClass().add("categoryButton");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        VBox uploadVBox = new VBox();
        uploadVBox.getChildren().addAll(uploadImageButton, imagePathLabel, errorLabel);

        VBox.setMargin(imagePathLabel, new Insets(5, 0, 0, 0));
        VBox.setMargin(errorLabel, new Insets(5, 0, 0, 0));

        HBox uploadBox = new HBox();
        uploadBox.getChildren().add(uploadVBox);

        HBox.setMargin(uploadVBox, new Insets(0, 0, 0, 140));
        
        final byte[][] imageBytes = new byte[1][1];
        uploadImageButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                try {
                    long fileSizeInBytes = selectedFile.length();
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    long fileSizeInMB = fileSizeInKB / 1024;

                    if (fileSizeInMB > 2) {
                        errorLabel.setText("File size must not exceed 2MB");
                    } else {
                        imageBytes[0] = Files.readAllBytes(selectedFile.toPath());
                        imagePathLabel.setText(selectedFile.getName());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JFXButton saveButton = new JFXButton("Save");
        saveButton.getStyleClass().add("categoryButton");
        saveButton.setOnAction(e -> {
            String idText = idField.getText().trim();
            String name = nameField.getText().trim();
            String category = categoryComboBox.getValue();

            String servingSizeText = servingSizeField.getText().trim();
            String caloriesText = caloriesField.getText().trim();
            String fatText = fatField.getText().trim();
            String carbsText = carbsField.getText().trim();
            String proteinText = proteinField.getText().trim();
            String fiberText = fiberField.getText().trim();
            String sugarText = sugarField.getText().trim();
            String updater = new UserDao().getNicknameByUsername(user.getUsername());

            if (idText.isEmpty() || name.isEmpty() || category == null ||
                servingSizeText.isEmpty() || caloriesText.isEmpty() ||
                fatText.isEmpty() || carbsText.isEmpty() ||
                proteinText.isEmpty() || fiberText.isEmpty() || sugarText.isEmpty()) {
                errorLabel.setText("All fields must be filled out!");
                return;
            }

            if (idText.length() != 13 || !idText.startsWith("89")) {
                errorLabel.setText("Product ID must be 13 digits long and start with 89!");
                return;
            }

            try {
                long id = Long.parseLong(idField.getText());
                ProductDao productDao = new ProductDao();

                if (productDao.getProductById(id) != null){
                    errorLabel.setText("Product ID already exists!");
                    return;
                }
        
                if (productDao.getProductByName(name) != null){
                    errorLabel.setText("Product name already exists!");
                    return;
                }

                double servingSize = Double.parseDouble(servingSizeText);
                double calories = Double.parseDouble(caloriesText);
                double fat = Double.parseDouble(fatText);
                double carbs = Double.parseDouble(carbsText);
                double protein = Double.parseDouble(proteinText);
                double fiber = Double.parseDouble(fiberText);
                double sugar = Double.parseDouble(sugarText);

                HashMap<String, Double> nutrients = new HashMap<>();
                nutrients.put("calories", calories);
                nutrients.put("fat", fat);
                nutrients.put("protein", protein);
                nutrients.put("carbs", carbs);
                nutrients.put("fiber", fiber);
                nutrients.put("sugar", sugar);

                byte[] image = imageBytes[0].length > 1 ? imageBytes[0] : Files.readAllBytes(new File("src/main/resources/images/NoImage.png").toPath());

                Date updateDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                String updateDateString = dateFormat.format(updateDate);

                Product newProduct = new Product(id, name, category, image);
                NutritionFacts nutritionFacts = new NutritionFacts(id, name, category, nutrients, servingSize, updateDateString, updater);

                NutritionFactsDao nutritionFactsDao = new NutritionFactsDao();

                productDao.saveProduct(newProduct);
                nutritionFactsDao.addNutritionFacts(nutritionFacts);

                new MyPopup().showPopup("Product added successfully!", stage, false);

                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(event2 -> backToHome());
                delay.play();
            } catch (NumberFormatException nfe) {
                errorLabel.setText("Product ID, Serving Size, Calories, Fat, Carbs, Protein, Fiber, and Sugar must be numbers!");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        
        JFXButton backButton = new JFXButton("Back");
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction(e -> backToHome());
        
        HBox lowerBox = new HBox();
        lowerBox.setSpacing(10);
        lowerBox.getChildren().addAll(saveButton, backButton);

        VBox.setMargin(lowerBox, new Insets(10, 0, 0, 0));
        HBox.setMargin(saveButton, new Insets(0, 0, 0, 140));

        addProductVBox.getChildren().addAll(idBox, nameField, categoryComboBox, servingSizeField, caloriesField, fatField, proteinField, carbsField, fiberField, sugarField, uploadBox, lowerBox);
        addProductVBox.getStyleClass().add("dialogBackground");
        addProductVBox.setAlignment(Pos.CENTER);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(20);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ImageView logoView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Logo.png"))));
        logoView.setFitHeight(65);
        logoView.setFitWidth(65);
        HBox.setMargin(logoView, new Insets(0, 0, 0, 100));

        Label titleLabel = new Label("SnackFacts");
        titleLabel.getStyleClass().add("title");

        topBar.getChildren().addAll(logoView, titleLabel);
        topBar.getStyleClass().add("topBar");

        root.setTop(topBar);
        root.setCenter(addProductVBox);

        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("SnackFacts - Add Product");
        stage.setResizable(false);
        stage.show();
    }

    private void backToHome() {
        HomeScene homeScene = new HomeScene(stage, user, false);
        stage.setScene(homeScene.getScene());
    }

    public Scene getScene() {
        return scene;
    }
}
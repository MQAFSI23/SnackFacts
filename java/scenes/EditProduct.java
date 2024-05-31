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
import models.AbstractUser;
import models.NutritionFacts;
import models.Product;
import utils.MyPopup;
import dao.NutritionFactsDao;
import dao.ProductDao;
import dao.UserDao;

public class EditProduct {
    private AbstractUser abstractUser;
    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private Product product;
    private NutritionFacts nutritionFacts;

    public EditProduct(Stage stage, AbstractUser abstractUser, Product product) {
        this.stage = stage;
        this.abstractUser = abstractUser;
        this.product = product;
        this.nutritionFacts = new NutritionFactsDao().getNutritionFactsById(product.getProductId());
        init();
    }

    private void init() {
        root = new BorderPane();
        root.getStyleClass().add("homeBackground");
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());

        VBox editProductVBox = new VBox();
        editProductVBox.setPadding(new Insets(10));
        editProductVBox.setSpacing(10);

        JFXTextField idField = new JFXTextField(String.valueOf(product.getProductId()));
        idField.setPromptText("Product ID");
        idField.setMaxWidth(500);
        idField.getStyleClass().add("searchField");
        if (abstractUser.getUsername().equals("@dm1N")) idField.setDisable(false);
        else idField.setDisable(true);;

        JFXTextField nameField = new JFXTextField(product.getProductName());
        nameField.setPromptText("Product Name");
        nameField.setMaxWidth(500);
        nameField.getStyleClass().add("searchField");
        if (abstractUser.getUsername().equals("@dm1N")) nameField.setDisable(false);
        else nameField.setDisable(true);

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Category");
        categoryComboBox.setMaxWidth(500);
        categoryComboBox.setItems(FXCollections.observableArrayList("Food", "Beverage"));
        categoryComboBox.setValue(product.getCategory());
        categoryComboBox.getStyleClass().add("categoryComboBox");
        if (abstractUser.getUsername().equals("@dm1N")) categoryComboBox.setDisable(false);
        else categoryComboBox.setDisable(true);

        JFXTextField servingSizeField = new JFXTextField(String.valueOf(nutritionFacts.getServingSize()));
        servingSizeField.setMaxWidth(500);
        servingSizeField.setPromptText("Serving Size (g or ml)");
        servingSizeField.getStyleClass().add("searchField");

        JFXTextField caloriesField = new JFXTextField(String.valueOf(nutritionFacts.getNutrientValue("calories")));
        caloriesField.setMaxWidth(500);
        caloriesField.setPromptText("Calories");
        caloriesField.getStyleClass().add("searchField");

        JFXTextField fatField = new JFXTextField(String.valueOf(nutritionFacts.getNutrientValue("fat")));
        fatField.setMaxWidth(500);
        fatField.setPromptText("Fat");
        fatField.getStyleClass().add("searchField");

        JFXTextField carbsField = new JFXTextField(String.valueOf(nutritionFacts.getNutrientValue("carbs")));
        carbsField.setMaxWidth(500);
        carbsField.setPromptText("Carbs");
        carbsField.getStyleClass().add("searchField");

        JFXTextField proteinField = new JFXTextField(String.valueOf(nutritionFacts.getNutrientValue("protein")));
        proteinField.setMaxWidth(500);
        proteinField.setPromptText("Protein");
        proteinField.getStyleClass().add("searchField");

        JFXTextField fiberField = new JFXTextField(String.valueOf(nutritionFacts.getNutrientValue("fiber")));
        fiberField.setMaxWidth(500);
        fiberField.setPromptText("Fiber");
        fiberField.getStyleClass().add("searchField");

        JFXTextField sugarField = new JFXTextField(String.valueOf(nutritionFacts.getNutrientValue("sugar")));
        sugarField.setMaxWidth(500);
        sugarField.setPromptText("Sugar");
        sugarField.getStyleClass().add("searchField");

        Label imagePathLabel = new Label("No image selected");
        JFXButton uploadImageButton = new JFXButton("Upload Image");
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
        imageBytes[0] = product.getImage();
        uploadImageButton.setOnAction(e -> {
            if (!abstractUser.getUsername().equals("@dm1N")) {
                errorLabel.setText("Only admin can upload image!");
                return;
            }

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
            String updater = new UserDao().getNicknameByUsername(abstractUser.getUsername());

            boolean noChanges = idText.equals(String.valueOf(product.getProductId()).trim())
                    && name.equals(product.getProductName().trim())
                    && category.equals(product.getCategory().trim())
                    && servingSizeText.equals(String.valueOf(nutritionFacts.getServingSize()).trim())
                    && caloriesText.equals(String.valueOf(nutritionFacts.getNutrientValue("calories")).trim())
                    && fatText.equals(String.valueOf(nutritionFacts.getNutrientValue("fat")).trim())
                    && carbsText.equals(String.valueOf(nutritionFacts.getNutrientValue("carbs")).trim())
                    && proteinText.equals(String.valueOf(nutritionFacts.getNutrientValue("protein")).trim())
                    && fiberText.equals(String.valueOf(nutritionFacts.getNutrientValue("fiber")).trim())
                    && sugarText.equals(String.valueOf(nutritionFacts.getNutrientValue("sugar")).trim())
                    && (imageBytes[0] == product.getImage());

            if (noChanges) {
                new MyPopup().showPopup("No changes detected", stage, true);

                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(event2 -> backToShowDetailProduct());
                delay.play();
                return;
            }

            if (name.isEmpty() || category == null ||
                servingSizeText.isEmpty() || caloriesText.isEmpty() ||
                fatText.isEmpty() || carbsText.isEmpty() ||
                proteinText.isEmpty() || fiberText.isEmpty() || sugarText.isEmpty()) {
                errorLabel.setText("All fields must be filled out!");
                return;
            }

            if (idText.length() != 13) {
                errorLabel.setText("Product ID must be 13 digits long!");
                return;
            }

            if (name.length() > 35) {
                errorLabel.setText("Product name length limit is 35 characters");
                return;
            }

            if (!name.equals(product.getProductName().trim()) && new ProductDao().getProductByName(name) != null) {
                errorLabel.setText("Product name already exists!");
                return;
            }

            try {
                double servingSize = Double.parseDouble(servingSizeText);
                if (servingSize < 1) {
                    errorLabel.setText("Serving size is at least 1g or 1ml!");
                    return;
                }
                double calories = Double.parseDouble(caloriesText);
                double fat = Double.parseDouble(fatText);
                double carbs = Double.parseDouble(carbsText);
                double protein = Double.parseDouble(proteinText);
                double fiber = Double.parseDouble(fiberText);
                double sugar = Double.parseDouble(sugarText);

                HashMap<String, Double> nutrients = new HashMap<>();
                nutrients.put("calories", calories);
                nutrients.put("fat", fat);
                nutrients.put("carbs", carbs);
                nutrients.put("protein", protein);
                nutrients.put("fiber", fiber);
                nutrients.put("sugar", sugar);
                
                byte[] image = imageBytes[0];

                Date updateDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                String updateDateString = dateFormat.format(updateDate);

                Product updatedProduct = new Product(product.getProductId(), name, category, image);
                NutritionFacts updatedNutritionFacts = new NutritionFacts(product.getProductId(), name, category, nutrients, servingSize, updateDateString, updater);

                ProductDao productDao = new ProductDao();
                NutritionFactsDao nutritionFactsDao = new NutritionFactsDao();

                productDao.updateProduct(updatedProduct);
                nutritionFactsDao.updateNutritionFacts(updatedNutritionFacts);

                new MyPopup().showPopup("Product updated successfully", stage, false);

                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(event2 -> backToShowDetailProduct());
                delay.play();
            } catch (NumberFormatException nfe) {
                errorLabel.setText("Serving Size, Calories, Fat, Carbs, Protein, Fiber, and Sugar must be numbers!");
            }
        });

        JFXButton backButton = new JFXButton("Back");
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction(e -> backToShowDetailProduct());

        HBox lowerBox = new HBox();
        lowerBox.setSpacing(10);
        lowerBox.getChildren().addAll(saveButton, backButton);

        VBox.setMargin(lowerBox, new Insets(10, 0, 0, 0));
        HBox.setMargin(saveButton, new Insets(0, 0, 0, 140));

        editProductVBox.getChildren().addAll(idField, nameField, categoryComboBox, servingSizeField, caloriesField, fatField, proteinField, carbsField, fiberField, sugarField, uploadBox, lowerBox);
        editProductVBox.getStyleClass().add("editdialogBackgroundProductVBox");
        editProductVBox.setAlignment(Pos.CENTER);

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
        root.setCenter(editProductVBox);

        scene = new Scene(root, 800, 600);
        stage.setTitle("SnackFacts - Edit Product");
    }

    private void backToShowDetailProduct() {
        stage.setScene(new ShowDetailProduct(stage, abstractUser, product).getScene());
    }

    public Scene getScene() {
        return scene;
    }
}

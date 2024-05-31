package scenes;

import java.text.DecimalFormat;
import java.util.Objects;
import com.jfoenix.controls.JFXButton;
import java.io.ByteArrayInputStream;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import dao.NutritionFactsDao;
import dao.ProductDao;
import utils.MyPopup;
import models.Product;
import models.AbstractUser;
import models.NutritionFacts;

public class ShowDetailProduct {
    private BorderPane root;
    private AbstractUser abstractUser;
    private Stage stage;
    private Scene scene;
    private Product product;

    public ShowDetailProduct(Stage stage, AbstractUser abstractUser, Product product) {
        this.stage = stage;
        this.abstractUser = abstractUser;
        this.product = product;
        init();
    }

    private void init() {
        ProductDao productDao = new ProductDao();
        this.product = productDao.getProductById(this.product.getProductId());

        root = new BorderPane();
        root.getStyleClass().add("homeBackground");
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());
    
        NutritionFactsDao nutritionFactsDao = new NutritionFactsDao();
        NutritionFacts nutritionFacts = nutritionFactsDao.getNutritionFactsById(product.getProductId());
    
        Label nameLabel = new Label("Name            : " + product.getProductName());
        nameLabel.getStyleClass().add("nutriLabel");
    
        Label categoryLabel = new Label("Category      : " + product.getCategory());
        categoryLabel.getStyleClass().add("nutriLabel");
    
        Label idLabel = new Label("ID                  : " + product.getProductId());
        idLabel.getStyleClass().add("nutriLabel");

        Label updateLabel = new Label("Update Date : " + nutritionFacts.getUpdateDate());
        updateLabel.getStyleClass().add("nutriLabel");

        Label updaterLabel = new Label("(by " + nutritionFacts.getUpdater() + ")");
        updaterLabel.getStyleClass().add("updaterLabel");

        HBox updaterBox = new HBox();
        updaterBox.getChildren().addAll(updateLabel, updaterLabel);
    
        Image image = new Image(new ByteArrayInputStream(product.getImage()));
        ImageView productImageView = new ImageView();
        productImageView.setImage(image);
        productImageView.setFitWidth(120);
        productImageView.setFitHeight(120);
    
        VBox servingVBox = new VBox();
        servingVBox.setPadding(new Insets(10));
        servingVBox.setSpacing(5);
        servingVBox.getStyleClass().add("servingVBox");
    
        VBox per100VBox = new VBox();
        per100VBox.setPadding(new Insets(10));
        per100VBox.setSpacing(5);
        per100VBox.getStyleClass().add("servingVBox");

        DecimalFormat df = new DecimalFormat("#.00");
        if (nutritionFacts != null) {
            Label servingSizeLabel = new Label("Nutrition Facts per " + df.format(nutritionFacts.getServingSize()) + (nutritionFacts.getCategory().equalsIgnoreCase("food") ? "g" : "ml"));
            servingSizeLabel.getStyleClass().add("nutriLabel");
    
            Label caloriesLabel = new Label("Calories               " + df.format(nutritionFacts.getNutrientValue("calories")) + "kcal");
            caloriesLabel.getStyleClass().add("detailLabel");
    
            Label fatLabel = new Label("Fat                        " + df.format(nutritionFacts.getNutrientValue("fat")) + "g");
            fatLabel.getStyleClass().add("detailLabel");

            Label proteinLabel = new Label("Protein                 " + df.format(nutritionFacts.getNutrientValue("protein")) + "g");
            proteinLabel.getStyleClass().add("detailLabel");
    
            Label carbsLabel = new Label("Carbs                   " + df.format(nutritionFacts.getNutrientValue("carbs")) + "g");
            carbsLabel.getStyleClass().add("detailLabel");
    
            Label fiberLabel = new Label("Fiber                     " + df.format(nutritionFacts.getNutrientValue("fiber")) + "g");
            fiberLabel.getStyleClass().add("detailLabel");
    
            Label sugarLabel = new Label("Sugar                   " + df.format(nutritionFacts.getNutrientValue("sugar")) + "g");
            sugarLabel.getStyleClass().add("detailLabel");
    
            servingVBox.getChildren().addAll(servingSizeLabel, caloriesLabel, fatLabel, proteinLabel, carbsLabel, fiberLabel, sugarLabel);
    
            double servingSize = nutritionFacts.getServingSize();
            double factor = 100.0 / servingSize;
            double caloriesPer100 = nutritionFacts.getNutrientValue("calories") * factor;
            double fatPer100 = nutritionFacts.getNutrientValue("fat") * factor;
            double carbsPer100 = nutritionFacts.getNutrientValue("carbs") * factor;
            double proteinPer100 = nutritionFacts.getNutrientValue("protein") * factor;
            double fiberPer100 = nutritionFacts.getNutrientValue("fiber") * factor;
            double sugarPer100 = nutritionFacts.getNutrientValue("sugar") * factor;
    
            Label per100Label = new Label("Nutrition Facts per 100.00" + (nutritionFacts.getCategory().equalsIgnoreCase("food") ? "g" : "ml"));
            per100Label.getStyleClass().add("nutriLabel");
    
            Label per100CaloriesLabel = new Label("Calories               " + df.format(caloriesPer100) + "kcal");
            per100CaloriesLabel.getStyleClass().add("detailLabel");
    
            Label per100FatLabel = new Label("Fat                        " + df.format(fatPer100) + "g");
            per100FatLabel.getStyleClass().add("detailLabel");

            Label per100ProteinLabel = new Label("Protein                 " + df.format(proteinPer100) + "g");
            per100ProteinLabel.getStyleClass().add("detailLabel");
    
            Label per100CarbsLabel = new Label("Carbs                   " + df.format(carbsPer100) + "g");
            per100CarbsLabel.getStyleClass().add("detailLabel");
    
            Label per100FiberLabel = new Label("Fiber                     " + df.format(fiberPer100) + "g");
            per100FiberLabel.getStyleClass().add("detailLabel");
    
            Label per100SugarLabel = new Label("Sugar                    " + df.format(sugarPer100) + "g");
            per100SugarLabel.getStyleClass().add("detailLabel");
    
            per100VBox.getChildren().addAll(per100Label, per100CaloriesLabel, per100FatLabel, per100ProteinLabel, per100CarbsLabel, per100FiberLabel, per100SugarLabel);
        }
        
        servingVBox.getStyleClass().add("servingsVBox");
        per100VBox.getStyleClass().add("servingsVBox");

        JFXButton backButton = new JFXButton("Back");
        backButton.setMinHeight(40);
        backButton.getStyleClass().add("backButton");
        backButton.setOnAction(e -> backToHome());

        JFXButton editButton = new JFXButton("Edit");
        editButton.setMinHeight(40);
        editButton.getStyleClass().add("editButton");
        editButton.setOnAction(e -> {
            if (!abstractUser.getIsGuest()) editProduct();
            else {
                new MyPopup().showPopup("Guest cannot edit a product", stage, true);
            }
        });

        JFXButton removeButton = new JFXButton("Remove");
        removeButton.setMinHeight(40);
        removeButton.getStyleClass().add("removeButton");
        removeButton.setOnAction(e -> {
            if (abstractUser.getUsername().equals("@dm1N")) removeProduct();
            else {
                new MyPopup().showPopup("Only admin can remove a product", stage, true);
            }
        });

        HBox lowerHBox = new HBox();
        lowerHBox.setSpacing(10);
        lowerHBox.getChildren().addAll(editButton, removeButton, backButton);
        lowerHBox.setAlignment(Pos.CENTER);

        VBox labelBox = new VBox();
        labelBox.setSpacing(10);
        labelBox.getChildren().addAll(nameLabel, categoryLabel, idLabel, updaterBox);

        HBox upperBox = new HBox();
        upperBox.setSpacing(20);
        upperBox.getChildren().addAll(productImageView, labelBox);
        upperBox.setAlignment(Pos.CENTER);

        HBox.setMargin(productImageView, new Insets(0, 0, 0, 250));
        HBox.setHgrow(labelBox, Priority.ALWAYS);
    
        HBox detailBox = new HBox();
        detailBox.setSpacing(20);
        detailBox.getChildren().addAll(servingVBox, per100VBox);
        detailBox.setAlignment(Pos.CENTER);

        HBox.setMargin(backButton, new Insets(0, 0, 0, 233));

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

        VBox detailVBox = new VBox();
        detailVBox.getChildren().addAll(upperBox, detailBox, lowerHBox);
        detailVBox.getStyleClass().add("dialogBackground");

        VBox.setMargin(upperBox, new Insets(30, 0, 0, 0));
        VBox.setMargin(detailBox, new Insets(30, 0, 0, 0));
        VBox.setMargin(lowerHBox, new Insets(30, 0, 0, 0));

        root.setTop(topBar);
        root.setCenter(detailVBox);

        scene = new Scene(root, 800, 600);
        stage.setTitle("SnackFacts - Product Details");
    }

    private void backToHome() {
        HomeScene home = new HomeScene(stage, abstractUser, abstractUser.getIsGuest());
        stage.setScene(home.getScene());
    }

    private void editProduct() {
        EditProduct editProduct = new EditProduct(stage, abstractUser, product);
        stage.setScene(editProduct.getScene());
    }

    private void removeProduct() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);

        VBox dialogVBox = new VBox();
        dialogVBox.setSpacing(20);
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setPadding(new Insets(20));

        Label confirmationLabel = new Label("Are you sure you want to remove this product?");
        confirmationLabel.getStyleClass().add("nutriLabel");
        confirmationLabel.setWrapText(true);

        Button yesButton = new Button("Yes");
        yesButton.getStyleClass().add("yesButton");
        yesButton.setOnAction(event -> {
            ProductDao productDao = new ProductDao();

            productDao.removeProduct(product.getProductId());

            dialog.close();
            new MyPopup().showPopup("Product removed successfully", stage, false);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event2 -> backToHome());
            delay.play();
        });

        Button noButton = new Button("No");
        noButton.getStyleClass().add("noButton");
        noButton.setOnAction(event -> dialog.close());

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(yesButton, noButton);

        dialogVBox.getChildren().addAll(confirmationLabel, buttonBox);
        dialogVBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());

        Scene dialogScene = new Scene(dialogVBox, 300, 150);
        dialog.setScene(dialogScene);
        dialog.setTitle("SnackFacts - Remove Product");
        dialog.show();
    }

    public Scene getScene() {
        return scene;
    }
}

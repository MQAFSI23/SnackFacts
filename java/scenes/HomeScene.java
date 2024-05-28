package scenes;

import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import dao.ProductDao;

import models.Guest;
import models.Product;
import models.User;
import utils.MyPopup;
import utils.BarcodeScanner;

public class HomeScene {
    private User user;
    private Scene scene;
    private Stage stage;
    private BorderPane root;
    private Stage loginStage;
    private ListView<Product> productListView;
    private ObservableList<Product> productList;

    public HomeScene(Stage stage, User user, boolean isGuest) {
        if (isGuest) this.user = new Guest("Guest", "Guest");
        else this.user = user;
        this.stage = stage;

        init();
    }

    private void init() {
        root = new BorderPane();
        root.getStyleClass().add("homeBackground");
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());

        JFXButton addProductButton = new JFXButton("+ New Product");
        addProductButton.getStyleClass().add("leftPanelButton");
        addProductButton.setMinHeight(40);
        addProductButton.setOnAction(e -> {
            if (!user.getIsGuest()) addProduct();
            else {
                new MyPopup().showPopup("Guest cannot add a product", stage, true);
            }
        });

        JFXButton scanButton = new JFXButton("🤳 Search by Barcode");
        scanButton.setMinHeight(40);
        scanButton.getStyleClass().add("leftPanelButton");

        JFXButton logoutButton = new JFXButton("🏃Logout");
        logoutButton.setMinHeight(40);
        logoutButton.getStyleClass().add("logoutButton");
        logoutButton.setOnAction(e -> {
            new MyPopup().showPopup("Logged out successfully", stage, false);
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> {
                loginStage = new Stage();
                LoginScene loginScene = new LoginScene(loginStage);
                loginStage.setScene(loginScene.getScene());
                stage.close();
                loginStage.show();
            });
            delay.play();
        });

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(addProductButton, scanButton);
        VBox.setMargin(buttonBox, new Insets(30, 0, 0, 0));

        VBox centerPanel = new VBox();
        centerPanel.setSpacing(10);
        centerPanel.setPadding(new Insets(10));
        centerPanel.setAlignment(Pos.TOP_CENTER);

        HBox searchBox = new HBox();
        searchBox.setSpacing(10);
        searchBox.setAlignment(Pos.CENTER);

        JFXTextField searchField = new JFXTextField();
        searchField.setPrefWidth(420);
        searchField.setPromptText("Search");
        searchField.getStyleClass().add("searchField");
        VBox.setMargin(searchBox, new Insets(20, 0, 0, 0));

        scanButton.setOnAction(e -> new BarcodeScanner().scanBarcode(searchField));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    filterList(productListView, productList, newValue);
                } catch (NumberFormatException ignored) {
                    // If scanned value is not a number, do nothing
                }
            }
        });

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Filter by category");
        categoryComboBox.setItems(FXCollections.observableArrayList("All", "Food", "Beverage"));
        categoryComboBox.getStyleClass().add("categoryComboBox");
        categoryComboBox.setOnAction(e -> filterCategory(categoryComboBox.getValue()));

        searchBox.getChildren().addAll(searchField, categoryComboBox);

        productListView = new ListView<>();
        productList = FXCollections.observableArrayList();
        loadProductsFromDatabase();
        productListView.setItems(productList);
        productListView.getStyleClass().add("productListView");

        productListView.setCellFactory(param -> new ListCell<>() {
            private ImageView imageView = new ImageView();
            
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    TextFlow textFlow = new TextFlow();
                    Text productName = new Text("\n" + product.getProductName() + "\n");
                    productName.setStyle("-fx-font-weight: bold;");
                    
                    Text productInfo = new Text(product.getProductId() + " | " + product.getCategory() + "\n");
                    productInfo.setStyle("-fx-fill: gray;");

                    textFlow.getChildren().addAll(productName, productInfo);
                    setGraphic(textFlow);
                    
                    if (product.getImage() != null) {
                        Image image = new Image(new ByteArrayInputStream(product.getImage()));
                        imageView.setImage(image);
                        // imageView.setPreserveRatio(true);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        
                        HBox hbox = new HBox(imageView, textFlow);
                        hbox.setSpacing(10);
                        hbox.setPadding(new Insets(5));

                        VBox.setMargin(imageView, new Insets(20, 0, 0, 0));
                        setGraphic(hbox);
                    } else {
                        setGraphic(textFlow);
                    }
                }
            }
        });        

        productListView.setOnMouseClicked(event -> {
            Product selectedProduct = productListView.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                showProductDetails(selectedProduct);
            }
        });

        productListView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Node node = event.getPickResult().getIntersectedNode();
                while (node != null && node != productListView && !(node instanceof ListCell)) {
                    node = node.getParent();
                }
                if (node instanceof ListCell == false) productListView.getSelectionModel().clearSelection();
            }
        });

        productListView.setMaxWidth(600);

        centerPanel.getChildren().addAll(buttonBox, searchBox, productListView);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(20);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ImageView logoView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Logo.png"))));
        logoView.setFitHeight(65);
        logoView.setFitWidth(65);

        Label titleLabel = new Label("SnackFacts");
        titleLabel.getStyleClass().add("title");

        HBox.setMargin(logoutButton, new Insets(0, 0, 0, 253));
        HBox.setMargin(logoView, new Insets(0, 0, 0, 100));

        topBar.getChildren().addAll(logoView, titleLabel, logoutButton);
        topBar.getStyleClass().add("topBar");

        root.setTop(topBar);
        root.setCenter(centerPanel);

        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("SnackFacts - Home");
        stage.setResizable(false);
        stage.show();
    }

    private void loadProductsFromDatabase() {
        ProductDao productDao = new ProductDao();
        productList.clear();
        productList.addAll(productDao.getAllProducts());
    }

    private void filterList(ListView<Product> listView, ObservableList<Product> originalList, String filter) {
        if (filter.isEmpty()) {
            listView.setItems(originalList);
        } else {
            ObservableList<Product> filteredList = FXCollections.observableArrayList();
            for (Product product : originalList) {
                if (product.getProductName().toLowerCase().contains(filter.toLowerCase())) {
                    filteredList.add(product);
                } else if (("" + product.getProductId()).toLowerCase().contains(filter.toLowerCase())) {
                    filteredList.add(product);
                }
            }
            listView.setItems(filteredList);
        }
    }

    private void filterCategory(String category) {
        if (category.equalsIgnoreCase("All")) {
            productListView.setItems(productList);
        } else {
            ObservableList<Product> filteredList = productList.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
            productListView.setItems(filteredList);
        }
    }

    private void addProduct() {
        stage.setScene(new AddProduct(stage, user).getScene());
    }

    private void showProductDetails(Product product) {
        stage.setScene(new ShowDetailProduct(stage, user, product).getScene());
    }

    public Scene getScene() {
        return scene;
    }
}
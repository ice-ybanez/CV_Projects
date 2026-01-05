package store.view;

import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import store.controller.ProductController;
import store.model.Product;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import store.utilities.StoreData;
import store.utilities.StoreSerializer;

public class ProductView {

    private final ProductController productController;

    // components
    private TextField nameTextField;
    private TextField priceTextField;
    private TextField stockTextField;
    private TableView<Product> productTable;
    private Button addButton;
    private Button updateButton;
    private Button removeButton;
    private Button saveButton;
    private Button loadButton;
    private Button exitButton;

    private ListView<Product> productListView;

    public ProductView(ProductController productController) {
        this.productController = productController;
        initializeComponents();
    }

    // Initialize components and layout
    private void initializeComponents() {
        nameTextField = new TextField();
        nameTextField.setPromptText("Enter Product Name");

        priceTextField = new TextField();
        priceTextField.setPromptText("Enter Product Price");

        stockTextField = new TextField();
        stockTextField.setPromptText("Enter Stock Quantity");

        addButton = new Button("Add Product");
        addButton.setOnAction(e -> handleAddProduct());

        updateButton = new Button("Update Product");
        updateButton.setOnAction(e -> handleUpdateProduct());

        removeButton = new Button("Remove Product");
        removeButton.setOnAction(e -> handleRemoveProduct());

//        saveButton = new Button("Save Products");
//        saveButton.setOnAction(e -> handleSaveProducts());
        saveButton = new Button("Save Products");
        saveButton.setOnAction(e -> {
            StoreData data = new StoreData(null, productController.getProductList(), null);
            StoreSerializer.saveAll(data);
            showInfo("Products saved successfully!!!");
        });

//        loadButton = new Button("Load Products");
//        loadButton.setOnAction(e -> handleLoadProducts());
        loadButton = new Button("Load Products");
        loadButton.setOnAction(e -> {
            StoreData data = StoreSerializer.loadAll();
            if (data != null && data.getProducts() != null) {
                productController.getProductList().clear();
                productController.getProductList().addAll(data.getProducts());
                productListView.getItems().setAll(productController.getProductList());
                showInfo("Products loaded successfully!!!");
            }
        });

        exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));

        // ListView for displaying products
        productListView = new ListView<>();
        productListView.getItems().addAll(productController.getProductList());

        productTable = new TableView<>();
        productTable.setEditable(true);

        // name column
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(event -> event.getRowValue().setName(event.getNewValue()));

        // price column
        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        priceCol.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
        priceCol.setOnEditCommit(event -> event.getRowValue().setPrice(event.getNewValue()));

        // stock column
        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getStockQuantity()).asObject());
        stockCol.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.IntegerStringConverter()));
        stockCol.setOnEditCommit(event -> event.getRowValue().setStockQuantity(event.getNewValue()));

        // add columns to table
        productTable.getColumns().addAll(nameCol, priceCol, stockCol);
        productTable.getItems().addAll(productController.getProductList()); // Assuming you pass this in


        // layout in VBox
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(
                new Label("Product Management"),
                new Label("Product Name:"), nameTextField,
                new Label("Price:"), priceTextField,
                new Label("Stock:"), stockTextField,
                addButton,
                updateButton,
                removeButton,
                new Label("Products List:"),
                productListView,
                productTable,
                saveButton,
                loadButton,
                exitButton
        );

        vbox.setSpacing(15);
    }

    // handle adding a product
    private void handleAddProduct() {
        String name = nameTextField.getText();
        String priceText = priceTextField.getText();
        String stockText = stockTextField.getText();

        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);
            if (price <= 0 && stock <= 0) {
                showError("Price and Stock must be greater than 0.");
                return;
            }

            productController.addProduct(name, price, stock);
            productListView.getItems().setAll(productController.getProductList());  // update list view
            productTable.getItems().setAll(productController.getProductList()); // update table view
            nameTextField.clear();
            priceTextField.clear();
            stockTextField.clear();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for price.");
        }
    }

    // hadnle updating a product
    private void handleUpdateProduct() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showError("Please select a product to update.");
            return;
        }

        String name = nameTextField.getText();
        String priceText = priceTextField.getText();
        String stockText = stockTextField.getText();

        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);
            if (price <= 0 && stock <= 0) {
                showError("Price and Stock must be greater than 0.");
                return;
            }

            productController.updateProduct(selectedProduct, name, price, stock);
            productListView.getItems().setAll(productController.getProductList());  // Update the list view

            nameTextField.clear();
            priceTextField.clear();
            stockTextField.clear();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for price.");
        }
    }

    // handle removing a product
    private void handleRemoveProduct() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showError("Please select a product to remove.");
            return;
        }

        productController.removeProduct(selectedProduct);
        productListView.getItems().setAll(productController.getProductList());  // update the list view
    }

    // show error message
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.show();
    }

    // get the Product UI component to be added to tab
    public VBox getProductTab() {
        return (VBox) addButton.getParent();
    }

    private void handleSaveProducts() {
        productController.saveProducts();
        showInfo("Products saved successfully!!!");
    }

    private void handleLoadProducts(){
        productController.loadProducts();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.show();
    }
}

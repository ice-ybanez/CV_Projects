package store.view;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import store.controller.SalesController;
import store.model.Customer;
import store.model.Product;
import store.model.Sale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import store.utilities.StoreData;
import store.utilities.StoreSerializer;

import java.time.LocalDate;

public class SalesView {

    private final SalesController salesController;

    // components
    private ComboBox<Customer> customerComboBox;
    private ComboBox<Product> productComboBox;
    private TableView<Sale> salesTable;
    private ComboBox<String> sortComboBox;
    private ObservableList<Sale> salesObservableList;
    private TextField quantityTextField;
    private TextArea insightsTextArea;
    private Button showProductStatsButton;
    private Button showCustomerStatsButton;
    private Button saveButton;
    private Button loadButton;
    private Button addButton;
    private Button exitButton;
    private Label totalLabel;
    private Label stockLabel;

    public SalesView(SalesController salesController) {
        this.salesController = salesController;
        initializeComponents();
        updateComboBoxes();
    }

    // initialize the components and layout
    private void initializeComponents() {
        customerComboBox = new ComboBox<>();
        customerComboBox.getItems().addAll(salesController.getCustomerList());
        customerComboBox.setPromptText("Select Customer");

        productComboBox = new ComboBox<>();
        productComboBox.getItems().addAll(salesController.getProductList());
        productComboBox.setPromptText("Select Product");

        stockLabel = new Label("Stock: ");
        productComboBox.setOnAction(e -> updateStockLabel());

        quantityTextField = new TextField();
        quantityTextField.setPromptText("Enter Quantity");

        addButton = new Button("Add Sale");
        addButton.setOnAction(e -> handleAddSale());

        saveButton = new Button("Save All");
        saveButton.setOnAction(e -> {
            StoreData data = new StoreData(
                    salesController.getCustomerList(),
                    salesController.getProductList(),
                    salesController.getSalesList()
            );
            StoreSerializer.saveAll(data);
            showInfo("Sales data saved successfully.");
        });

        loadButton = new Button("Load All");
        loadButton.setOnAction(e -> {
            StoreData data = StoreSerializer.loadAll();
            if (data != null) {
                salesController.getCustomerList().clear();
                salesController.getCustomerList().addAll(data.getCustomers());

                salesController.getProductList().clear();
                salesController.getProductList().addAll(data.getProducts());

                salesController.getSalesList().clear();
                salesController.getSalesList().addAll(data.getSales());

                updateComboBoxes(); // Refresh combo boxes
                showInfo("Sales data loaded successfully.");
            }
        });

        exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));

        // sorting ComboBox
        sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("Sort by Date", "Sort by Product Name");
        sortComboBox.setValue("Sort by Purchase Date"); // default sorting method
        sortComboBox.setOnAction(e -> sortSalesTable());

        // TableView for displaying sales
        salesTable = new TableView<>();
        salesObservableList = FXCollections.observableArrayList(salesController.getSalesList());
        salesTable.setItems(salesObservableList);

        TableColumn<Sale, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCustomer().getName()));

        TableColumn<Sale, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProduct().getName()));

        TableColumn<Sale, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Sale, Double> totalCol = new TableColumn<>("Total (€)");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        TableColumn<Sale, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPurchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        salesTable.getColumns().addAll(customerCol, productCol, quantityCol, totalCol, dateCol);
        salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        totalLabel = new Label("Total: €0.00");

        showProductStatsButton = new Button("Product Stats (Selected)");
        showProductStatsButton.setOnAction(e -> showProductStats());

        showCustomerStatsButton = new Button("Customer Stats (Selected)");
        showCustomerStatsButton.setOnAction(e -> showCustomerStats());

        insightsTextArea = new TextArea();
        insightsTextArea.setEditable(false);
        insightsTextArea.setPrefRowCount(5);

        // layout in VBox
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(
                new Text("Sales Form"),
                new Label("Customer:"), customerComboBox,
                new Label("Product:"), productComboBox, stockLabel,
                new Label("Quantity:"), quantityTextField,
                new Label("Sort by:"), sortComboBox,
                new Label("Sales History:"), salesTable,
                addButton,
                totalLabel,
                saveButton,
                loadButton,
                new Separator(),
                new Label("Analytics:"),
                showProductStatsButton,
                showCustomerStatsButton,
                insightsTextArea,
                exitButton
        );

        vbox.setSpacing(15);
    }

    // load data into ComboBoxes
    private void updateComboBoxes() {
        customerComboBox.getItems().setAll(salesController.getCustomerList());
        productComboBox.getItems().setAll(salesController.getProductList());
    }

    // handle adding a sale
    private void handleAddSale() {
        Customer selectedCustomer = customerComboBox.getValue();
        Product selectedProduct = productComboBox.getValue();

        if (selectedCustomer == null || selectedProduct == null || quantityTextField.getText().isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityTextField.getText());
            if (quantity <= 0) {
                showError("Quantity must be greater than 0.");
                return;
            }

            // add the sale
            salesController.addSale(selectedCustomer, selectedProduct, quantity);

            // update the total amount
            updateTotalLabel();
            quantityTextField.clear();
            updateStockLabel();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for quantity.");
        }
        salesObservableList.setAll(salesController.getSalesList());
        sortSalesTable(); // keep the sort consistent

    }

    // update the total label
    private void updateTotalLabel() {
        double totalAmount = salesController.getTotalSalesAmount();
        totalLabel.setText("Total: €" + String.format("%.2f", totalAmount));
    }

    // update the sorted sales
    private void sortSalesTable() {
        String selected = sortComboBox.getValue();
        if (selected.equals("Sort by Product Name")) {
            salesTable.getItems().sort((s1, s2) ->
                    s1.getProduct().getName().compareToIgnoreCase(s2.getProduct().getName()));
        } else if (selected.equals("Sort by Purchase Date")) {
            salesTable.getItems().sort((s1, s2) ->
                    s1.getPurchaseDate().compareTo(s2.getPurchaseDate()));
        }
    }

    private void updateStockLabel() {
        Product selectedProduct = productComboBox.getValue();
        if (selectedProduct != null) {
            stockLabel.setText("Stock: " + selectedProduct.getStockQuantity());
        } else {
            stockLabel.setText("Stock: -");
        }
    }


    private void showProductStats() {
        Product selectedProduct = productComboBox.getValue();
        if (selectedProduct == null) {
            insightsTextArea.setText("Please select a product first.");
            return;
        }

        int soldLastMonth = 0;
        for (Sale sale : salesController.getSalesList()) {
            if (sale.getProduct().equals(selectedProduct) && isFromLastMonth(sale)) {
                soldLastMonth += sale.getQuantity();
            }
        }

        int inStock = selectedProduct.getStockQuantity(); // assuming stock quantity is stored

        insightsTextArea.setText(
                "Product: " + selectedProduct.getName() + "\n" +
                        "In Stock: " + inStock + "\n" +
                        "Sold Last Month: " + soldLastMonth
        );
    }

    private void showCustomerStats() {
        Customer selectedCustomer = customerComboBox.getValue();
        if (selectedCustomer == null) {
            insightsTextArea.setText("Please select a customer first.");
            return;
        }

        StringBuilder purchases = new StringBuilder("Purchases last month by " + selectedCustomer.getName() + ":\n");

        for (Sale sale : salesController.getSalesList()) {
            if (sale.getCustomer().equals(selectedCustomer) && isFromLastMonth(sale)) {
                purchases.append("- ")
                        .append(sale.getQuantity())
                        .append(" x ")
                        .append(sale.getProduct().getName())
                        .append(" on ")
                        .append(sale.getPurchaseDate())
                        .append("\n");
            }
        }

        if (purchases.toString().endsWith(":\n")) {
            purchases.append("None.");
        }

        insightsTextArea.setText(purchases.toString());
    }

    // from last month method to make customer and product stats work
    private boolean isFromLastMonth(Sale sale) {
        LocalDate now = LocalDate.now();
        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());

        return !sale.getPurchaseDate().isBefore(lastMonthStart) && !sale.getPurchaseDate().isAfter(lastMonthEnd);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.show();
    }

    // show error message
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.show();
    }

    // get the Sales UI component to be added to the tab
    public VBox getSalesTab() {
        return (VBox) addButton.getParent();
    }
}

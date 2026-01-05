package store.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import store.controller.CustomerController;
import store.model.Customer;
import store.utilities.StoreData;
import store.utilities.StoreSerializer;

public class CustomerView {
    private final CustomerController customerController;
    private final TextArea customerListArea;
    private final TextField nameField;
    private final TextField emailField;

    private ListView<Customer> customerListView;


    public CustomerView(CustomerController controller) {
        this.customerController = controller;
        this.customerListArea = new TextArea();
        this.customerListArea.setEditable(false);
        this.nameField = new TextField();
        this.emailField = new TextField();
    }

    public VBox getCustomerTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        customerListView = new ListView<>();
        customerListView.getItems().addAll(customerController.getCustomerList());

        // Labels & Input Fields
        Label nameLabel = new Label("Customer Name:");
        Label emailLabel = new Label("Email:");
        nameField.setPromptText("Enter Name");
        emailField.setPromptText("Enter Email");

        // Buttons
        Button addButton = new Button("Add Customer");
        Button removeButton = new Button("Remove Customer");
        Button saveButton = new Button("Save");
        Button loadButton = new Button("Load");
        Button exitButton = new Button("Exit");

        // Event Handlers
        addButton.setOnAction(e -> {
            customerController.addCustomer(nameField, emailField);
            updateCustomerList();
            nameField.clear();
            emailField.clear();
        });

        removeButton.setOnAction(e -> {
            String selectedText = customerListArea.getSelectedText();
            customerController.removeCustomer(selectedText);
            updateCustomerList();
        });

        saveButton.setOnAction(e -> {
            customerController.saveCustomers();
            updateCustomerList();
            handleSaveCustomers();
        });
//        saveButton.setOnAction(e -> {
//            StoreData data = new StoreData(customerController.getCustomerList(), null, null);
//            StoreSerializer.saveAll(data);
//            showInfo("Customer data saved successfully!!!");
//        });

        loadButton.setOnAction(e -> {
            customerController.loadCustomers();
            updateCustomerList();
        });
//        loadButton.setOnAction(e -> {
//            StoreData data = StoreSerializer.loadAll();
//            if (data != null && data.getCustomers() != null) {
//                customerController.getCustomerList().clear();
//                customerController.getCustomerList().addAll(data.getCustomers());
//                customerListView.getItems().setAll(customerController.getCustomerList());
//                showInfo("Customer data loaded successfully.");
//            }
//        });

        exitButton.setOnAction(e -> System.exit(0)); // Close application

        // Layout
        layout.getChildren().addAll(
                nameLabel,
                nameField,
                emailLabel,
                emailField,
                addButton,
                removeButton,
                new Label("Customers List:"),
                customerListArea,
                saveButton,
                loadButton,
                exitButton);
        return layout;
    }

    private void updateCustomerList() {
        customerListArea.setText(customerController.listCustomers());
    }

    private void handleSaveCustomers() {
        customerController.saveCustomers();
        showInfo("Customers saved successfully!!!");
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.show();
    }
}

package store.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import store.model.Customer;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class CustomerController {

    private final ArrayList<Customer> customerList = new ArrayList<>();
    private int customerId = 1;  // Auto-incrementing ID
    private final String FILE_NAME = "customers.txt";

    public void addCustomer(TextField nameField, TextField emailField) {
        String name = nameField.getText();
        String email = emailField.getText();

        if (!name.isEmpty() && !email.isEmpty()) {
            Customer customer = new Customer(customerId++, name);
            customerList.add(customer);
        }
    }

    public void removeCustomer(String selectedText) {
        if (selectedText.isEmpty()) {
            return;
        }

        Iterator<Customer> iterator = customerList.iterator();
        while (iterator.hasNext()) {
            Customer customer = iterator.next();
            if (selectedText.contains("ID: " + customer.getId() + ", Name: " + customer.getName())) {
                iterator.remove();
                break;
            }
        }
    }

    public String listCustomers() {
        StringBuilder sb = new StringBuilder();
        for (Customer customer : customerList) {
            sb.append(customer.toString()).append("\n");
        }
        return sb.toString();
    }

    public void saveCustomers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Customer customer : customerList) {
                writer.write(customer.getId() + "," + customer.getName());
                writer.newLine();
            }
            System.out.println("Customer saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCustomers() {
        customerList.clear();
        File file = new File(FILE_NAME);

        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];

                    customerList.add(new Customer(id, name));
                    customerId = Math.max(customerId, id + 1);

                }
            }
            listCustomers();
            showAlert("Yayyy!!!", "Customers loaded successfully!");
        } catch (IOException e) {
            showAlert("Nayyy...", "Could not load customers.");
        }
    }

    public ArrayList<Customer> getCustomerList() {
        return customerList;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

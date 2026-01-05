package store.controller;

import store.model.Product;
import java.io.*;
import java.util.ArrayList;

public class ProductController {
    private final ArrayList<Product> productList = new ArrayList<>();
    private final String FILE_NAME = "products.txt";

    // get list of products
    public ArrayList<Product> getProductList() {
        return productList;
    }

    // add new product
    public void addProduct(String name, double price, int stockQuantity) {
        Product newProduct = new Product(name, price, stockQuantity); // default stock is 0
        productList.add(newProduct);
        saveProducts();
    }

    // update existing products
    public void updateProduct(Product product, String newName, double newPrice, int newStockQuantity) {
        product.setName(newName);
        product.setPrice(newPrice);
        product.setStockQuantity(newStockQuantity);
    }

    // remove a product
    public void removeProduct(Product product) {
        productList.remove(product);
    }

    public void saveProducts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Product product : productList) {
                writer.write(product.getName() + "," + product.getPrice() + "," + product.getStockQuantity());
                writer.newLine();
            }
            System.out.println("Products saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving products: " + e.getMessage());
        }
    }

    public void loadProducts() {
        productList.clear();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No saved products file found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    String name = data[0];
                    double price = Double.parseDouble(data[1]);
                    int quantity = -1;
                    productList.add(new Product(name, price, quantity));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

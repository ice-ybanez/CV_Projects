package store.controller;

import store.model.Customer;
import store.model.Product;
import store.model.Sale;
import java.util.ArrayList;
import java.time.LocalDate;

public class SalesController {

    private final ArrayList<Sale> salesList = new ArrayList<>();
    private final ArrayList<Customer> customerList;
    private final ArrayList<Product> productList;
    private double totalSalesAmount;

    public SalesController(ArrayList<Customer> customerList, ArrayList<Product> productList) {
        this.customerList = customerList;
        this.productList = productList;
        this.totalSalesAmount = 0.0;
    }

    // add a new sale
    public void addSale(Customer customer, Product product, int quantity) {

        if (product.getStockQuantity() < quantity) {
            System.out.println("There isn't enough stock.");
            return;
        }

        Sale newSale = new Sale(customer, product, quantity, LocalDate.now()); // added date
        product.setStockQuantity(product.getStockQuantity() - quantity);
        salesList.add(newSale); // store the sale in the list

//        product.reduceStock(quantity);

        double saleAmount = product.getPrice() * quantity;
        totalSalesAmount += saleAmount;

        System.out.println("Sale Added: " + customer.getName() + " bought " + quantity + " x " + product.getName());
        System.out.println("Sale Added: " + newSale);
    }

    // get all sales
    public ArrayList<Sale> getSalesList() {
        return salesList;
    }

    // total sales amount
    public double getTotalSalesAmount() {
        return totalSalesAmount;
    }

    // get customer list for ComboBox
    public ArrayList<Customer> getCustomerList() {
        return customerList;
    }

    // get product list for ComboBox
    public ArrayList<Product> getProductList() {
        return productList;
    }
}

package store.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Sale implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Customer customer;
    private Product product;
    private int quantity;
    private double totalPrice;
    private LocalDate purchaseDate;

    // Constructor
    public Sale(Customer customer, Product product, int quantity, LocalDate purchaseDate) {
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity;
        this.purchaseDate = purchaseDate;
    }

    // Getters
    public Customer getCustomer() {
        return customer;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    // Setters
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity; // Update totalPrice
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    // toString method
    @Override
    public String toString() {
        return "Sale - Customer: " + customer.getName() +
                ", Product: " + product.getName() +
                ", Quantity: " + quantity +
                ", Total Price: €" + totalPrice +
                ", Date: " + purchaseDate;
    }
}

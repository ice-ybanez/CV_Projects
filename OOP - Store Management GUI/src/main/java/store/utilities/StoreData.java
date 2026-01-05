package store.utilities;

import store.model.Customer;
import store.model.Product;
import store.model.Sale;

import java.io.Serializable;
import java.util.List;


public class StoreData implements Serializable {
    private List<Customer> customers;
    private List<Product> products;
    private List<Sale> sales;

    public StoreData(List<Customer> customers, List<Product> products, List<Sale> sales) {
        this.customers = customers;
        this.products = products;
        this.sales = sales;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Sale> getSales() {
        return sales;
    }
}

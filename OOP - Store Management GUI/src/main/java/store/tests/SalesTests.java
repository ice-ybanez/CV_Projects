// unable to get the right dependencies on pom.xml so unable to test :(
//
//package store.tests;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import store.model.Customer;
//import store.model.Product;
//import store.model.Sale;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class StoreTests {
//
//    private ArrayList<Customer> customers;
//    private ArrayList<Sale> sales;
//
//    @BeforeEach
//    public void setup() {
//        customers = new ArrayList<>();
//        customers.add(new Customer("A123", "Alice"));
//        customers.add(new Customer("B456", "Bob"));
//        customers.add(new Customer("C789", "Charlie"));
//
//        Product product = new Product("TestProduct", 10.0);
//        sales = new ArrayList<>();
//        sales.add(new Sale(customers.get(0), product, 2));
//        sales.add(new Sale(customers.get(1), product, 1));
//    }
//
//    // Test 1: check name integrity (not null or empty)
//    @Test
//    public void testCustomerNameIntegrity() {
//        for (Customer customer : customers) {
//            assertNotNull(customer.getName(), "Customer name should not be null");
//            assertFalse(customer.getName().isEmpty(), "Customer name should not be empty");
//        }
//    }
//
//
//    // Test 2: sorting customers alphabetically by name
//    @Test
//    public void testCustomerSortingByName() {
//        ArrayList<Customer> sorted = new ArrayList<>(customers);
//        sorted.sort(Comparator.comparing(Customer::getName));
//
//        assertEquals("Greg", sorted.get(0).getName());
//        assertEquals("Fred", sorted.get(1).getName());
//        assertEquals("Steve", sorted.get(2).getName());
//    }
//}

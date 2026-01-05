package store.model;

//--------------------------------------------------
//	IMPORTS
//--------------------------------------------------
import java.io.Serial;
import java.io.Serializable;

//--------------------------------------------------
//
//	CLASS Product
//
//--------------------------------------------------
/**
 * This class models a Product of the online store<br>.
 */
public class Product implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    //---------------------------------------
    //	Fields
    //---------------------------------------
    private String name; // Name of the product
    private double price; // Price of the product
    private int stockQuantity; // stock quantity

    //---------------------------------------
    //	Constructor
    //---------------------------------------
    /**
     * The constructor creates 1 instance (1 object) of the class Product<br>
     * @param name - Name of the product.
     * @param price - Price of the product.
     */
    public Product(String name, double price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    //---------------------------------------
    //	GET METHODS
    //---------------------------------------

    //---------------------------------------
    //	getName
    //---------------------------------------
    /**
     *
     * @return - Returns the name of the product.
     */
    public String getName() {
        return name;
    }

    //---------------------------------------
    //	getPrice
    //---------------------------------------
    /**
     *
     * @return - Returns the price of the product.
     */
    public double getPrice() {
        return price;
    }


    /**
     *
     * @return - Returns the quantity of the product.
     */
    public int getStockQuantity() {
        return stockQuantity;
    }


    //---------------------------------------
    //	SET METHODS
    //---------------------------------------

    //---------------------------------------
    //	setName
    //---------------------------------------
    /**
     *
     * @param name - Sets the name of the product as the name provided.<br>
     */
    public void setName(String name) {
        this.name = name;
    }

    //---------------------------------------
    //	setPrice
    //---------------------------------------

    /**
     *
     * @param price - Sets the price of the product as the price provided.<br>
     */
    public void setPrice(double price) {
        this.price = price;
    }

    // setStockQuantity
    /**
     *
     * @param stockQuantity - Sets the stock quantity of the product as the quantity provided.<br>
     */
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }


    //---------------------------------------
    //	EXTRA METHODS
    //---------------------------------------

    // reduces stock when a sale is made
    public void reduceStock(int amount) {
        this.stockQuantity -= amount;
    }

    //---------------------------------------
    //	toString
    //---------------------------------------
    /**
     *
     * @return - Returns a string representation of the product.
     */
    public String toString() {
        return "Name: " + name + ", Price: €" + price;
    }
}

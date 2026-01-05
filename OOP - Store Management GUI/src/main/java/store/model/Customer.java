// using Customer.java from last semester

package store.model;

//--------------------------------------------------
//	IMPORTS
//--------------------------------------------------
import java.io.Serial;
import java.io.Serializable;

//--------------------------------------------------
//
//	CLASS Customer
//
//--------------------------------------------------
/**
 * This class models a customer of the online store.<br>
 */
public class Customer implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    //---------------------------------------
    //	Fields
    //---------------------------------------
    private int id; // Unique ID for the customer
    private String name; // Name of the customer

    //---------------------------------------
    //	Constructor
    //---------------------------------------
    /**
     * The constructor creates 1 instance (1 object) of the class Customer.<br>
     *
     * @param id   Unique ID for the customer.
     * @param name Name of the customer.
     */
    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    //---------------------------------------
    //	GET METHODS
    //---------------------------------------


    //---------------------------------------
    //	getId
    //---------------------------------------
    /**
     * @return - Returns the id of the customer.
     */
    public int getId() {
        return id;
    }

    //---------------------------------------
    //	getName
    //---------------------------------------
    /**
     *
     * @return - Returns the name of the customer
     */
    public String getName() {
        return name;
    }


    //---------------------------------------
    //	SET METHODS
    //---------------------------------------


    //---------------------------------------
    //	setId
    //---------------------------------------
    /**
     * @param id - Sets the id of customer as the id provided.<br>
     */
    public void setId(int id) {
        this.id = id;
    }

    //---------------------------------------
    //	setName
    //---------------------------------------
    /**
     *
     * @param name - Sets the name of the customer as the name provided.<br>
     */
    public void setName(String name) {
        this.name = name;
    }

    //---------------------------------------
    //	EXTRA METHODS
    //---------------------------------------


    //---------------------------------------
    //	toString
    //---------------------------------------
    /**
     * @return - Returns a string representation of the customer.
     */
    public String toString() {
        return "Customer ID: " + id + ", Name: " + name;
    }
}

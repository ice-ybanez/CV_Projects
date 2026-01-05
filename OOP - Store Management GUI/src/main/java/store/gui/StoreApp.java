package store.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import store.controller.CustomerController;
import store.controller.ProductController;
import store.view.CustomerView;
import store.view.ProductView;
import store.view.SalesView;
import store.controller.SalesController;

public class StoreApp extends Application {

    @Override
    public void start(Stage stage) {

        // adding icon to the stage/window
        Image icon = new Image("unnamed.png");
        stage.getIcons().add(icon);

        stage.setTitle("Store Management System");

        TabPane tabPane = new TabPane();

        // customers tab
        CustomerController customerController = new CustomerController();
        customerController.loadCustomers(); // load saved customers
        CustomerView customerView = new CustomerView(customerController);
        Tab customerTab = new Tab("Customers", customerView.getCustomerTab());

        // maintenance tab for products
        ProductController productController = new ProductController();
        productController.loadProducts(); // load saved products
        ProductView productView = new ProductView(productController);
        Tab productTab = new Tab("Maintenance", productView.getProductTab());

        // sales tab
        SalesController salesController = new SalesController(
                customerController.getCustomerList(),
                productController.getProductList()
        );
        SalesView salesView = new SalesView(salesController);
        Tab salesTab = new Tab("Sales", salesView.getSalesTab());

        // connecting all tabs into tab pane
        tabPane.getTabs().addAll(customerTab, salesTab, productTab);

        // stage & scene
        Scene scene = new Scene(tabPane, 600, 800);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

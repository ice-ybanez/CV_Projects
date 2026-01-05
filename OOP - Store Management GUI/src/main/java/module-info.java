module com.example.stage3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens store.gui to javafx.fxml;
    exports store.gui;
}
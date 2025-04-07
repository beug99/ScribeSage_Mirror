module com.example.addressbook {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.addressbook to javafx.fxml;
    exports com.example.addressbook;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    exports com.example.addressbook.model;
    opens com.example.addressbook.model to javafx.fxml;
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    exports com.example.addressbook.controller;
    opens com.example.addressbook.controller to javafx.fxml;
}
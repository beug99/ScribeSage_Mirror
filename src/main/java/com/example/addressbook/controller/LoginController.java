package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

import static java.sql.DriverManager.println;

public class LoginController {
    @FXML
    private Button logIn;
    @FXML
    private TextField eMail;
    @FXML
    private PasswordField password;

    @FXML
    private void onLogIn() throws IOException{
        String emailInput = this.eMail.getText();
        String passwordInput = this.password.getText();

        if (SqliteConnection.authenticateUser(emailInput, passwordInput)) {
            // if user is successful, move them to the main note view
            Stage stage = (Stage) logIn.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
            stage.setScene(scene);
        } else {
            // show error message for failed log in attempts
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText(emailInput + " " + passwordInput + "Invalid email or password. Please check you have" + " " +
                    "typed your email and password correctly");
            alert.showAndWait();
        }
    }
}

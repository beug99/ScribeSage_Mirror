package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.Session;
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
    private Button signUp;

    @FXML
    protected void onSignUp() throws IOException {
        Stage stage = (Stage) logIn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);

        stage.show();
    }

    @FXML
    private void onLogIn() throws IOException{
        String emailInput = this.eMail.getText();
        String passwordInput = this.password.getText();

        if (emailInput.isBlank() || passwordInput.isBlank()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("You must input both your email and password!");
            alert.showAndWait();
        }
        else if (SqliteUserDAO.authenticateUser(emailInput, passwordInput)) {
            // fetch full user details
            User user = SqliteUserDAO.getUserByEmail(emailInput);

            if (user != null) {
                Session.setUser(user.getEmail(), user.getFirstName(), user.getLastName());
            }

            // load the homepage
            Stage stage = (Stage) logIn.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("homepage-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(false);

            stage.show();

        } else {
            // show error message for failed log in attempts
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid email or password. Please check you have" + " " +
                    "typed your email and password correctly");
            alert.showAndWait();
        }
    }
}

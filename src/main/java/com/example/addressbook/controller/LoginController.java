package com.example.addressbook.controller;

import com.example.addressbook.Session;
import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

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
        SceneLoader.switchScene(stage, "signup-view.fxml", false);
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
            SceneLoader.switchScene(stage, "homepage-view.fxml", false);

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

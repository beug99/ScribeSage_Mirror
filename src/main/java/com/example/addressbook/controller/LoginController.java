package com.example.addressbook.controller;

import com.example.addressbook.Session;
import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * A controller class that manages the Login page of the application.
 * Upon opening the App, the scene is set to login-view.fxml. The user can interact with
 * the page by logging in with existing account details or by redirecting to the signup page.
 */
public class LoginController {
    @FXML
    private Button logIn;
    @FXML
    private TextField eMail;
    @FXML
    private PasswordField password;
    @FXML
    private Button signUp;

    /**
     * A method that will load the Sign-up page when the 'Sign Up' button is clicked.
     * @throws IOException An error will occur or the Signup page will not load.
     */
    @FXML
    protected void onSignUp() throws IOException {
        Stage stage = (Stage) logIn.getScene().getWindow();
        SceneLoader.switchScene(stage, "signup-view.fxml", false);
    }

    /**
     * A method that checks the user credentials against the database and either logs an existing
     * user into the application or alerts the user that the log-in has failed and provides the
     * reason why. If the log-in is successful, the user details are loaded from the database and
     * the session credentials are set.
     * @throws IOException An error will appear or the log in function will fail.
     */
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
            SceneLoader.switchScene(stage, "homepage-view.fxml", true);
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

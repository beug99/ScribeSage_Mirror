package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.Session;
import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.SqliteUserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This controller class manages the update details page for the application.
 * The scene will load through the updateDetails-view.fxml. It provides methods for users
 * to alter their email address and password that are stored on the database.
 */
public class UpdateDetailsController {

    @FXML private Label nameLabel;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button backButton;

    /**
     * The initialize method sets the display labels to the name, email address and password
     * of the current logged-in user by collecting the current session information. The password
     * is hidden for confidentiality.
     */
    public void initialize() {
        nameLabel.setText(Session.getFirstName() + " " + Session.getLastName());
        emailField.setText(Session.getLoggedInEmail());
        passwordField.setText("**********"); // Just display, not the actual password
    }

    /**
     * A method that loads the applications home page {@link HomePageController} when the home button is clicked.
     * @throws IOException Page fails to load.
     */
    @FXML
    private void onBackToHome() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.hide();
    }

    /**
     * This method loads the Update Email page {@link UpdateEmailController} when the 'Change' button is clicked.
     * @throws IOException An error appears or the page fails to load.
     */
    @FXML
    private void onChangeEmail() throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();
        SceneLoader.switchScene(stage, "updateEmail-view.fxml", false);
    }

    /**
     * This method loads the Update Password page {@link UpdatePasswordController} when the 'Change' button is clicked.
     * @throws IOException An error appears or the page fails to load.
     */
    @FXML
    private void onChangePassword() throws IOException {
        Stage stage = (Stage) passwordField.getScene().getWindow();
        SceneLoader.switchScene(stage, "updatePassword-view.fxml", false);
    }
}

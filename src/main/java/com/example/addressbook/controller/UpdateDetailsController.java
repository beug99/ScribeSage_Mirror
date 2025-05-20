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

public class UpdateDetailsController {

    @FXML private Label nameLabel;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button backButton;


    public void initialize() {
        nameLabel.setText(Session.getFirstName() + " " + Session.getLastName());
        emailField.setText(Session.getLoggedInEmail());
        passwordField.setText("**********"); // Just display, not the actual password
    }

    @FXML
    private void onBackToHome() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneLoader.switchScene(stage, "homepage-view.fxml", false);
    }


    @FXML
    private void onChangeEmail() throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();
        SceneLoader.switchScene(stage, "updateEmail-view.fxml", false);
    }

    @FXML
    private void onChangePassword() throws IOException {
        Stage stage = (Stage) passwordField.getScene().getWindow();
        SceneLoader.switchScene(stage, "updatePassword-view.fxml", false);
    }
}

package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.Session;
import com.example.addressbook.model.SqliteUserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdateDetailsController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button backButton;

    public void initialize() {
        nameField.setText(Session.getFirstName() + " " + Session.getLastName());
        emailField.setText(Session.getLoggedInEmail());
        passwordField.setText("**********"); // Just display, not the actual password
    }

    @FXML
    private void onBackToHome() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/addressbook/homepage-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }


    @FXML
    private void onChangeEmail() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/addressbook/updateEmail-view.fxml"));
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    private void onChangePassword() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/addressbook/updatePassword-view.fxml"));
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.centerOnScreen();
        stage.show();
    }

}

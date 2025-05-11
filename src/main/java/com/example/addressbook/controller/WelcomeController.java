package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.helper.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private Button logIn;

    @FXML
    protected void onLogIn() throws IOException {
        Stage stage = (Stage) logIn.getScene().getWindow();
        SceneLoader.switchScene(stage, "login-view.fxml", false);
    }

    @FXML
    protected void onSignUp() throws IOException {
        Stage stage = (Stage) logIn.getScene().getWindow();
        SceneLoader.switchScene(stage, "signup-view.fxml", false);
    }
}

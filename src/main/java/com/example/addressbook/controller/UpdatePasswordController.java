package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.Session;
import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class UpdatePasswordController {
    @FXML
    private PasswordField oldPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private Button updatePWordConfirm;
    @FXML
    private Button backButton;


    @FXML
    private void onUpdatePassword() throws IOException {
        String oldPassword = this.oldPassword.getText();
        String newPassword = this.newPassword.getText();
        String email = Session.getLoggedInEmail();

        if (SqliteUserDAO.updatePassword(email, oldPassword, newPassword)) {
            // check if oldPassword matches latest password used in authenticateUser method (current user password)
            Stage stage = (Stage) updatePWordConfirm.getScene().getWindow();
            SceneLoader.switchScene(stage, "updateDetails-view.fxml", false);

        } else {
            // show error message for failed log in attempts
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Password Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid email or password. Please check you have" + " " +
                    "typed your old password correctly");
            alert.showAndWait();
        }
    }

    @FXML
    private void onBackToDetails() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneLoader.switchScene(stage, "updateDetails-view.fxml", false);
    }
}

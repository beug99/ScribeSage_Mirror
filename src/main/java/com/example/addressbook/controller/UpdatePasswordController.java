package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.Session;
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
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("updateDetails-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();

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
    private void onBackToDetails(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/addressbook/updateDetails-view.fxml"));
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }


    public void onBackToDetails(javafx.event.ActionEvent actionEvent) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/addressbook/updateDetails-view.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();
        }

}

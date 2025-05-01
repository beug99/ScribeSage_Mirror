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

public class UpdateEmailController {

    @FXML private PasswordField currentPassword;
    @FXML private TextField newEmailField;
    @FXML private Button confirmEmailUpdate;
    @FXML
    private Button homeButton;

    @FXML
    public void onHomeButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("updateDetails-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    private void onConfirmUpdateEmail() throws IOException {
        String currentPwd = currentPassword.getText();
        String newEmail = newEmailField.getText();
        String currentEmail = Session.getLoggedInEmail();

        boolean success = SqliteUserDAO.updateEmail(currentEmail, currentPwd, newEmail);

        if (success) {
            Session.setLoggedInEmail(newEmail); // update session
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Your email was updated successfully!");
            alert.showAndWait();

            // Optionally return to profile view
            Stage stage = (Stage) confirmEmailUpdate.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("updateDetails-view.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Failed");
            alert.setHeaderText(null);
            alert.setContentText("Incorrect password or failed update. Please try again.");
            alert.showAndWait();
        }
    }
}

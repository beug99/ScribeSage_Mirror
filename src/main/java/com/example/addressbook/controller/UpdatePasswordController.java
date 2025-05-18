package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.Session;
import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.regex.Pattern;


/**
 * This controller class manages the update password page for the application.
 * The scene will load through the updatePassword-view.fxml. It provides methods for users
 * to alter their password that is stored on the database.
 */
public class UpdatePasswordController {
    @FXML
    private PasswordField oldPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private Button updatePWordConfirm;
    @FXML
    private Button backButton;

    /**
     * A method that authenticates the current session user, checks the validity of the new password and
     * updates user and the database depending on the result. It takes user input and retrieves the current
     * session parameters. The sqliteUserDAO.updatePassword method is called and utilised within the method.
     * @throws IOException An error will occur or the password will not update.
     */
    @FXML
    private void onUpdatePassword() throws IOException {
        String oldPassword = this.oldPassword.getText();
        String newPassword = this.newPassword.getText();
        String email = Session.getLoggedInEmail();

        if (SqliteUserDAO.updatePassword(email, oldPassword, newPassword)) {
            // check if oldPassword matches latest password used in authenticateUser method (current user password)

            // Displays an alert advising the user that the password has bene updated before redirecting back to
            // the update details page
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Success");
            success.setHeaderText(null);
            success.setContentText("Your password has been successfully updated.");
            success.showAndWait();

            Stage stage = (Stage) updatePWordConfirm.getScene().getWindow();
            SceneLoader.switchScene(stage, "updateDetails-view.fxml", false);

        } else {
            // Show error message for failed log in attempts
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Password Failed");
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setContentText("Invalid Password. A valid password is at least 8 characters long, " +
                    "contains a mix of upper-case and lower-case characters, numbers, and symbols. " +
                    "Your new password can not be your most recent password.");
            alert.showAndWait();
        }
    }

    /**
     * This method returns the user to the previous Update Details page. If the 'Confirm Update'
     * button has not been clicked, the updates are discarded.
     * @throws IOException An error appears or the page does not load.
     */
    @FXML
    private void onBackToDetails() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneLoader.switchScene(stage, "updateDetails-view.fxml", false);
    }
}

package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.Session;
import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.INoteDAO;
import com.example.addressbook.model.Note;
import com.example.addressbook.model.SqliteNoteDAO;
import com.example.addressbook.model.INoteDAO;
import com.example.addressbook.model.SqliteNoteDAO;
import com.example.addressbook.model.SqliteUserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This controller class manages the update email page for the application.
 * The scene will load through the updateEmail-view.fxml. It provides methods for users
 * to alter their email address that is stored on the database.
 */
public class UpdateEmailController {

    @FXML private PasswordField currentPassword;
    @FXML private TextField newEmailField;
    @FXML private Button backButton;
    @FXML private INoteDAO noteDAO;

    /**
     * Constructs an instance of a note Data Access Object (INoteDAO) and assigns it to noteDAO.
     * TODO CHECK THIS?? SHOULD IT BE NOTE DAO?
     */
    public UpdateEmailController()
    {
        noteDAO = new SqliteNoteDAO();
    }

    /**
     * This method assesses the user input from the password and new email fields and validates
     * if the changes are permitted. The current users password must be accurate, the new email
     * address must be in the correct syntax. If either of these are incorrect, the user is alerted
     * to the reason why the change of email address has failed. If successful, the email address in
     * the database is updated. It accesses methods from {@link SqliteUserDAO} for validation.
     * @throws IOException An error appears or the page fails to load.
     */
    @FXML
    private void onConfirmUpdateEmail() throws IOException {
        String currentPwd = currentPassword.getText();
        String newEmail = newEmailField.getText();
        String currentEmail = Session.getLoggedInEmail();

        // Validate new email format before proceeding
        if (!isValidEmail(newEmail)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address.");
            return;
        }

        boolean success = SqliteUserDAO.updateEmail(currentEmail, currentPwd, newEmail);

        if (success) {
            // Update session email
            Session.setLoggedInEmail(newEmail);

            // Update associated notes
            List<Note> currentNotes = noteDAO.getNotesByOwner(currentEmail);
            if (currentNotes != null) {
                for (Note note : currentNotes) {
                    note.setNoteOwner(newEmail);
                    noteDAO.updateNote(note);
                }
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Your email was updated successfully!");
            Stage stage = (Stage) backButton.getScene().getWindow();
            SceneLoader.switchScene(stage, "updateDetails-view.fxml", false);

        } else {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Incorrect password or failed update. Please try again.");
        }
    }

    /**
     * A method that analyses the email entered by the user. If the email is written in the correct
     * syntax it is valid, else it is invalid.
     * @param email The new email entered by the user.
     * @return True if valid, False if invalid.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /**
     * This method returns the user to the previous Update Details page {@link UpdateDetailsController}.
     * If the 'Confirm Update' button has not been clicked, the updates are discarded.
     * @throws IOException An error appears or the page does not load.
     */
    public void onBackToDetails() throws IOException  {
            Stage stage = (Stage) backButton.getScene().getWindow();
            SceneLoader.switchScene(stage, "updateDetails-view.fxml", false);
    }

    /**
     * This method manages the Error alert that appears when interacting with the Update Email page.
     * It requires input parameters of alert type (Error, Information etc.), the error title and
     * content of the error which will display to the user once the method is called.
     * @param type The type of the alert.
     * @param title The title of the alert.
     * @param content The content of the alert.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

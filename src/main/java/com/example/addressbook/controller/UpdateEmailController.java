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

public class UpdateEmailController {

    @FXML private PasswordField currentPassword;
    @FXML private TextField newEmailField;
    @FXML private Button backButton;
    @FXML private INoteDAO noteDAO;

    public UpdateEmailController()
    {
        noteDAO = new SqliteNoteDAO();
    }

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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    public void onBackToDetails() throws IOException  {
            Stage stage = (Stage) backButton.getScene().getWindow();
            SceneLoader.switchScene(stage, "updateDetails-view.fxml", false);
        }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}

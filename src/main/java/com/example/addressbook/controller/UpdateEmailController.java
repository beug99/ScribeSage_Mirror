package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.Session;
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

public class UpdateEmailController {

    @FXML private PasswordField currentPassword;
    @FXML private TextField newEmailField;
    @FXML private Button confirmEmailUpdate;
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

        boolean success = SqliteUserDAO.updateEmail(currentEmail, currentPwd, newEmail);

        if (success) {
            // Update session
            Session.setLoggedInEmail(newEmail);

            // Update user's note owner's new email
            List<Note> currentNotes = noteDAO.getNotesByOwner(currentEmail);
            for (Note note : currentNotes) {
                note.setNoteOwner(newEmail);
                noteDAO.updateNote(note);
            }

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
            stage.setResizable(false);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Failed");
            alert.setHeaderText(null);
            alert.setContentText("Incorrect password or failed update. Please try again.");
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

    public void onBackToDetails(javafx.event.ActionEvent actionEvent) throws IOException  {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/addressbook/updateDetails-view.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();
        }
}

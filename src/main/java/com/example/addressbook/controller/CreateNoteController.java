package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.model.Note;
import com.example.addressbook.model.INoteDAO;
import com.example.addressbook.model.SqliteNoteDAO;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateNoteController {
    public Label folderName;
    public Button createButton;
    public Button homeButton;

    public String currentNote;

    @FXML
    private ListView<Note> noteListView;
    private INoteDAO noteDAO;


    public CreateNoteController() {
        noteDAO = new SqliteNoteDAO();
    }

    @FXML
    private TextField noteNameTextField;
    @FXML
    private TextField noteTagsTextField;



    @FXML
    public void onCreateButtonClick(ActionEvent actionEvent) throws IOException {
        NewNoteController labelForFXML = null;
        if (noteNameTextField != null && noteTagsTextField != null) {
            Note newNote = new Note(noteNameTextField.getText(), noteTagsTextField.getText());

            noteDAO.addNote(newNote);

            currentNote = noteNameTextField.getText();

            Stage stage = (Stage) createButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("new-note-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            labelForFXML = fxmlLoader.getController();
            labelForFXML.setLabelText(currentNote);
        }
    }

    public void onHomeClick(ActionEvent actionEvent) throws IOException {

        Stage stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("create-note-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    public void onUploadSparseClick(ActionEvent actionEvent) {
    }

    public void onUploadCanvasClick(ActionEvent actionEvent) {
    }

    public void onAINoteSummariseClick(ActionEvent actionEvent) {
    }

    public void onChangeFolderClick(ActionEvent actionEvent) {
    }


    @FXML
    public void onCancel(ActionEvent actionEvent) {
        //Make link to go back to home page
    }

}

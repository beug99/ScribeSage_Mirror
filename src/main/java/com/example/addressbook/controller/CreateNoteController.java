package com.example.addressbook.controller;
import com.example.addressbook.HelloApplication;
import com.example.addressbook.Session;
import com.example.addressbook.model.Note;
import com.example.addressbook.model.INoteDAO;
import com.example.addressbook.model.SqliteNoteDAO;
import com.example.addressbook.controller.HomePageController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;


public class CreateNoteController {
    public Label folderName;
    public Button createNoteButton;
    public Button homeButton;
    public Button cancelButton;
    public String currentNote;

    @FXML
    private TextField noteNameTextField;
    @FXML
    private TextField noteTagsTextField;
    @FXML
    private ListView<Note> noteListView;

    private INoteDAO noteDAO;


    public CreateNoteController() {
        noteDAO = new SqliteNoteDAO();
    }

    @FXML
    public void onCreateButtonClick(ActionEvent actionEvent) throws IOException {
        //When clicked, create instance of a new note, adds initial note to DB
        //Opens the New Note scene under with the note name that was entered

        NewNoteController labelForFXML = null;
        if (noteNameTextField != null && noteTagsTextField != null) {
            Note newNote = new Note(noteNameTextField.getText(), noteTagsTextField.getText(), "Your Note", Session.getLoggedInEmail());
            noteDAO.addNote(newNote);
            currentNote = noteNameTextField.getText();

            Stage stage = (Stage) createNoteButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("new-note-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            stage.setScene(scene);
            labelForFXML = fxmlLoader.getController();
            labelForFXML.setLabelText(currentNote);
            labelForFXML.setCurrentNote(newNote);
        }
    }

    @FXML
    public void onHomeClick(ActionEvent actionEvent) throws IOException {
        //Will change the FXML file to the home page once I've merged the project

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("homepage-view.fxml"));
        Stage stage = (Stage) homeButton.getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    @FXML
    public void onCancelButtonClick() throws IOException {
        //This just re-loads the page a fresh, no text, no buttons clicked etc

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/addressbook/homepage-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    public void onUploadSparseClick(ActionEvent actionEvent) {
    }

    public void onUploadCanvasClick(ActionEvent actionEvent) {
        FileChooser chooseFile = new FileChooser();
        chooseFile.setTitle("Upload Canvas Material");
        chooseFile.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Supported Files", "*.pdf", "*.mp4","*.mp3","*.docx","*.txt"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("MP4 Files", "*.mp4"),
            new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"),
            new FileChooser.ExtensionFilter("DOCX Files", "*.docx"),
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        File selectedFile = chooseFile.showOpenDialog(((Node) actionEvent.getSource()).getScene().getWindow());
        if(selectedFile != null) {
            // Handle files (copy, link to note, etc.)
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    public void onAINoteSummariseClick(ActionEvent actionEvent) {
    }

    public void onChangeFolderClick(ActionEvent actionEvent) {
    }


}
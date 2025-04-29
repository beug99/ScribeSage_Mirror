package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.model.Note;
import com.example.addressbook.model.SqliteNoteDAO;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.web.*;
import java.io.IOException;


public class NewNoteController extends CreateNoteController {

    @FXML
    public Label currentNoteName;
    public Button homeButton;
    public TextField searchBarID;
    public Button saveButton;
    public VBox vBoxForHtmlGui;
    public HTMLEditor htmlEditorGui;
    public Button searchBarButton;

    private SqliteNoteDAO noteDOA;
    private Note currentNote;

    public NewNoteController() {
        super();
        noteDOA = new SqliteNoteDAO();
    }


    @FXML
    public void setLabelText(String text) {
        //This sets the note name displayed to the note name just created
        currentNoteName.setText(text);
    }

    @FXML
    public void setCurrentNote(Note note) {
        currentNote = note;
        System.out.println("Note set in NewNoteController: " + currentNote.getNoteName());
        if (currentNoteName != null && htmlEditorGui != null) {
            currentNoteName.setText(currentNote.getNoteName());
            htmlEditorGui.setHtmlText(currentNote.getNoteText());
            System.out.println("UI updated from setCurrentNote().");
        }

    }

    @FXML
    public void initialize() {
        System.out.println("Initialising NewNoteController");
        if(currentNote != null) {
            currentNoteName.setText(currentNote.getNoteName());
            htmlEditorGui.setHtmlText(currentNote.getNoteText());
            System.out.println("Trying to put note content in text field: " + currentNote.getNoteText());
        } else {
            System.out.println("Current Note is null");
        }
     }


    @FXML
    public void onSaveButtonClick(ActionEvent actionEvent) throws IOException {
        System.out.println("Attempting to save: Note ID_" + currentNote.getId() + " Note name_" + currentNote.getNoteName());
        String updatedContent = htmlEditorGui.getHtmlText();
        currentNote.setNoteText(updatedContent);
        noteDOA.updateNote(currentNote);
    }

//    @FXML
//    public void onLoadButtonClick(ActionEvent actionEvent) throws IOException {
//        System.out.println("Load button pressed");
//        //TODO Load another view with sole purpose to display notes associated with owner
//    }

    @FXML
    public void onHomeButtonClick(ActionEvent actionEvent) throws IOException  {

        Stage stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("homepage-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

    }

    @FXML
    public void searchBarButtonClick(ActionEvent actionEvent) {
        //TODO Create a search function - for a later sprint

    }

}
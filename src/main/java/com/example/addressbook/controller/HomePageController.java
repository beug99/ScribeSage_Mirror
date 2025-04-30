package com.example.addressbook.controller;

import com.example.addressbook.controller.CreateNoteController;
import com.example.addressbook.HelloApplication;
import com.example.addressbook.model.*;
import javafx.application.Platform;
import com.example.addressbook.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class HomePageController {
    @FXML
    public HBox profileBar;
    @FXML
    private Label updateDetailsLabel;
    @FXML
    private Button createNewButton;
    @FXML
    private VBox navMenu;
    @FXML
    private ListView<String> notesListView;
    @FXML
    private Label nameLabel;
    @FXML
    private Button delete;
    @FXML
    private Button load;
    private INoteDAO noteDAO;
    private ObservableList<Note> notesObservableList;
    private ObservableList<String> noteNamesObservableList;
    private Note selectedNote;


    public HomePageController() {
        noteDAO = new SqliteNoteDAO();
    }

    private void setSelectedNote(Note note) {
        selectedNote = note;
        System.out.println("Selected note: " + note.getNoteName() + " ID:" + note.getId() + " Owner: " + note.getNoteOwner());
    }


    public void initialize() {
        String fullName = Session.getFirstName() + " " + Session.getLastName();
        nameLabel.setText(fullName);

        loadUserNotes();

        Platform.runLater(() -> {
            navMenu.getScene().addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                // Only hide the menu if it's open and the click is outside the menu and profile bar
                if (navMenu.isVisible() &&
                        !navMenu.localToScene(navMenu.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY()) &&
                        !profileBar.localToScene(profileBar.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())) {

                    navMenu.setVisible(false);
                }
            });
        });
    }
    @FXML
    private void onUpdateDetails() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/addressbook/updateDetails-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) updateDetailsLabel.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    private void onLogOut() throws IOException {
        Stage stage = (Stage) updateDetailsLabel.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    // Loads a selected note from the LoadNote listener
    @FXML
    private void onLoadNote() throws IOException {
        if (selectedNote != null) {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/addressbook/new-note-view.fxml"));
            Parent root = fxmlLoader.load();

            // Sets Note and Note name for NoteController
            NewNoteController noteController = fxmlLoader.getController();
            noteController.setCurrentNote(selectedNote);
            noteController.setLabelText(selectedNote.getNoteName());

            Stage stage = (Stage) notesListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } else {
            System.out.println("No note selected to load.");
        }
    }

    // Deletes the selected note in the user's notes list from notes DB
    @FXML
    private void onDeleteNote() throws IOException {
        if (selectedNote != null) {
            noteDAO.deleteNote(selectedNote);
            loadUserNotes();
        } else {
            System.out.println("No note selected to delete.");
        }
    }

    // Presents current user's notes from db
    private void loadUserNotes() {
        try {
            // Load notes associated with user email from notes db
            List<Note> userNotes = noteDAO.getNotesByOwner(Session.getLoggedInEmail());

            // Populates the observable list with note objects
            notesObservableList = FXCollections.observableArrayList(userNotes);

            // Represent note objects via name (what appears visually)
            noteNamesObservableList = FXCollections.observableArrayList();
            for (Note note : notesObservableList) {
                noteNamesObservableList.add(note.getNoteName());
            }
            notesListView.setItems(noteNamesObservableList);

            // Listener for when user selects a note to Load or Delete
            notesListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    String selectedNoteName = notesListView.getSelectionModel().getSelectedItem();
                    if (selectedNoteName != null) {
                        for (Note note : notesObservableList) {
                            if (note.getNoteName().equals(selectedNoteName)) {
                                // Note to be passed for Loading/Deleting
                                setSelectedNote(note);
                                break;
                            }
                        }
                        System.out.println("Selected note: " + selectedNote.getNoteName());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleNavMenu(javafx.scene.input.MouseEvent mouseEvent) {
        navMenu.setVisible(!navMenu.isVisible());
    }

    @FXML
    private void onCreateNew(javafx.event.ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/addressbook/create-note-view.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) createNewButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

}

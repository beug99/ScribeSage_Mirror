package com.example.addressbook.shellsstufftodelete;

import com.example.addressbook.model.Note;
import com.example.addressbook.model.INoteDAO;
import com.example.addressbook.model.SqliteNoteDAO;


import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainController {
    @FXML
    private ListView<Note> noteListView;
    private INoteDAO noteDAO;
//
//    public ListView notesListView;
//    @FXML
//    private ListView<Note> noteListView;
//    private INoteDAO noteDAO;

    public MainController() {
        noteDAO = new SqliteNoteDAO();
    }


    @FXML
    private TextField noteNameTextField;
    @FXML
    private TextField noteTagsTextField;


    private void selectNote(Note note) {
        noteListView.getSelectionModel().select(note);
        noteNameTextField.setText(note.getNoteName());
        noteTagsTextField.setText(note.getNoteTags());
    }


    private ListCell<Note> renderCell(ListView<Note> noteListView) {
        return new ListCell<>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                // If the cell is empty, set the text to null, otherwise set it to the contact's full name
                if (empty || note == null || note.getNoteName() == null) {
                    setText(null);
                } else {
                    setText(note.getNoteName());
                }
            }
        };
    }


    private void syncNotes() {
        noteListView.getItems().clear();
        noteListView.getItems().addAll(noteDAO.getAllNotes());
    }

    @FXML
    public void initialize() {
        noteListView.setCellFactory(this::renderCell);
        syncNotes();

        // Add listener for selection changes
        noteListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectNote(newSelection);
            }
        });
    }

    @FXML
    private void onEditConfirm() {
        // Get the selected contact from the list view
        Note selectedNote = noteListView.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            selectedNote.setNoteName(noteNameTextField.getText());
            selectedNote.setNoteTags(noteTagsTextField.getText());

            noteDAO.updateNote(selectedNote);
            syncNotes();
        }
    }

    @FXML
    private void onDelete() {
        // Get the selected contact from the list view
        Note selectedNote = noteListView.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            noteDAO.deleteNote(selectedNote);
            syncNotes();
        }
    }

    @FXML
    private void onAdd() {
        // Default values for a new contact
        final String DEFAULT_NOTE_NAME = "New Note";
        final String DEFAULT_NOTE_TAGS = "Tags";

        Note newNote = new Note(DEFAULT_NOTE_NAME, DEFAULT_NOTE_TAGS);
        // Add the new contact to the database
        noteDAO.addNote(newNote);
        syncNotes();
        // Select the new contact in the list view
        // and focus the first name text field
        selectNote(newNote);
        noteNameTextField.requestFocus();
    }

    @FXML
    private void onCancel() {
        // Find the selected contact
        Note selectedNote = noteListView.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            // Since the contact hasn't been modified,
            // we can just re-select it to refresh the text fields
            selectNote(selectedNote);
        }
    }
}




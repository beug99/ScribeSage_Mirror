package com.example.addressbook.controller;

import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.*;
import javafx.application.Platform;
import com.example.addressbook.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.TextField;
import javafx.collections.transformation.FilteredList;


public class HomePageController {

    @FXML
    private TextField searchField;
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
    private TreeView<String> folderTreeView;
    @FXML
    private Button createFolderButton;

    @FXML
    private MouseEvent mouseEvent;

    private INoteDAO noteDAO;
    private ObservableList<Note> notesObservableList;
    private ObservableList<String> noteNamesObservableList;
    private Note selectedNote;

    // folder lists
    private List<Folder> folderList = new ArrayList<>();
    private IFolderDAO folderDAO;

    public HomePageController() {
        noteDAO = new SqliteNoteDAO();
        folderDAO = new SqliteFolderDAO();
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
                // only hide the menu if it's open and the click is outside the menu and profile bar
                if (navMenu.isVisible() &&
                        !navMenu.localToScene(navMenu.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY()) &&
                        !profileBar.localToScene(profileBar.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())) {
                    navMenu.setVisible(false); // make the navigation bar hidden by default
                }
            });
        });

        // handler for drag and drop on folders
        notesListView.setOnDragDetected(event -> {
            String selectedNoteName = notesListView.getSelectionModel().getSelectedItem();
            if (selectedNoteName != null) {
                for (Note note : notesObservableList) {
                    if (note.getNoteName().equals(selectedNoteName)) {
                        Dragboard db = notesListView.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent content = new ClipboardContent();
                        content.putString(String.valueOf(note.getId())); // Use note ID
                        db.setContent(content);
                        event.consume();
                        break;
                    }
                }
            }
        });

        folderTreeView.setOnDragOver(event -> {
            if (event.getGestureSource() != folderTreeView && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        folderTreeView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                try {
                    int noteId = Integer.parseInt(db.getString());
                    Note note = noteDAO.getNoteById(noteId);
                    TreeItem<String> targetItem = folderTreeView.getSelectionModel().getSelectedItem();
                    if (targetItem != null) {
                        String folderName = targetItem.getValue();
                        Folder targetFolder = null;
                        for (Folder folder : folderList) {
                            if (folder.getFolderName().equals(folderName)) {
                                targetFolder = folder;
                                break;
                            }
                        }
                        if (note != null && targetFolder != null) {
                            addNoteToFolder(note, targetFolder);
                            populateFolderTreeView();
                            success = true;
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    private void onUpdateDetails() throws IOException {
        Stage stage = (Stage) updateDetailsLabel.getScene().getWindow();
        SceneLoader.switchScene(stage, "updateDetails-view.fxml", false);
    }

    @FXML
    private void onLogOut() throws IOException {
        Stage stage = (Stage) updateDetailsLabel.getScene().getWindow();
        SceneLoader.switchScene(stage, "login-view.fxml", false);
    }

    // loads a selected note from the LoadNote listener
    @FXML
    private void onLoadNote() throws IOException {
        if (selectedNote != null) {
            Stage stage = (Stage) notesListView.getScene().getWindow();
            NewNoteController noteController = SceneLoader.switchScene(stage, "new-note-view.fxml", true);

            noteController.setCurrentNote(selectedNote);
            noteController.setLabelText(selectedNote.getNoteName());
        // if no note selected
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

    @FXML
    public void toggleNavMenu(javafx.scene.input.MouseEvent mouseEvent) {
        navMenu.setVisible(!navMenu.isVisible());
    }

    @FXML
    private void onCreateNew(javafx.event.ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) createNewButton.getScene().getWindow();
        SceneLoader.switchScene(stage, "create-note-view.fxml", false);
    }

    // populating folder views
    private void populateFolderTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Folders");
        rootItem.setExpanded(true);

        for (Folder folder : folderList) {
            TreeItem<String> folderItem = new TreeItem<>(folder.getFolderName());
            if (folder.getNotes() != null) {
                for (Note note : folder.getNotes()) {
                    folderItem.getChildren().add(new TreeItem<>(note.getNoteName()));
                }
            }
            rootItem.getChildren().add(folderItem);
        }
        folderTreeView.setRoot(rootItem);
        folderTreeView.setShowRoot(false); // optional
    }

    // Create Folder
    @FXML
    private void onCreateFolder(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Folder");
        dialog.setHeaderText("Enter folder name:");
        dialog.setContentText("Folder name:");

        dialog.showAndWait().ifPresent(folderName -> {
            String email = Session.getLoggedInEmail();
            createFolder(folderName, email);
            populateFolderTreeView();
        });
    }

    private void createFolder(String folderName, String email) {
        // input validation
        if (folderName == null || folderName.trim().isEmpty()) return;
        // duplicate checks
        for (Folder folder : folderList) {
            if (folder.getFolderName().equalsIgnoreCase(folderName.trim()))
            return;
        }
        // folder creation
        Folder newFolder = new Folder(folderName.trim());
        // db & local updates
        newFolder.setEmail(Session.getLoggedInEmail());
        folderDAO.addFolder(newFolder);
        folderList.add(newFolder);
    }

    // add note to folder
    public void addNoteToFolder(Note note, Folder folder) {
        folder.getNotes().add(note);
        note.setFolderId(folder.getFolderId());
        noteDAO.updateNote(note);
    }

    @FXML
    private void onSortAlphabetically(ActionEvent event) {
        // Example logic: sort notes alphabetically
        if (noteNamesObservableList != null) {
            FXCollections.sort(noteNamesObservableList);
        }
    }


    @FXML
    private void onSearchNote() {
        String keyword = searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            // Reset the list
            noteNamesObservableList.clear();
            for (Note note : notesObservableList) {
                noteNamesObservableList.add(note.getNoteName());
            }
        } else {
            // Filter based on keyword
            noteNamesObservableList.clear();
            for (Note note : notesObservableList) {
                if (note.getNoteName().toLowerCase().contains(keyword)) {
                    noteNamesObservableList.add(note.getNoteName());
                }
            }
        }
        notesListView.setItems(noteNamesObservableList);
    }



}
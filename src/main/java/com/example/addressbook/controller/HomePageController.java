package com.example.addressbook.controller;

import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.*;
import com.example.addressbook.service.NoteService;
import javafx.application.Platform;
import com.example.addressbook.Session;
import javafx.event.ActionEvent;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import javafx.scene.control.TextField;


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
    private Label nameLabel;
    @FXML
    private TreeView<String> notesTreeView;
    @FXML
    private Button createFolderButton;

    private INoteDAO noteDAO;
    private List<Note> userNotes;
    private TreeItem<String> selectedItem;

    // folder lists
    private List<Folder> folderList = new ArrayList<>();
    private IFolderDAO folderDAO;

    // Constants for the tree structure
    private static final String ALL_NOTES_NODE = "All Notes";
    private static final String FOLDERS_NODE = "Folders";

    public HomePageController() {
        noteDAO = new SqliteNoteDAO();
        folderDAO = new SqliteFolderDAO();
    }

    public void initialize() {
        String fullName = Session.getFirstName() + " " + Session.getLastName();
        nameLabel.setText(fullName);

        loadUserData();
        populateNotesTreeView();

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

        // Add event handler for selections in the tree view
        notesTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedItem = newValue;
                handleTreeSelection(newValue);
            }
        });

        // Setup drag and drop functionality
        setupDragAndDrop();
    }

    private void setupDragAndDrop() {
        notesTreeView.setOnDragDetected(event -> {
            TreeItem<String> selected = notesTreeView.getSelectionModel().getSelectedItem();
            if (selected != null && isNoteItem(selected)) {
                // Find the note
                Note note = findNoteByName(selected.getValue());
                if (note != null) {
                    Dragboard db = notesTreeView.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(String.valueOf(note.getId()));
                    db.setContent(content);
                    event.consume();
                }
            }
        });

        notesTreeView.setOnDragOver(event -> {
            if (event.getGestureSource() != notesTreeView && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            } else if (event.getGestureSource() == notesTreeView && event.getDragboard().hasString()) {
                TreeItem<String> target = notesTreeView.getSelectionModel().getSelectedItem();
                if (target != null && (isFolderItem(target) || FOLDERS_NODE.equals(target.getValue()))) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
            event.consume();
        });

        notesTreeView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                try {
                    int noteId = Integer.parseInt(db.getString());
                    Note note = noteDAO.getNoteById(noteId);
                    TreeItem<String> targetItem = getDropTarget(notesTreeView.getSelectionModel().getSelectedItem());

                    if (note != null && targetItem != null) {
                        String targetValue = targetItem.getValue();
                        if (FOLDERS_NODE.equals(targetValue)) {
                            // Dropped on "Folders" node - remove from any folder
                            note.setFolderId(null);
                            noteDAO.updateNote(note);
                            success = true;
                        } else {
                            // Find the target folder
                            Folder targetFolder = findFolderByName(targetValue);
                            if (targetFolder != null) {
                                addNoteToFolder(note, targetFolder);
                                success = true;
                            }
                        }

                        if (success) {
                            loadUserData();
                            populateNotesTreeView();
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

    private TreeItem<String> getDropTarget(TreeItem<String> item) {
        if (item == null) return null;

        // If this is a folder node or the Folders root, return it
        if (isFolderItem(item) || FOLDERS_NODE.equals(item.getValue())) {
            return item;
        }

        // If this is a note within a folder, return its parent folder
        TreeItem<String> parent = item.getParent();
        if (parent != null && isFolderItem(parent)) {
            return parent;
        }

        return null;
    }

    private boolean isNoteItem(TreeItem<String> item) {
        if (item == null) return false;

        // It's a note if its parent is "All Notes" or a folder
        TreeItem<String> parent = item.getParent();
        if (parent != null) {
            return ALL_NOTES_NODE.equals(parent.getValue()) || isFolderItem(parent);
        }
        return false;
    }

    private boolean isFolderItem(TreeItem<String> item) {
        if (item == null) return false;

        // It's a folder if its parent is "Folders"
        TreeItem<String> parent = item.getParent();
        if (parent != null) {
            return FOLDERS_NODE.equals(parent.getValue());
        }
        return false;
    }

    private void handleTreeSelection(TreeItem<String> item) {
        if (item == null) return;

        // Clear current selection
        NoteService.selectedNote = null;

        // If this is a note item
        if (isNoteItem(item)) {
            String noteName = item.getValue();
            Note note = findNoteByName(noteName);
            if (note != null) {
                NoteService.setSelectedNote(note);
            }
        }
    }

    private Note findNoteByName(String noteName) {
        for (Note note : userNotes) {
            if (note.getNoteName().equals(noteName)) {
                return note;
            }
        }
        return null;
    }

    private Folder findFolderByName(String folderName) {
        for (Folder folder : folderList) {
            if (folder.getFolderName().equals(folderName)) {
                return folder;
            }
        }
        return null;
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

    // loads a selected note from the tree view
    @FXML
    private void onLoadNote() throws IOException {
        if (NoteService.selectedNote != null) {
            Stage stage = (Stage) notesTreeView.getScene().getWindow();
            NewNoteController noteController = SceneLoader.switchScene(stage, "new-note-view.fxml", true);

            noteController.setCurrentNote(NoteService.selectedNote);
            noteController.setLabelText(NoteService.selectedNote.getNoteName());
        } else {
            System.out.println("No note selected to load.");
        }
    }

    // deletes the selected note
    @FXML
    private void onDeleteItem() throws IOException {
        if (NoteService.selectedNote != null) {
            noteDAO.deleteNote(NoteService.selectedNote);
            loadUserData();
            populateNotesTreeView();
        } else if (selectedItem != null && isFolderItem(selectedItem)) {
            // If a folder is selected, prompt to delete it
            Folder folder = findFolderByName(selectedItem.getValue());
            if (folder != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Folder");
                alert.setHeaderText("Delete folder: " + folder.getFolderName());
                alert.setContentText("Are you sure you want to delete this folder? The notes inside will not be deleted.");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // Remove folder association from notes
                        for (Note note : userNotes) {
                            if (note.getFolderId() != null && note.getFolderId().equals(folder.getFolderId())) {
                                note.setFolderId(null);
                                noteDAO.updateNote(note);
                            }
                        }
                        folderDAO.deleteFolder(folder);

                        loadUserData();
                        populateNotesTreeView();
                    }
                });
            }
        } else {
            System.out.println("No note or folder selected to delete.");
        }
    }

    @FXML
    private void handleDeleteFolder(ActionEvent event) {
        // Get the selected item from the tree view
        TreeItem<String> selectedItem = notesTreeView.getSelectionModel().getSelectedItem();

        // Check if a folder is selected
        if (selectedItem != null && isFolderItem(selectedItem)) {
            // Find the folder
            Folder folder = findFolderByName(selectedItem.getValue());
            if (folder != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Folder");
                alert.setHeaderText("Delete folder: " + folder.getFolderName());
                alert.setContentText("Are you sure you want to delete this folder? The notes inside will not be deleted.");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // Remove folder association from notes
                        for (Note note : userNotes) {
                            if (note.getFolderId() != null && note.getFolderId().equals(folder.getFolderId())) {
                                note.setFolderId(null);
                                noteDAO.updateNote(note);
                            }
                        }
                        folderDAO.deleteFolder(folder);

                        loadUserData();
                        populateNotesTreeView();
                    }
                });
            }
        } else {
            // Show alert if no folder is selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Folder Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a folder to delete.");
            alert.showAndWait();
        }
    }

    // Load user data (notes and folders)
    private void loadUserData() {
        loadUserNotes();
        loadUserFolders();
    }

    // Load user notes
    private void loadUserNotes() {
        try {
            userNotes = noteDAO.getNotesByOwner(Session.getLoggedInEmail());
        } catch (Exception e) {
            e.printStackTrace();
            userNotes = new ArrayList<>();
        }
    }

    private void loadUserFolders() {
        try {
            // Clear existing folder list
            folderList.clear();

            // Load folders for the current user
            List<Folder> userFolders = folderDAO.getFolderByEmail(Session.getLoggedInEmail());
            folderList.addAll(userFolders);

            // For each folder, identify its notes
            for (Folder folder : folderList) {
                List<Note> notesInFolder = new ArrayList<>();

                // Find notes that belong to this folder
                for (Note note : userNotes) {
                    if (note.getFolderId() != null && note.getFolderId().equals(folder.getFolderId())) {
                        notesInFolder.add(note);
                    }
                }

                folder.setNotes(notesInFolder);
            }
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

    // Populate the unified tree view
    private void populateNotesTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Root");
        rootItem.setExpanded(true);

        // Create "All Notes" node
        TreeItem<String> allNotesNode = new TreeItem<>(ALL_NOTES_NODE);
        allNotesNode.setExpanded(true);

        // Add all user notes to the "All Notes" node
        for (Note note : userNotes) {
            allNotesNode.getChildren().add(new TreeItem<>(note.getNoteName()));
        }

        // Create "Folders" node
        TreeItem<String> foldersNode = new TreeItem<>(FOLDERS_NODE);
        foldersNode.setExpanded(true);

        // Add each folder and its notes
        for (Folder folder : folderList) {
            TreeItem<String> folderNode = new TreeItem<>(folder.getFolderName());

            // Add notes that belong to this folder
            if (folder.getNotes() != null) {
                for (Note note : folder.getNotes()) {
                    folderNode.getChildren().add(new TreeItem<>(note.getNoteName()));
                }
            }

            foldersNode.getChildren().add(folderNode);
        }

        // Add nodes to root
        rootItem.getChildren().add(allNotesNode);
        rootItem.getChildren().add(foldersNode);

        // Set the root and hide it
        notesTreeView.setRoot(rootItem);
        notesTreeView.setShowRoot(false);
    }

    // Create Folder
    @FXML
    private void onCreateFolder(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Folder");
        dialog.setHeaderText("Enter folder name:");
        dialog.setContentText("Folder name:");

        dialog.showAndWait().ifPresent(folderName -> {
            if (createFolder(folderName, Session.getLoggedInEmail())) {
                loadUserData();
                populateNotesTreeView();
            }
        });
    }

    private boolean createFolder(String folderName, String email) {
        // input validation
        if (folderName == null || folderName.trim().isEmpty()) return false;

        // duplicate checks
        for (Folder folder : folderList) {
            if (folder.getFolderName().equalsIgnoreCase(folderName.trim()))
                return false;
        }

        // folder creation
        Folder newFolder = new Folder(folderName.trim());
        newFolder.setEmail(email);
        newFolder.setNotes(new ArrayList<>());
        folderDAO.addFolder(newFolder);
        folderList.add(newFolder);
        return true;
    }

    // add note to folder
    public void addNoteToFolder(Note note, Folder folder) {
        if (folder.getNotes() == null) {
            folder.setNotes(new ArrayList<>());
        }

        // Check if the note is already in the folder
        boolean alreadyInFolder = false;
        for (Note n : folder.getNotes()) {
            if (n.getId() == note.getId()) {
                alreadyInFolder = true;
                break;
            }
        }

        if (!alreadyInFolder) {
            folder.getNotes().add(note);
        }

        note.setFolderId(folder.getFolderId());
        noteDAO.updateNote(note);
    }

    @FXML
    private void onSortAlphabetically(ActionEvent event) {
        // This will need to be updated to sort the tree view
        loadUserData();

        // Sort the notes list
        userNotes.sort(Comparator.comparing(Note::getNoteName));

        // Sort folders
        folderList.sort(Comparator.comparing(Folder::getFolderName));

        // Sort notes within folders
        for (Folder folder : folderList) {
            if (folder.getNotes() != null) {
                folder.getNotes().sort(Comparator.comparing(Note::getNoteName));
            }
        }

        populateNotesTreeView();
    }

    @FXML
    private void onSearchNote() {
        String keyword = searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            // Reset the tree view
            loadUserData();
            populateNotesTreeView();
        } else {
            // Filter notes based on keyword
            List<Note> filteredNotes = new ArrayList<>();
            for (Note note : userNotes) {
                if (note.getNoteName().toLowerCase().contains(keyword)) {
                    filteredNotes.add(note);
                }
            }

            // Create a filtered tree view
            TreeItem<String> rootItem = new TreeItem<>("Root");
            rootItem.setExpanded(true);

            // Create search results node
            TreeItem<String> searchResultsNode = new TreeItem<>("Search Results");
            searchResultsNode.setExpanded(true);

            // Add matching notes to search results
            for (Note note : filteredNotes) {
                searchResultsNode.getChildren().add(new TreeItem<>(note.getNoteName()));
            }

            // Add nodes to root
            rootItem.getChildren().add(searchResultsNode);

            // Set the root and hide it
            notesTreeView.setRoot(rootItem);
            notesTreeView.setShowRoot(false);
        }
    }
}
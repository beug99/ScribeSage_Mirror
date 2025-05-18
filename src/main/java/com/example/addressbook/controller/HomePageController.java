package com.example.addressbook.controller;

import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.*;
import com.example.addressbook.service.FolderService;
import com.example.addressbook.service.NoteService;
import javafx.application.Platform;
import com.example.addressbook.Session;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

import javafx.scene.control.TextField;

import static com.example.addressbook.service.NoteService.notesTreeView;

/**
 * A controller class that manages the Home Page of the application once a user has signed in.
 * Upon logging in, the scene is set to homepage-view.fxml. User specific details, notes and
 * folders are displayed on the page. The user can perform further functions by interacting
 * with the user interface.
 */
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

    private INoteDAO noteDAO;
    private List<Note> userNotes;
    private TreeItem<String> selectedItem;

    // folder lists
    private List<Folder> folderList = new ArrayList<>();
    private IFolderDAO folderDAO;

    /**
     * Constructs an instance of both a note and a folder Data Access Object ({@link INoteDAO} and
     * {@link IFolderDAO}).
     */
    public HomePageController() {
        noteDAO = new SqliteNoteDAO();
        folderDAO = new SqliteFolderDAO();
    }

    /**
     * The Initialize method retrieves the name, existing notes and folders created by the current
     * session user from the database and populates the UI. It also loads the listener functions for the
     * navigation menu and the note/folder selection.
     */
    public void initialize() {
        String fullName = Session.getFirstName() + " " + Session.getLastName();
        nameLabel.setText(fullName);

        // initialise services
        FolderService.initialise(folderList, folderDAO);
        NoteService.initialize(new ArrayList<>(), folderList, notesTreeView);
        NoteService.noteDAO = noteDAO;

        // load user data
        NoteService.loadUserNotes();
        FolderService.loadUserFolders();
        this.userNotes = NoteService.getUserNotes();
        // setup ui
        NoteService.populateNotesTreeView();
        NoteService.setupDateCellFactory();
        // setup context menu for notes
        setupContextMenus();

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
                NoteService.handleTreeSelection(newValue);
            }
        });
    }

    /**
     * This method //TODO -- JUST CHECKING WHAT THIS DOES AND IF IT WORKS
     */
    private void setupContextMenus() {
        ContextMenu folderMenu = new ContextMenu();
        // for notes, add a move to folder option
        ContextMenu noteMenu = new ContextMenu();
        Menu moveToFolder = new Menu("Move to folder");
        MenuItem removeFromFolder = new MenuItem("Remove from folder");

        notesTreeView.setOnContextMenuRequested(event -> {
            TreeItem<String> item = notesTreeView.getSelectionModel().getSelectedItem();
            if (item != null) {
                if (FolderService.isFolderItem(item)) {
                    folderMenu.show(notesTreeView, event.getScreenX(), event.getSceneY());
                } else if (NoteService.isNoteItem(item)) {
                    // clear previous menu items
                    moveToFolder.getItems().clear();

                    // add each folder as options
                    for (Folder folder : FolderService.getFolderList()) {
                        MenuItem folderItem = new MenuItem(folder.getFolderName());
                        folderItem.setOnAction(actionEvent -> {
                            Note note = findNoteByName(item.getValue());
                            if (note != null) {
                                FolderService.addNoteToFolder(note, folder);
                                NoteService.loadUserData();
                                NoteService.populateNotesTreeView();
                            }
                        });
                        moveToFolder.getItems().add(folderItem);
                    }

                    // add menu items to context menu
                    noteMenu.getItems().clear();
                    if (!moveToFolder.getItems().isEmpty()) {
                        noteMenu.getItems().add(moveToFolder);
                    }

                    // option to remove note from folder
                    removeFromFolder.setOnAction(actionEvent -> {
                        Note note = findNoteByName(item.getValue());
                        if (note != null) {
                            note.setFolderId(null);
                            noteDAO.updateNote(note);
                            NoteService.loadUserData();
                            NoteService.populateNotesTreeView();
                        }
                    });
                    noteMenu.getItems().add(removeFromFolder);

                    noteMenu.show(notesTreeView, event.getScreenX(), event.getSceneY());
                }
            }
        });
    }

    /**
     * This method takes the user string input and searches the database for an instance of
     * a note with the same name.
     * @param noteName The search query entered by the user.
     * @return Either the note if found, or null.
     */
    private Note findNoteByName(String noteName) {
        for (Note note : userNotes) {
            if (note.getNoteName().equals(noteName)) {
                return note;
            }
        }
        return null;
    }

    /**
     * This method will load the Update Details {@link UpdateDetailsController} page. When the
     * 'Update Details' button is clicked, the updateDetails-view.fxml will display.
     * @throws IOException An error will occur or the page will not load.
     */
    @FXML
    private void onUpdateDetails() throws IOException {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        SceneLoader.switchScene(popupStage, "updateDetails-view.fxml", false);
    }

    /**
     * This method will log the user out of the application. When the 'Logout' button is clicked,
     * the user will be taken back to the Log-In page {@link LoginController}.
     * @throws IOException An error will appear or the page will not load.
     */
    @FXML
    private void onLogOut() throws IOException {
        Stage stage = (Stage) updateDetailsLabel.getScene().getWindow();
        SceneLoader.switchScene(stage, "login-view.fxml", false);
    }

    /**
     * This method will open and display the selected note when the 'Open Note' button is
     * clicked. If no note is selected, an alert will appear to inform the user.
     * @throws IOException An error will occur or the selected note will not open.
     */
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

    /**
     * This method will delete the selected note or folder from the database when the 'Delete'
     * button is clicked. If no note or folder is selected, an alert will appear to inform the user.
     * @throws IOException An error will occur or the selected object will not be deleted.
     */
    @FXML
    private void onDeleteItem() throws IOException {
        if (NoteService.selectedNote != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Note");
            alert.setHeaderText("Delete Note: " + selectedItem.getValue());
            alert.setContentText("Are you sure you want to delete this note? This is irreversible.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Remove folder association from notes
                    for (Note note : userNotes) {
                        if (note.getNoteName() != null && note.getNoteName().equals(selectedItem.getValue())) {
                            note.setFolderId(null);
                            noteDAO.updateNote(note);
                        }
                    }
                    noteDAO.deleteNote(NoteService.selectedNote);

                    NoteService.loadUserData();
                    NoteService.populateNotesTreeView();
                }
            });
        } else if (selectedItem != null && FolderService.isFolderItem(selectedItem)) {
            // If a folder is selected, prompt to delete it
            Folder folder = FolderService.findFolderByName(selectedItem.getValue());
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

                        NoteService.loadUserData();
                        NoteService.populateNotesTreeView();
                    }
                });
            }
        } else {
            System.out.println("No note or folder selected to delete.");
        }
    }

    /**
     * This method will display the navigation menu when the Logo is clicked.
     * @param mouseEvent A mouse click on the logo.
     */
    @FXML
    public void toggleNavMenu(javafx.scene.input.MouseEvent mouseEvent) {
        navMenu.setVisible(!navMenu.isVisible());
    }

    /**
     * This method will load the Create Note page {@link CreateNoteController} when the 'New Note'
     * button is clicked.
     * @param actionEvent A mouse click on the 'New Note' Button.
     * @throws IOException An error will occur or the Create Note page will not load.
     */
    @FXML
    private void onCreateNew(javafx.event.ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) createNewButton.getScene().getWindow();
        SceneLoader.switchScene(stage, "create-note-view.fxml", false);
    }

    /**
     * This method will display a dialog prompting users to enter a new folder name. The user
     * can then cancel or click OK which will create and save a named folder to the database.
     * @param event Clicking the 'New Folder' button.
     */
    @FXML
    private void onCreateFolder(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Folder");
        dialog.setHeaderText("Enter folder name:");
        dialog.setContentText("Folder name:");

        dialog.showAndWait().ifPresent(folderName -> {
            if (FolderService.createFolder(folderName, Session.getLoggedInEmail())) {
                NoteService.loadUserData();
                NoteService.populateNotesTreeView();
            }
        });
    }

    /**
     * This method sorts the existing notes and folders into alphabetical
     * order when the 'Sort Alphabetically' button is clicked.
     * @param event The 'Sort Alphabetically' button is clicked.
     */
    @FXML
    private void onSortAlphabetically(ActionEvent event) {
        // This will need to be updated to sort the tree view
        NoteService.loadUserData();

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
        NoteService.populateNotesTreeView();
    }

    /**
     * This method takes the user input and searches the notes within the database
     * for any note that contains the input. The notes that contain the text will be displayed.
     * //TODO Search function didn't work for me, will test.
     */
    @FXML
    private void onSearchNote() {
        String keyword = searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            // Reset the tree view
            NoteService.loadUserData();
            NoteService.populateNotesTreeView();
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
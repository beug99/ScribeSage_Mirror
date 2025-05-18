package com.example.addressbook.controller;
import com.example.addressbook.Session;
import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.*;
import com.example.addressbook.service.FolderService;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A controller class that manages the creation of a new Note. Upon opening the page, the scene
 * is set to the create-note-view.fxml. Depending on the user input validity, the user is able to
 * create and name a note, add it to an existing folder, upload files to the note and add note tags.
 */
public class CreateNoteController {
    public Label folderName;
    public Button createNoteButton;
    public Button homeButton;
    public Button cancelButton;
    public String currentNote;
    private INoteDAO noteDAO;
    private IFolderDAO folderDAO;
    private Folder selectedFolder;
    private List<Folder> folderList;

    @FXML
    private TextField noteNameTextField;
    @FXML
    private TextField noteTagsTextField;


    /**
     * Constructs an instance of both a note and a folder Data Access Object (INoteDAO and IFolderDAO).
     * The available folders are loaded to the instance by accessing the existing folders through the
     * current logged-in users email.
     */
    public CreateNoteController() {
        noteDAO = new SqliteNoteDAO();
        folderDAO = new SqliteFolderDAO();
        folderList = folderDAO.getFolderByEmail(Session.getLoggedInEmail());
    }

    /**
     * A method which initializes the folder service, this loads the users existing folders which can
     * be selected when assigning the new note to a folder.
     */
    @FXML
    public void initialize() {
        // initialize folder service if not already initialized
        if (FolderService.getFolderList() == null || FolderService.getFolderList().isEmpty()) {
            FolderService.initialise(folderList, folderDAO);
            FolderService.loadUserFolders();
        }
    }

    /**
     * A method that creates a new note instance and save it to the database if the user input
     * variables are valid. The variables include the note name, chosen folder and note tags. If the
     * note is successfully created and saved, the method loads the New Note page. If unsuccessful,
     * the method will display errors accordingly.
     * @throws IOException An error will occur or the new note will note be created.
     */
    @FXML
    public void onCreateButtonClick() throws IOException {
        // validate note name
        if (noteNameTextField.getText() == null || noteNameTextField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Create Note Failed");
            alert.setHeaderText(null);
            alert.setContentText("You must give your note a name!");
            alert.showAndWait();
            return;
        }

        // get the folder ID if one was selected
        Integer folderId = null;
        if (folderName.getUserData() != null) {
            folderId = (Integer) folderName.getUserData();
        }

        // create the note with proper parameters
        Note newNote = new Note(
                noteNameTextField.getText(),
                noteTagsTextField.getText() != null ? noteTagsTextField.getText() : "",
                "Your Note",
                Session.getLoggedInEmail(),
                folderId
        );

        // save the note to the database
        noteDAO.addNote(newNote);
        currentNote = noteNameTextField.getText();

        Stage stage = (Stage) createNoteButton.getScene().getWindow();
        NewNoteController controller = SceneLoader.switchScene(stage, "new-note-view.fxml", true);
        controller.setLabelText(currentNote);
        controller.setCurrentNote(newNote);
    }

    /**
     * A method that loads the applications home page when the home button is clicked.
     * @throws IOException Page fails to load.
     */
    @FXML
    public void onHomeClick() throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        SceneLoader.switchScene(stage, "homepage-view.fxml", true);
    }

    /**
     * A method that cancels the creation of a new note and loads the home page if the cancel
     * button is clicked.
     * @throws IOException An error appears, the note creation hasn't cancelled or the home
     * page has not loaded.
     */
    @FXML
    public void onCancelButtonClick() throws IOException {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        SceneLoader.switchScene(stage, "homepage-view.fxml", true);
    }

    /**
     * A method that allows the user to choose a file to upload to the new note.
     * @param actionEvent The corresponding 'Upload' button is clicked.
     */
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
            // hndle files (copy, link to note, etc.)
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    /**
     * A method that allows the user to choose a folder to save the note to. If the user has not
     * created any folders yet, an alert advises them of this. If the user has created folders,
     * they will have a drop-down list of folders that they can choose from.
     * @param actionEvent The 'Choose Folder' button has been clicked.
     */
    public void onChangeFolderClick(ActionEvent actionEvent) {
        // make sure folders are loaded
        List<Folder> userFolders = folderDAO.getFolderByEmail(Session.getLoggedInEmail());

        if (userFolders == null || userFolders.isEmpty()) {
            // no folders available
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Folders Available");
            alert.setHeaderText(null);
            alert.setContentText("You don't have any folders yet. You can create folders from the home page.");
            alert.showAndWait();
            return;
        }

        // choice text for folder selection
        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("Select Folder");
        dialog.setHeaderText("Choose a folder for this note");
        dialog.setContentText("Folder:");

        // add no folder option
        dialog.getItems().add("No Folder");

        // add all user folders
        for (Folder folder : userFolders) {
            dialog.getItems().add(folder.getFolderName());
        }
        // default selection to "No Folder"
        dialog.setSelectedItem("No Folder");

        // show dialog and process result
        dialog.showAndWait().ifPresent(folderChoice -> {
            if (folderChoice.equals("No Folder")) {
                folderName.setText("No Folder Selected");
            } else {
                // find the selected folder
                Folder selectedFolder = null;
                for (Folder folder : userFolders) {
                    if (folder.getFolderName().equals(folderChoice)) {
                        selectedFolder = folder;
                        break;
                    }
                }
                if (selectedFolder != null) {
                    folderName.setText(selectedFolder.getFolderName());
                    // store folder ID for later use when creating the note
                    folderName.setUserData(selectedFolder.getFolderId());
                }
            }
        });
    }

    public void onTagsAddButton(ActionEvent actionEvent) {
        // Placeholder for future functionality
    }

    public void onAINoteSummariseClick(ActionEvent actionEvent) {
        // Placeholder for future functionality
    }

    public void onUploadSparseClick(ActionEvent actionEvent) {
        // Placeholder for future functionality
    }

}
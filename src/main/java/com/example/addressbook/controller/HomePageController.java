package com.example.addressbook.controller;

import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.*;
import com.example.addressbook.service.FolderService;
import com.example.addressbook.service.NoteService;
import com.lowagie.text.*;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.sun.source.tree.Tree;
import javafx.application.Platform;
import com.example.addressbook.Session;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.List;

import javafx.scene.control.TextField;

import static com.example.addressbook.service.NoteService.*;
import static com.lowagie.text.StandardFonts.HELVETICA;
import static com.lowagie.text.StandardFonts.HELVETICA_ITALIC;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

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
    private Map<String, Note> searchNoteMap = new HashMap<>();


    private INoteDAO noteDAO;
    private List<Note> userNotes;
    private TreeItem<String> selectedItem;

    // folder lists
    private List<Folder> folderList = new ArrayList<>();
    private IFolderDAO folderDAO;

    public HomePageController() {
        noteDAO = new SqliteNoteDAO();
        folderDAO = new SqliteFolderDAO();
    }

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

    private void setupContextMenus() {
        ContextMenu folderMenu = new ContextMenu();
        // for notes, add a move to folder option
        ContextMenu noteMenu = new ContextMenu();
        Menu moveToFolder = new Menu("Move to folder");
        MenuItem removeFromFolder = new MenuItem("Remove from folder");
        MenuItem deleteNote = new MenuItem("Delete note (NOT IMPLEMENTED)");
        MenuItem duplicateNote = new MenuItem("Duplicate note (NOT IMPLEMENTED)");
        MenuItem exportNote = new MenuItem("Export note");
        MenuItem renameNote = new MenuItem("Rename note");

        renameNote.setOnAction(event -> {
            TreeItem<String> item = notesTreeView.getSelectionModel().getSelectedItem();
            if (item != null && NoteService.isNoteItem(item)) {
                Note note = findNoteByName(item.getValue());
                if (note != null) {
                    showRenameDialog(note);
                }
            }
        });
        exportNote.setOnAction(event -> {
            TreeItem<String> item = notesTreeView.getSelectionModel().getSelectedItem();
            if (item != null && NoteService.isNoteItem(item)) {
                Note note = findNoteByName(item.getValue());
                if (note != null) {
                    showExportDialog(note);
                } else {
                    showAlert("Export Error", "Selected note not found.");
                }
            } else {
                showAlert("Export Error", "No valid note selected.");
            }
        });



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

                    noteMenu.getItems().add(renameNote);

                    // option to delete note from context menu
                    deleteNote.setOnAction(actionEvent -> {
                        System.out.println("Trying to delete note: " + selectedNote.getNoteName());
                        try {
                            onDeleteItem();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    noteMenu.getItems().add(deleteNote);

                    // Option to create note from copy of selected note
                    duplicateNote.setOnAction(actionEvent -> {
                        if (selectedNote != null) {
                            System.out.println("Trying to duplicate note: " + selectedNote.getNoteName());
                        }
                    });
                    noteMenu.getItems().add(duplicateNote);

                    noteMenu.getItems().add(exportNote);


                    noteMenu.show(notesTreeView, event.getScreenX(), event.getSceneY());

                }
            }
        });
    }

    private Note findNoteByName(String noteName) {
        for (Note note : userNotes) {
            if (note.getNoteName().equals(noteName)) {
                return note;
            }
        }
        return null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showRenameDialog(Note note) {
        TextInputDialog dialog = new TextInputDialog(note.getNoteName());
        dialog.setTitle("Rename Note");
        dialog.setHeaderText("Enter new note name:");
        dialog.setContentText("Note name:");

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                // update the note name
                note.setNoteName(newName.trim());
                noteDAO.updateNote(note);
                // refresh view
                NoteService.loadUserData();
                NoteService.populateNotesTreeView();
            }
        });
    }

    /**
     * Exports selected note into pdf format
     * @param note the selected note
     */
    private void showExportDialog(Note note) {
        try {
            // file chooser dialog box pops up when user selects the export option
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Note to PDF");
            fileChooser.setInitialFileName(note.getNoteName() + ".pdf");

            // add extension filter to set filechooser to pdf files
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extensionFilter);

            // show the save file dialog
            File file = fileChooser.showSaveDialog(notesTreeView.getScene().getWindow());

            if (file != null) {
                // call method to create the PDF
                exportNoteToPdf(note, file);

                // show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Successful");
                alert.setHeaderText(null);
                alert.setContentText("Note has been successfully exported");
                alert.showAndWait();
            }
        } catch (Exception e) {
            // show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to export note: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Creates a PDF file from the selected note
     * @param note The note to export
     * @param outputFile The file to save the PDF to
     */
    private void exportNoteToPdf(Note note, File outputFile) throws IOException, DocumentException {
        // create document with A4 size
        Document document = new Document(PageSize.A4);
        // create pdf
        PdfWriter.getInstance(document, new FileOutputStream(outputFile));

        // open doc
        document.open();

        // add data from note
        document.addTitle(note.getNoteName());
        document.addAuthor(note.getNoteOwner());
        document.addCreationDate();

        // Add title
        Paragraph title = new Paragraph(note.getNoteName(), HELVETICA.create());
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Add creation date if available
        if (note.getCreatedDate() != null) {
            Paragraph dateInfo = new Paragraph("Created on: " + note.getFormattedCreatedDate(), HELVETICA.create());
            dateInfo.setAlignment(Element.ALIGN_RIGHT);
            dateInfo.setSpacingAfter(15);
            document.add(dateInfo);
        }

        // Add separator line
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineWidth(0.5f);
        document.add(lineSeparator);
        document.add(Chunk.NEWLINE);

        // Determine if note text is HTML or plain text
        String noteText = note.getNoteText();
        if (isHtmlContent(noteText)) {
            // Parse HTML content
            HTMLWorker htmlWorker = new HTMLWorker(document);
            htmlWorker.parse(new StringReader(noteText));
        } else {
            // Add plain text content
            Paragraph content = new Paragraph(noteText, HELVETICA.create());
            content.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(content);
        }

        // Add tags if available
        if (note.getNoteTags() != null && !note.getNoteTags().isEmpty()) {
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            Paragraph tags = new Paragraph();
            tags.setAlignment(Element.ALIGN_LEFT);
            document.add(tags);
        }

        // Close document
        document.close();
    }

    /**
     * Determines if the content is HTML
     * @param content The content to check
     * @return true if content appears to be HTML
     */
    private boolean isHtmlContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        String trimmedContent = content.trim().toLowerCase();
        return trimmedContent.contains("<html") ||
                trimmedContent.contains("<p>") ||
                trimmedContent.contains("<div") ||
                trimmedContent.contains("<body");
    }

    @FXML
    private void onUpdateDetails() throws IOException {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        SceneLoader.switchScene(popupStage, "updateDetails-view.fxml", false);
    }

    @FXML
    private void onLogOut() throws IOException {
        Stage stage = (Stage) updateDetailsLabel.getScene().getWindow();
        SceneLoader.switchScene(stage, "login-view.fxml", false);
    }

    // loads a selected note from the tree view
    @FXML
    private void onLoadNote() throws IOException {
        TreeItem<String> selectedItem = notesTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String selectedName = selectedItem.getValue();
            if (searchNoteMap.containsKey(selectedName)) {
                NoteService.selectedNote = searchNoteMap.get(selectedName);  // from search
            }
        }

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
            NoteService.loadUserData();
            NoteService.populateNotesTreeView();
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

    @FXML
    public void toggleNavMenu(javafx.scene.input.MouseEvent mouseEvent) {
        navMenu.setVisible(!navMenu.isVisible());
    }

    @FXML
    private void onCreateNew(javafx.event.ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) createNewButton.getScene().getWindow();
        SceneLoader.switchScene(stage, "create-note-view.fxml", false);
    }

    // Create Folder
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

    @FXML
    private void onSearchNote() {
        String keyword = searchField.getText().toLowerCase().trim();

        // Reset search
        if (keyword.isEmpty()) {
            searchNoteMap.clear();  // clear previous search mappings
            NoteService.loadUserData();
            NoteService.populateNotesTreeView();
            return;
        }

        // Prepare new filtered notes and folders
        searchNoteMap.clear();
        List<Note> filteredNotes = new ArrayList<>();
        for (Note note : userNotes) {
            if (note.getNoteName().toLowerCase().contains(keyword)) {
                filteredNotes.add(note);
                searchNoteMap.put(note.getNoteName(), note); // store for click handling
            }
        }

        List<Folder> filteredFolders = new ArrayList<>();
        for (Folder folder : folderList) {
            if (folder.getFolderName().toLowerCase().contains(keyword)) {
                filteredFolders.add(folder);
            }
        }

        // Build TreeView
        TreeItem<String> rootItem = new TreeItem<>("Root");
        rootItem.setExpanded(true);

        TreeItem<String> searchResults = new TreeItem<>("Search Results");
        searchResults.setExpanded(true);

        // Folders (expanded by default)
        if (!filteredFolders.isEmpty()) {
            TreeItem<String> folderNode = new TreeItem<>("Folders");
            folderNode.setExpanded(true);
            for (Folder folder : filteredFolders) {
                folderNode.getChildren().add(new TreeItem<>(folder.getFolderName()));
            }
            searchResults.getChildren().add(folderNode);
        }

        // Notes (expanded by default)
        if (!filteredNotes.isEmpty()) {
            TreeItem<String> noteNode = new TreeItem<>("Notes");
            noteNode.setExpanded(true);
            for (Note note : filteredNotes) {
                TreeItem<String> noteItem = new TreeItem<>(note.getNoteName());
                noteNode.getChildren().add(noteItem);
            }
            searchResults.getChildren().add(noteNode);
        }

        rootItem.getChildren().add(searchResults);
        notesTreeView.setRoot(rootItem);
        notesTreeView.setShowRoot(false);
    }

    @FXML
    private void onSortByDate(ActionEvent event) {
        System.out.println("Sort by date clicked!");
        // TODO: Add actual sorting logic here
    }

}
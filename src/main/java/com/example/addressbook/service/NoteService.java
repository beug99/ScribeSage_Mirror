package com.example.addressbook.service;

import com.example.addressbook.Session;
import com.example.addressbook.controller.HomePageController;
import com.example.addressbook.model.Folder;
import com.example.addressbook.model.INoteDAO;
import com.example.addressbook.model.Note;
import com.example.addressbook.model.SqliteNoteDAO;
import com.sun.source.tree.Tree;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.util.ArrayList;
import java.util.List;

import static com.example.addressbook.service.FolderService.*;

public class NoteService {

    public static Note selectedNote;
    public static INoteDAO noteDAO = new SqliteNoteDAO();
    private static List<Note> userNotes = new ArrayList<>();
    private static List<Folder> folderList = new ArrayList<>();
    private static final String ALL_NOTES_NODE = "All Notes";
    private static TreeView<String> notesTreeView;

    public static void initialize(List<Note> notes, List<Folder> folders, TreeView<String> treeView) {
        notesTreeView = treeView;
        userNotes = notes;
        folderList = folders;

    }
    public static List<Note> getUserNotes() {
        return userNotes;
    }

    public static List<Folder> getFolderList() {
        return folderList;
    }

    public static void setSelectedNote(Note note) {
        selectedNote = note;
        System.out.println("Selected note: " + note.getNoteName() + " ID:" + note.getId() + " Owner: " + note.getNoteOwner());
    }

    public static void setupDragAndDrop() {
        if (notesTreeView == null){
            System.err.println("Treeview not initialised in Noteservice");
            return;
        }
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
                            Folder targetFolder = FolderService.findFolderByName(targetValue);
                            if (targetFolder != null) {
                                FolderService.addNoteToFolder(note, targetFolder);
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

    public static boolean isNoteItem(TreeItem<String> item) {
        if (item == null) return false;

        // It's a note if its parent is "All Notes" or a folder
        TreeItem<String> parent = item.getParent();
        if (parent != null) {
            return ALL_NOTES_NODE.equals(parent.getValue()) || isFolderItem(parent);
        }
        return false;
    }

    private static Note findNoteByName(String noteName) {
        for (Note note : getUserNotes()) {
            if (note.getNoteName().equals(noteName)) {
                return note;
            }
        }
        return null;
    }

    private static TreeItem<String> getDropTarget(TreeItem<String> item) {
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

    public static void loadUserData() {
        loadUserNotes();
        FolderService.loadUserFolders();
        userNotes = NoteService.getUserNotes();
    }

    public static void loadUserNotes() {
        try {
            userNotes = getNoteDAO().getNotesByOwner(Session.getLoggedInEmail());
        } catch (Exception e) {
            e.printStackTrace();
            userNotes = new ArrayList<>();
        }
    }

    public static void populateNotesTreeView() {
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

    public static INoteDAO getNoteDAO() {
        return noteDAO;
    }

    public static void handleTreeSelection(TreeItem<String> item) {
        if (item == null) return;

        // Clear current selection
        NoteService.selectedNote = null;

        // If this is a note item
        if (NoteService.isNoteItem(item)) {
            String noteName = item.getValue();
            Note note = findNoteByName(noteName);
            if (note != null) {
                NoteService.setSelectedNote(note);
            }
        }
    }
}

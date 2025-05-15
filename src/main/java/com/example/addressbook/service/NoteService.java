package com.example.addressbook.service;

import com.example.addressbook.Session;
import com.example.addressbook.model.Folder;
import com.example.addressbook.model.INoteDAO;
import com.example.addressbook.model.Note;
import com.example.addressbook.model.SqliteNoteDAO;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;

import java.util.ArrayList;
import java.util.List;

import static com.example.addressbook.service.FolderService.*;

public class NoteService {

    public static Note selectedNote;
    public static INoteDAO noteDAO = new SqliteNoteDAO();
    private static List<Note> userNotes = new ArrayList<>();
    private static List<Folder> folderList = new ArrayList<>();
    private static final String ALL_NOTES_NODE = "All Notes";
    public static TreeView<String> notesTreeView;

    public static void initialize(List<Note> notes, List<Folder> folders, TreeView<String> treeView) {
        notesTreeView = treeView;
        userNotes = notes;
        folderList = folders;

        //  set up context menu for notes
        setupContextMenu();
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

    private static void setupContextMenu() {
        notesTreeView.setCellFactory(tv -> {
            TreeCell<String> cell = new TextFieldTreeCell<>();

            ContextMenu contextMenu = new ContextMenu();

            cell.itemProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    TreeItem<String> treeItem = cell.getTreeItem();

                    // only show the context menu for note items
                    if (treeItem != null && isNoteItem(treeItem)) {
                        contextMenu.getItems().clear();

                        // create "move to folder" menu with submenu of folders
                        Menu moveToFolderMenu = new Menu("Move to folder");

                        // add option to remove from folder
                        MenuItem removeFromFolder = new MenuItem("Remove from folder");
                        removeFromFolder.setOnAction(event -> {
                            Note note = findNoteByName(treeItem.getValue());
                            if (note != null) {
                                note.setFolderId(null);
                                getNoteDAO().updateNote(note);
                                loadUserData();
                                populateNotesTreeView();
                            }
                        });

                        // adding each folder as an option
                        for (Folder folder : getFolderList()) {
                            MenuItem folderItem = new MenuItem(folder.getFolderName());
                            folderItem.setOnAction(event -> {
                                Note note = findNoteByName(treeItem.getValue());
                                if (note != null) {
                                    FolderService.addNoteToFolder(note, folder);
                                    loadUserData();
                                    populateNotesTreeView();
                                }
                            });
                            moveToFolderMenu.getItems().add(folderItem);
                        }

                        // only add the menu if there are folders
                        if (!moveToFolderMenu.getItems().isEmpty()) {
                            contextMenu.getItems().add(moveToFolderMenu);
                            contextMenu.getItems().add(removeFromFolder);
                            cell.setContextMenu(contextMenu);
                        }
                    } else {
                        cell.setContextMenu(null);
                    }
                } else {
                    cell.setContextMenu(null);
                }
            });
            return cell;
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

        // create node for all notes
        TreeItem<String> allNotesNode = new TreeItem<>(ALL_NOTES_NODE);
        allNotesNode.setExpanded(true);

        // add all the notes to the node
        for (Note note : userNotes) {
            allNotesNode.getChildren().add(new TreeItem<>(note.getNoteName()));
        }

        // do the same for folders
        TreeItem<String> foldersNode = new TreeItem<>(FOLDERS_NODE);
        foldersNode.setExpanded(true);

        for (Folder folder : folderList) {
            TreeItem<String> folderNode = new TreeItem<>(folder.getFolderName());

            // add notes that belong to this folder
            if (folder.getNotes() != null) {
                for (Note note : folder.getNotes()) {
                    folderNode.getChildren().add(new TreeItem<>(note.getNoteName()));
                }
            }

            foldersNode.getChildren().add(folderNode);
        }

        // add nodes to root
        rootItem.getChildren().add(allNotesNode);
        rootItem.getChildren().add(foldersNode);

        // set the root and hide it
        notesTreeView.setRoot(rootItem);
        notesTreeView.setShowRoot(false);
    }

    public static INoteDAO getNoteDAO() {
        return noteDAO;
    }

    public static void handleTreeSelection(TreeItem<String> item) {
        if (item == null) return;

        // clear current selection
        NoteService.selectedNote = null;

        // if this is a note item
        if (NoteService.isNoteItem(item)) {
            String noteName = item.getValue();
            Note note = findNoteByName(noteName);
            if (note != null) {
                NoteService.setSelectedNote(note);
            }
        }
    }
}

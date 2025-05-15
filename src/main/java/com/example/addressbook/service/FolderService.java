package com.example.addressbook.service;

import com.example.addressbook.Session;
import com.example.addressbook.model.Folder;
import com.example.addressbook.model.IFolderDAO;
import com.example.addressbook.model.Note;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

public class FolderService {
    public static final String FOLDERS_NODE = "Folders";
    private static List<Folder> folderList = new ArrayList<>();
    private static IFolderDAO folderDAO;

    public static void initialise(List<Folder> folders, IFolderDAO dao) {
        folderList = folders;
        folderDAO = dao;
    }

    public static IFolderDAO getFolderDAO() {
        return folderDAO;
    }

    public static List<Folder> getFolderList() {
        return folderList;
    }

    public static boolean isFolderItem(TreeItem<String> item) {
        if (item == null) return false;
        TreeItem<String> parent = item.getParent();
        return parent != null && FOLDERS_NODE.equals(parent.getValue());
    }

    public static Folder findFolderByName(String folderName) {
        for (Folder folder : getFolderList()) {
            if (folder.getFolderName().equals(folderName)) {
                return folder;
            }
        }
        return null;
    }

    public static void addNoteToFolder(Note note, Folder folder) {
        if (folder.getNotes() == null) {
            folder.setNotes(new ArrayList<>());
        }

        // Check if the note is already in the folder
        boolean alreadyInFolder = folder.getNotes().stream().anyMatch(n -> n.getId() == note.getId());

        if (!alreadyInFolder) {
            folder.getNotes().add(note);
        }

        note.setFolderId(folder.getFolderId());
        NoteService.getNoteDAO().updateNote(note);
    }

    public static void loadUserFolders() {
        try {
            getFolderList().clear();
            List<Folder> userFolders = getFolderDAO().getFolderByEmail(Session.getLoggedInEmail());
            getFolderList().addAll(userFolders);

            for (Folder folder : getFolderList()) {
                List<Note> notesInFolder = new ArrayList<>();
                for (Note note : NoteService.getUserNotes()) {
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

    public static boolean createFolder(String folderName, String email) {
        // input validation
        if (folderName == null || folderName.trim().isEmpty()) return false;

        // duplicate checks
        for (Folder folder : getFolderList()) {
            if (folder.getFolderName().equalsIgnoreCase(folderName.trim()))
                return false;
        }

        // folder creation
        Folder newFolder = new Folder(folderName.trim());
        newFolder.setEmail(email);
        newFolder.setNotes(new ArrayList<>());
        getFolderDAO().addFolder(newFolder);
        getFolderList().add(newFolder);
        return true;
    }
}

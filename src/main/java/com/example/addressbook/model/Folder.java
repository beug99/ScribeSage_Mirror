package com.example.addressbook.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A model class that represents a Folder with a folder ID, a folder name, the email
 * address of the folder owner and a list of the notes inside the folder
 */
public class Folder {
    private Integer folderId;
    private String folderName;
    private String email;
    private List<Note> notes;

    /**
     * Constructs a new Folder with a specified folder name and an empty list.
     * @param folderName The name of the folder.
     */
    public Folder(String folderName) {
        this.folderName = folderName;
        this.notes = new ArrayList<>();
    }

    /**
     * This is the getter method for the folder ID.
     * @return The ID of the selected folder.
     */
    public Integer getFolderId() {
        return folderId;
    }

    /**
     * //TODO --------------------
     * @param folderId
     */
    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    /**
     * This is the getter method for the name of the folder.
     * @return The name of the selected folder.
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * This is the setter method for the name of the folder. The parameter is set through user input.
     * @param folderName The name of The folder.
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * This is the getter method for the email address of the owner of the folder.
     * @return The email address of the owner of the folder.
     */
    public String getEmail() {
        return email;
    }

    /**
     * This is the setter method for the email address of the owner of the folder. The parameter
     * is set as the current logged-in users email address.
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * This is the getter method for the list of notes within a selected folder.
     * @return A list of the notes.
     */
    public List<Note> getNotes() {
        return notes;
    }

    /**
     * This is the setter method for the list of notes within the selected folder.
     * //TODO -------------------------------
     * @param notes
     */
    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    /**
     * This method adds a note to a list within the selected folder.
     * If the folder is empty, it creates a new list.
     * @param note The note that will be added to the list.
     */
    public void addNote(Note note) {
        if (this.notes == null) {
            this.notes = new ArrayList<>();
        }
        this.notes.add(note);
    }

    /**
     * This method removes a note from the list of notes in a selected folder.
     * @param note The note that will be removed from the list.
     */
    public void removeNote(Note note) {
        if (this.notes != null) {
            this.notes.remove(note);
        }
    }
}
package com.example.addressbook.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A model class that represents a Note with a note name, note tage, the note text, a note owner,
 * the folder ID of the note and the date of the note creation.
 */
public class Note {
    private int id;
    private String noteName;
    private String noteTags;
    private String noteText;
    private String noteOwner;
    private Integer folderId;
    private LocalDateTime createdDate;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * A constructor that creates a new Note with the specified note name, note tags, note test,
     * note owner and folder ID.
     * @param noteName The name of the note.
     * @param noteTags The note tags.
     * @param noteText The text that has been saved to the note.
     * @param noteOwner The owner and creator of the note.
     * @param folderId The ID of the folder that the note is saved in.
     */
    public Note(String noteName, String noteTags, String noteText, String noteOwner, Integer folderId) {
        this.noteName = noteName;
        this.noteTags = noteTags;
        this.noteText = noteText;
        this.noteOwner = noteOwner;
        this.folderId = folderId;
        this.createdDate = LocalDateTime.now();
    }

    /**
     * Constructs a new Note with the specified note name, note tags, note test and note owner.
     * @param noteName The name of the note.
     * @param noteTags The note tags.
     * @param noteText The text that has been saved to the note.
     * @param noteOwner The owner and creator of the note.
     */
    public Note(String noteName, String noteTags, String noteText, String noteOwner) {
        this(noteName, noteTags, noteText, noteOwner, null);
    }

    /**
     * Constructs a new Note with the specified note name.
     * @param noteName The name of the note.
     */
    public Note(String noteName) {
        this(noteName, "", "", null, null);
    }

    /**
     * This is the getter method for the note ID.
     * @return The ID of the note as an int.
     */
    public int getId() {
        return id;
    }

    /**
     * This is the setter method for the note ID, the note ID is set as the parameter.
     * @param id The ID number of the note.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * This is the getter method for the Note name.
     * @return The name of the note.
     */
    public String getNoteName() {
        return noteName;
    }

    /**
     * This is the setter method for the note name. The parameter is set through user input.
     * @param noteName The name of the note.
     */
    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    /**
     * This is the getter method for the note tags.
     * @return The note tags.
     */
    public String getNoteTags() {
        return noteTags;
    }

    /**
     * This is the setter method for the note tags. The parameter is set through user input.
     * @param noteTags The tags of the note.
     */
    public void setNoteTags(String noteTags) {
        this.noteTags = noteTags;
    }

    /**
     * This is the getter method for the note text.
     * @return The text that has been saved to the note.
     */
    public String getNoteText() {
        return noteText;
    }

    /**
     * This is the setter method for the note text. The parameter is set through user input.
     * @param noteText Text that has been saved to the note.
     */
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    /**
     * This is the getter method for the note owners email.
     * @return The email address of the owner of the note.
     */
    public String getNoteOwner() {
        return noteOwner;
    }

    /**
     * This is the setter method for the note owner. The email address is set as the current logged-in user.
     * @param noteOwner The email address of the creator and owner of the note - the current logged-in user.
     */
    public void setNoteOwner(String noteOwner) {
        this.noteOwner = noteOwner;
    }

    /**
     * This is the getter method for the folder that the note is saved in.
     * @return The ID of the folder that the note is saved in.
     */
    public Integer getFolderId() {
        return folderId;
    }

    /**
     * This is the setter method for the folder ID. The ID is set when the user chooses a folder at note creation
     * or when the user changes the notes folder.
     * @param folderId The ID of the folder that the note is saved in.
     */
    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    /**
     * This is the getter method for the date that the note was created.
     * @return The date that the note was created.
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * This is the setter method for the note creation date. The date is set to the current date when the user creates the note.
     * @param createdDate The date that note was created.
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * This method returns the value that is returned from the getCreatedDate() method as a string.
     * @return The note creation date as a string.
     */
    public String getFormattedCreatedDate() {
        return createdDate != null ? createdDate.format(formatter) : "";
    }
}
package com.example.addressbook.model;

import java.time.LocalDate;

public class Note {
    private int noteID;
    private String noteName;
    private String noteTags;
    private String noteText;
    private String noteOwner;
    private LocalDate lastModified;
    private boolean isFolder;
    private boolean isCompleted;

    public Note(String noteName, String noteTags, String noteText, String noteOwner){
        this.noteName = noteName;
        this.noteTags = noteTags;
        this.noteText = noteText;
        this.noteOwner = noteOwner;
    }

    public int getId() {
        return noteID;
    }

    public void setId(int id) {
        this.noteID = id;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getNoteTags() {
        return noteTags;
    }

    public void setNoteTags(String noteTags) {
        this.noteTags = noteTags;
    }

    public String getNoteOwner() { return noteOwner;}

    public void setNoteOwner(String owner) { this.noteOwner = owner;}

    public LocalDate getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDate lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

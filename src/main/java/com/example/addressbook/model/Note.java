package com.example.addressbook.model;

import java.awt.*;
import java.io.File;

public class Note {
    private int noteID;
    private String noteName;
    private String noteTags;
    private String noteText;
    private String noteOwner;
    private Integer folderId;

    public Note(String noteName, String noteTags, String noteText, String noteOwner, Integer folderId){
        this.noteName = noteName;
        this.noteTags = noteTags;
        this.noteText = noteText;
        this.noteOwner = noteOwner;
        this.folderId = folderId;
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

    public Integer getFolderId() { return folderId; }

    public void setFolderId(Integer folderId) { this.folderId = folderId; }

}

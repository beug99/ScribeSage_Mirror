package com.example.addressbook.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {
    private int id;
    private String noteName;
    private String noteTags;
    private String noteText;
    private String noteOwner;
    private Integer folderId;
    private LocalDateTime createdDate;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Note(String noteName, String noteTags, String noteText, String noteOwner, Integer folderId) {
        this.noteName = noteName;
        this.noteTags = noteTags;
        this.noteText = noteText;
        this.noteOwner = noteOwner;
        this.folderId = folderId;
        this.createdDate = LocalDateTime.now();
    }

    public Note(String noteName, String noteTags, String noteText, String noteOwner) {
        this(noteName, noteTags, noteText, noteOwner, null);
    }

    public Note(String noteName) {
        this(noteName, "", "", null, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteTags() {
        return noteTags;
    }

    public void setNoteTags(String noteTags) {
        this.noteTags = noteTags;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getNoteOwner() {
        return noteOwner;
    }

    public void setNoteOwner(String noteOwner) {
        this.noteOwner = noteOwner;
    }

    public Integer getFolderId() {
        return folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getFormattedCreatedDate() {
        return createdDate != null ? createdDate.format(formatter) : "";
    }
}
package com.example.addressbook.model;

import java.util.ArrayList;
import java.util.List;

public class Folder {
    private Integer folderId;
    private String folderName;
    private String email;
    private List<Note> notes;

    public Folder(String folderName) {
        this.folderName = folderName;
        this.notes = new ArrayList<>();
    }

    public Integer getFolderId() {
        return folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public void addNote(Note note) {
        if (this.notes == null) {
            this.notes = new ArrayList<>();
        }
        this.notes.add(note);
    }

    public void removeNote(Note note) {
        if (this.notes != null) {
            this.notes.remove(note);
        }
    }
}
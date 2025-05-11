package com.example.addressbook.model;

import java.util.List;

public class Folder {
    private Integer FolderId;
    private String folderName;
    private List<Note> notes;
    private String email;

    public Folder(String folderName) {
        this.folderName = folderName;
    }

    public Integer getFolderId() 
        { return FolderId; }

    public void setFolderId(Integer id) 
        { this.FolderId = id; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFolderName() 
        { return folderName; }

    public void setFolderName(String folderName) 
        { this.folderName = folderName; }

    public List<Note> getNotes() 
        { return notes; }

    public void setNotes(List<Note> notes) 
        { this.notes = notes; }
}
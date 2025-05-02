package com.example.addressbook.model;

import java.util.List;

public class Folder {
    private Integer FolderId;
    private String folderName;
    private List<Note> notes;
    private Integer userId;

    public Folder(String folderName) {
        this.folderName = folderName;
    }

    public Integer getFolderId() 
        { return FolderId; }

    public void setFolderId(Integer id) 
        { this.FolderId = id; }

    public Integer getUserId() 
        { return userId; }

    public void setUserId(Integer userId) 
        { this.userId = userId; }

    public String getFolderName() 
        { return folderName; }

    public void setFolderName(String folderName) 
        { this.folderName = folderName; }

    public List<Note> getNotes() 
        { return notes; }

    public void setNotes(List<Note> notes) 
        { this.notes = notes; }
}
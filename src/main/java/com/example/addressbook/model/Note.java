package com.example.addressbook.model;

public class Note {
    private int noteID;
    private String noteName;
    private String noteTags;

    public Note(String noteName, String noteTags){
        this.noteName = noteName;
        this.noteTags = noteTags;
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

    public String getNoteTags() {
        return noteTags;
    }

    public void setNoteTags(String noteTags) {
        this.noteTags = noteTags;
    }


}

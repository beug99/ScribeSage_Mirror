package com.example.addressbook.model;

public class Note {
    private int noteID;
    private String noteName;
    private String noteTags;
    private String noteTexts;

    public Note(String noteName, String noteTags){
        this.noteName = noteName;
        this.noteTags = noteTags;
        this.noteTexts = noteTexts;
    }

    public int getId() {
        return noteID;
    }

    public String getNoteTexts() { return noteTexts; }

    public void setNoteTexts(String text) { this.noteTexts = noteTexts; }

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

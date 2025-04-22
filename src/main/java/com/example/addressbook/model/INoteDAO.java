package com.example.addressbook.model;

import java.util.List;

public interface INoteDAO {
    public void addNote(Note note);

    public void updateNote(Note note);

    public Note getNote(int id);

    public List<Note> getAllNotes();

    void deleteNote(Note selectedNote);
}

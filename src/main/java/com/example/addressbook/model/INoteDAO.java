package com.example.addressbook.model;
import java.util.List;

public interface INoteDAO {
    int addNote(Note note);
    void updateNote(Note note);
    Note getNoteById(int id);
    List<Note> getNotesByOwner(String owner);
    List<Note> getAllNotes();
    void deleteNote(Note selectedNote);
}
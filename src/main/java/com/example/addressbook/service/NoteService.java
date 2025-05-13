package com.example.addressbook.service;

import com.example.addressbook.model.Note;

public class NoteService {

    public static Note selectedNote;

    public static void setSelectedNote(Note note) {
        selectedNote = note;
        System.out.println("Selected note: " + note.getNoteName() + " ID:" + note.getId() + " Owner: " + note.getNoteOwner());
    }
}

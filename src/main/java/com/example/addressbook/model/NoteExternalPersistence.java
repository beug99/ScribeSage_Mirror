package com.example.addressbook.model;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class NoteExternalPersistence {

    // Concats the String and Int properties of notes to single string
    private String convertNote(Note note) {
        StringBuilder noteAsString = new StringBuilder();
        noteAsString.append("!Note Metadata!\n");
        noteAsString.append("ID: ").append(note.getId()).append("\n");
        noteAsString.append("Name: ").append(note.getNoteName()).append("\n");
        noteAsString.append("Tags: ").append(note.getNoteTags()).append("\n");
        noteAsString.append("!Note Content!\n");
        noteAsString.append(note.getNoteText());
        return noteAsString.toString();
    }

    // Turns a Note into a txt file
    public void exportNote(Note note, String filePath) {
        String contents = convertNote(note);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

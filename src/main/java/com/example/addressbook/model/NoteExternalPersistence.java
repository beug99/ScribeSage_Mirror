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
/*
    // Takes a single TXT file as input and adds it to a users notes DB
    public void importNote(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Example assumes file structure from convertNote()
            int iNoteID = Integer.parseInt(reader.readLine().split(": ")[1]);
            String iNoteName = reader.readLine().split(": ")[1];
            String iNoteTags = reader.readLine().split(": ")[1];
            reader.readLine(); // Skip empty line
            reader.readLine(); // Skip comment line

            StringBuilder htmlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                htmlBuilder.append(line).append("\n");
            }
            String iNoteText = htmlBuilder.toString();
        } catch (IIOException e) {
            e.printStackTrace();
            // Handle exception
        }

    } */
}

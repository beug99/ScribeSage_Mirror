package com.example.addressbook.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqliteNoteDAO implements INoteDAO {
    private Connection connection;

    public SqliteNoteDAO() {
        connection = SqliteConnection.getInstance();
        createTable();
    }

    private void createTable() {
        // Create table if not exists
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS notes ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "noteName VARCHAR NOT NULL,"
                    + "noteTags VARCHAR NOT NULL,"
                    + "noteText VARCHAR NOT NULL"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void addNote(Note note) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO notes (noteName, noteTags, noteText) VALUES (?, ?, ?)");
            statement.setString(1, note.getNoteName());
            statement.setString(2, note.getNoteTags());
            statement.setString(3, note.getNoteText());

            statement.executeUpdate();
            // Set the id of the new contact
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                note.setId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateNote(Note note) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE notes SET noteName = ?, noteTags = ?, noteText = ? WHERE id = ?");
            statement.setString(1, note.getNoteName());
            statement.setString(2, note.getNoteTags());
            statement.setString(3, note.getNoteText());
            statement.setInt(4, note.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Note getNote(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM notes WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String noteName = resultSet.getString("noteName");
                String noteTags = resultSet.getString("noteTags");
                String noteText = resultSet.getString("noteText");

                Note note = new Note(noteName, noteTags, noteText);
                note.setId(id);
                return note;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM notes";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String noteName = resultSet.getString("noteName");
                String noteTags = resultSet.getString("noteTags");
                String noteText = resultSet.getString("noteText");

                Note note = new Note(noteName, noteTags, noteText);
                note.setId(id);
                notes.add(note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notes;
    }

    @Override
    public void deleteNote(Note selectedNote) {

//        try {
//            PreparedStatement statement = connection.prepareStatement("DELETE FROM notes WHERE id = ?");
//            statement.setInt(1, selectedNote.getId());
//            statement.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
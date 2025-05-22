package com.example.addressbook.model;

import com.example.addressbook.Session;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

/**
 * This DAO class will handle the CRUD operations with the SQlite Note database. This class
 * implements the {@link com.example.addressbook.model.INoteDAO INoteDAO} interface. It handles
 * the operations for note creation, viewing, editing and deletion.
 */
public class SqliteNoteDAO implements INoteDAO {
    private Connection connection;

    /**
     *
     */
    public SqliteNoteDAO() {
        connection = SqliteNoteConnection.getInstance("notes.db");
        createTable();
    }

    private void createTable() {
        // Create table if not exists
        try {
            Statement statement = connection.createStatement();

            try {
                ResultSet rs = connection.getMetaData().getColumns(null, null, "notes", "createdDate");
                if (!rs.next()) {
                    // column doesn't exist, add it
                    statement.execute("ALTER TABLE notes ADD COLUMN createdDate TIMESTAMP");
                    logSQLexecution("Added createdDate column to notes table");
                }
            }catch (Exception e) {
            }
            String query = "CREATE TABLE IF NOT EXISTS notes ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "noteName VARCHAR NOT NULL,"
                    + "noteTags VARCHAR NOT NULL,"
                    + "noteText VARCHAR NOT NULL,"
                    + "noteOwner VARCHAR NOT NULL,"
                    + "folderId INTEGER,"
                    + "createdDate TIMESTAMP"
                    + ")";
            statement.execute(query);
            logSQLexecution(statement.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logSQLexecution(String statement) {
        System.out.println("SQL executed: " + statement);
    }


    @Override
    public int addNote(Note note) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO notes (noteName, noteTags, noteText, noteOwner, createdDate) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, note.getNoteName());
            statement.setString(2, note.getNoteTags());
            statement.setString(3, note.getNoteText());
            statement.setString(4, Session.getLoggedInEmail());
//            statement.setObject(5, note.getFolderId());
            if (note.getCreatedDate() == null) {
                note.setCreatedDate(LocalDateTime.now());
            }
            Timestamp timeStamp = Timestamp.valueOf(note.getCreatedDate());
            statement.setTimestamp(5,timeStamp);

            statement.executeUpdate();
            logSQLexecution(statement.toString());
            // Set the id of the new contact
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                note.setId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void updateNote(Note note) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE notes SET noteName = ?, noteTags = ?, " +
                    "noteText = ?, noteOwner = ?, folderId = ?, createdDate = ? WHERE id = ?");
            statement.setString(1, note.getNoteName());
            statement.setString(2, note.getNoteTags());
            statement.setString(3, note.getNoteText());
            statement.setString(4, Session.getLoggedInEmail());
            statement.setObject(5, note.getFolderId());
            statement.setInt(6, note.getId());

            // set created date
            LocalDateTime createdDate = note.getCreatedDate();
            if (createdDate == null) {
                createdDate = LocalDateTime.now();
                note.setCreatedDate(createdDate);
            }
            Timestamp timestamp = Timestamp.valueOf(createdDate);
            statement.setTimestamp(6, timestamp);

            statement.setInt(7, note.getId());
            statement.executeUpdate();
            logSQLexecution(statement.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Note getNoteById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM notes WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            logSQLexecution(statement.toString());
            if (resultSet.next()) {
                String noteName = resultSet.getString("noteName");
                String noteTags = resultSet.getString("noteTags");
                String noteText = resultSet.getString("noteText");
                String noteOwner = resultSet.getString("noteOwner");
                Integer folderId = resultSet.getInt("folderId");

                Note note = new Note(noteName, noteTags, noteText, noteOwner, folderId);
                note.setId(id);

                Timestamp timestamp = resultSet.getTimestamp("createdDate");
                if (timestamp != null) {
                    note.setCreatedDate(timestamp.toLocalDateTime());
                } else {
                    note.setCreatedDate(LocalDateTime.now());
                }

                return note;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Note not found");
        return null;
    }

    @Override
    public List<Note> getNotesByOwner(String owner) {
        List<Note> notes = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM notes WHERE noteOwner = ?");
            statement.setString(1, owner);
            ResultSet resultSet = statement.executeQuery();
            logSQLexecution(statement.toString());
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String noteName = resultSet.getString("noteName");
                String noteTags = resultSet.getString("noteTags");
                String noteText = resultSet.getString("noteText");
                String noteOwner = resultSet.getString("noteOwner");
                Integer folderId = resultSet.getInt("folderId");

                Note note = new Note(noteName, noteTags, noteText, noteOwner, folderId);
                note.setId(id);

                Timestamp timestamp = resultSet.getTimestamp("createdDate");
                if (timestamp != null) {
                    note.setCreatedDate(timestamp.toLocalDateTime());
                } else {
                    note.setCreatedDate(LocalDateTime.now());
                }

                notes.add(note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notes;
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
                String noteOwner = resultSet.getString("noteOwner");
                logSQLexecution(statement.toString());
                Integer folderId = resultSet.getInt("folderId");
                logSQLexecution(statement.toString());

                Note note = new Note(noteName, noteTags, noteText, noteOwner, folderId);
                note.setId(id);

                Timestamp timestamp = resultSet.getTimestamp("createdDate");
                if (timestamp != null) {
                    note.setCreatedDate(timestamp.toLocalDateTime());
                } else {
                    note.setCreatedDate(LocalDateTime.now());
                }

                notes.add(note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notes;
    }

    /**
     *
     * @param selectedNote
     */
    @Override
    public void deleteNote(Note selectedNote) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM notes WHERE id = ?");
            statement.setInt(1, selectedNote.getId());
            statement.executeUpdate();
            logSQLexecution(statement.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.example.addressbook.model;
import java.util.List;

/**
 * Interface for the Note Data Access Object that handles
 * the CRUD operations for the Note class with the database.
 */
public interface INoteDAO {
    /**
     * Adds a note to the database
     * @param note The note that will be added.
     * @return
     */
    int addNote(Note note);

    /**
     * Updates an edited note on the database.
     * @param note The note that will be edited.
     */
    void updateNote(Note note);

    /**
     * Retrieves a note from the database by the notes ID.
     * @param id The ID of the note.
     * @return A note with the matching ID, or a message stating no note is found.
     */
    Note getNoteById(int id);

    /**
     * Retrieves a list from the database of the notes that the owner has saved.
     * @param owner The email address of the owner.
     * @return A list of Notes owned by the owner.
     */
    List<Note> getNotesByOwner(String owner);

    /**
     *
     * @return
     */
    List<Note> getAllNotes();

    /**
     * Deletes a selected note from the database.
     * @param selectedNote The note that will be deleted.
     */
    void deleteNote(Note selectedNote);
}
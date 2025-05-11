package com.example.addressbook.model;
import java.util.List;

/**
 * Interface for Note DAO, handles
 * CRUD operations for Notes database.
 */
public interface INoteDAO {
    /**
     * Adds a new Note to the DB
     *
     * @param note The Note to add
     */
    public void addNote(Note note);

    /**
     * Updates an existing note in the DB
     *
     * @param note The Note to update
     */
    public void updateNote(Note note);

    /**
     * Retrieves a Note from the DB via ID
     *
     * @param id Integer ID of desired Note
     * @return Note with input ID or null if not found
     */
    public Note getNoteById(int id);

    /**
     * Retrieves all Notes belonging to an Owner
     *
     * @param owner String email of user who owns the Note
     * @return Notes associated with Owner
     */
    public List<Note> getNotesByOwner(String owner);

    /**
     * Retrieves all Notes in the notes DB
     *
     * @return All Notes in the notes DB
     */
    public List<Note> getAllNotes();

    /**
     * Deletes a Note from the DB
     *
     * @param selectedNote Note to delete from Notes DB
     */
    public void deleteNote(Note selectedNote);
}

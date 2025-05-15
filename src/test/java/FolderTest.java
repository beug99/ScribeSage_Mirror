import com.example.addressbook.model.Folder;
import com.example.addressbook.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FolderTest {
    private Folder folder;
    private final String FOLDER_NAME = "Work Notes";
    private final String EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        folder = new Folder(FOLDER_NAME);
    }

    @Test
    void testConstructor() {
        assertEquals(FOLDER_NAME, folder.getFolderName());
        assertNotNull(folder.getNotes());
        assertTrue(folder.getNotes().isEmpty());
        assertNull(folder.getFolderId());
        assertNull(folder.getEmail());
    }

    @Test
    void testSetFolderId() {
        Integer folderId = 1;
        folder.setFolderId(folderId);
        assertEquals(folderId, folder.getFolderId());
    }

    @Test
    void testSetFolderName() {
        String newName = "Personal Notes";
        folder.setFolderName(newName);
        assertEquals(newName, folder.getFolderName());
    }

    @Test
    void testSetEmail() {
        folder.setEmail(EMAIL);
        assertEquals(EMAIL, folder.getEmail());
    }

    @Test
    void testSetNotes() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note("Note 1"));
        notes.add(new Note("Note 2"));

        folder.setNotes(notes);

        assertEquals(notes, folder.getNotes());
        assertEquals(2, folder.getNotes().size());
    }

    @Test
    void testAddNote() {
        Note note1 = new Note("Note 1");
        Note note2 = new Note("Note 2");

        folder.addNote(note1);
        assertEquals(1, folder.getNotes().size());
        assertTrue(folder.getNotes().contains(note1));

        folder.addNote(note2);
        assertEquals(2, folder.getNotes().size());
        assertTrue(folder.getNotes().contains(note2));
    }

    @Test
    void testAddNoteWhenNotesIsNull() {
        folder.setNotes(null);

        Note note = new Note("Test Note");
        folder.addNote(note);

        assertNotNull(folder.getNotes());
        assertEquals(1, folder.getNotes().size());
        assertTrue(folder.getNotes().contains(note));
    }

    @Test
    void testRemoveNote() {
        Note note1 = new Note("Note 1");
        Note note2 = new Note("Note 2");

        folder.addNote(note1);
        folder.addNote(note2);
        assertEquals(2, folder.getNotes().size());

        folder.removeNote(note1);
        assertEquals(1, folder.getNotes().size());
        assertFalse(folder.getNotes().contains(note1));
        assertTrue(folder.getNotes().contains(note2));
    }

    @Test
    void testRemoveNoteWhenNotesIsNull() {
        // shold not throw an exception
        folder.setNotes(null);
        Note note = new Note("Test Note");
        folder.removeNote(note);
        // test passes if no exception is thrown
    }
}

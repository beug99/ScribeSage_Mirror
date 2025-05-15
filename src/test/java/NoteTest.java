import com.example.addressbook.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NoteTest {

    private Note noteWithFolder;
    private Note noteWithoutFolder;
    private Note simpleNote;

    private final String NOTE_NAME = "Shopping List";
    private final String NOTE_TAGS = "shopping, groceries";
    private final String NOTE_TEXT = "Milk, Eggs, Bread";
    private final String NOTE_OWNER = "john.doe@example.com";
    private final Integer FOLDER_ID = 1;

    @BeforeEach
    void setUp() {
        noteWithFolder = new Note(NOTE_NAME, NOTE_TAGS, NOTE_TEXT, NOTE_OWNER, FOLDER_ID);
        noteWithoutFolder = new Note(NOTE_NAME, NOTE_TAGS, NOTE_TEXT, NOTE_OWNER);
        simpleNote = new Note(NOTE_NAME);
    }

    @Test
    void testFullConstructor() {
        assertEquals(NOTE_NAME, noteWithFolder.getNoteName());
        assertEquals(NOTE_TAGS, noteWithFolder.getNoteTags());
        assertEquals(NOTE_TEXT, noteWithFolder.getNoteText());
        assertEquals(NOTE_OWNER, noteWithFolder.getNoteOwner());
        assertEquals(FOLDER_ID, noteWithFolder.getFolderId());
    }

    @Test
    void testConstructorWithoutFolder() {
        assertEquals(NOTE_NAME, noteWithoutFolder.getNoteName());
        assertEquals(NOTE_TAGS, noteWithoutFolder.getNoteTags());
        assertEquals(NOTE_TEXT, noteWithoutFolder.getNoteText());
        assertEquals(NOTE_OWNER, noteWithoutFolder.getNoteOwner());
        assertNull(noteWithoutFolder.getFolderId());
    }

    @Test
    void testSimpleConstructor() {
        assertEquals(NOTE_NAME, simpleNote.getNoteName());
        assertEquals("", simpleNote.getNoteTags());
        assertEquals("", simpleNote.getNoteText());
        assertNull(simpleNote.getNoteOwner());
        assertNull(simpleNote.getFolderId());
    }

    @Test
    void testSetId() {
        int id = 1;
        noteWithFolder.setId(id);
        assertEquals(id, noteWithFolder.getId());
    }

    @Test
    void testSetNoteName() {
        String newName = "Grocery List";
        noteWithFolder.setNoteName(newName);
        assertEquals(newName, noteWithFolder.getNoteName());
    }

    @Test
    void testSetNoteTags() {
        String newTags = "food, essentials";
        noteWithFolder.setNoteTags(newTags);
        assertEquals(newTags, noteWithFolder.getNoteTags());
    }

    @Test
    void testSetNoteText() {
        String newText = "Apples, Bananas, Oranges";
        noteWithFolder.setNoteText(newText);
        assertEquals(newText, noteWithFolder.getNoteText());
    }

    @Test
    void testSetNoteOwner() {
        String newOwner = "jane.smith@example.com";
        noteWithFolder.setNoteOwner(newOwner);
        assertEquals(newOwner, noteWithFolder.getNoteOwner());
    }

    @Test
    void testSetFolderId() {
        Integer newFolderId = 2;
        noteWithFolder.setFolderId(newFolderId);
        assertEquals(newFolderId, noteWithFolder.getFolderId());

        // Test setting folder ID for note without folder
        noteWithoutFolder.setFolderId(newFolderId);
        assertEquals(newFolderId, noteWithoutFolder.getFolderId());
    }
}
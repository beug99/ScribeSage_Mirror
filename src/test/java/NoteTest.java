import com.example.addressbook.model.Note;
import com.example.addressbook.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoteTest {
    //SHELLEY'S TEST FILE

    private static final String NOTE_NAME = "Uni Week 1";
    private static final String NOTE_TAGS = "Math";
    private static final String NOTE_TEXT = "This is my note";

    private static final String NOTE_NAME_TWO = "Uni Week 2";
    private static final String NOTE_TAGS_TWO = "Databases";
    private static final String NOTE_TEXT_TWO = "This is my second note";


    private Note note;
    private Note noteTwo;

    @BeforeEach
    public void setUp() {
        note = new Note(NOTE_NAME, NOTE_TAGS, NOTE_TEXT);
        noteTwo = new Note(NOTE_NAME_TWO, NOTE_TAGS_TWO, NOTE_TEXT_TWO);
    }

    @Test
    public void testSetId() {
        note.setId(1);
        assertEquals(1, note.getId());
    }

    @Test
    public void testGetNoteName() {
        assertEquals(NOTE_NAME, note.getNoteName());
    }

    @Test
    public void testSetNoteName() {
        note.setNoteName(NOTE_NAME_TWO);
        assertEquals(NOTE_NAME_TWO, note.getNoteName());
    }
    @Test
    public void testGetNoteTags() {
        assertEquals(NOTE_TAGS, note.getNoteTags());
    }
    @Test
    public void testSetNoteTags() {
        note.setNoteTags(NOTE_TAGS_TWO);
        assertEquals(NOTE_TAGS_TWO, note.getNoteTags());
    }

}

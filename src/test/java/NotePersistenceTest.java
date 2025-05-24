import com.example.addressbook.Session;
import com.example.addressbook.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotePersistenceTest {

    /**
     * TEST USERS
     */
    // Test User 1
    private static final String User1_FirstName = "Andy";
    private static final String User1_LastName = "Pole";
    private static final String User1_Email = "Andyp@me.com";
    private static final String User1_Password = "APass";

    // Test User 2
    private static final String User2_FirstName = "Zane";
    private static final String User2_LastName = "Post";
    private static final String User2_Email = "Zanep@me.com";
    private static final String User2_Password = "g00d_Pazzwrxd";

    /**
     * TEST NOTES
     */
    // Test Note 1
    private static final String Note1_Name = "BioMedNotes Week 1";
    private static final String Note1_Tags = "BioMed";
    private static final String Note1_Text_Before = "I am going to fail, I have not studdied. Sorry mum, me no doctor.";
    private static final String Note1_Text_After = "I will NOT fail, I have STUDDIED. MUM! ME AM DOCTOR!!!";

    // Test Note 2
    private static final String Note2_Name = "BioMedNotes Week 2";
    private static final String Note2_Tags = "BioMed";
    private static final String Note2_Text = "Bones have something to do with anatomy... I think.";

    // Test Note 3
    private static final String Note3_Name = "Maths Week 1";
    private static final String Note3_Tags = "Math";
    private static final String Note3_Text_Before = "1 + 1 = _ uh let me get the calculator.";
    private static final String Note3_Text_After = "1 + 1 = 2.1 heheh eazy.";

    // Test Note 4
    private static final String Note4_Name = "Maths Week 2";
    private static final String Note4_Tags = "Math";
    private static final String Note4_Text = "Ducks have eyes but not all eyes have a duck. Quack.";

    // Test Note 5
    private static final String Note5_Name = "DELETE ME!";
    private static final String Note5_Tags = "";
    private static final String Note5_Text = "I meant to load a note not make this ugh! Whatever shall I do!";

    // Test Note 6
    private static final String Note6_Name = "DELETE ME!";
    private static final String Note6_Tags = "";
    private static final String Note6_Text = "I meant to load a note not make this ugh! Whatever shall I do!";

    /**
     * Test Initializations
     */
    private INoteDAO NotesDAO;
    private IUserDAO UserDAO;

    private User User1;
    private User User2;
    private List<User> userList;

    private List<Note> noteList;

    private NotePersistenceTest() {
        NotesDAO = new SqliteNoteDAO();
        UserDAO = new SqliteUserDAO();
    }

    /**
     * Test Setup
     */
    @BeforeEach
    public void setup() {
        // Clear logged-in user
        Session.clear();

        // Clear User DB
        userList = UserDAO.getAllUsers();
        for (User user : userList) {
            UserDAO.deleteUser(user);
        }

        // Clear Notes DB
        noteList = NotesDAO.getAllNotes();
        for (Note note : noteList) {
            NotesDAO.deleteNote(note);
        }

        // Add test users to User DB
        User1 = new User(User1_FirstName, User1_LastName, User1_Email, User1_Password);
        UserDAO.addUser(User1);
        User2 = new User(User2_FirstName, User2_LastName, User2_Email, User2_Password);
        UserDAO.addUser(User2);
    }

    /**
     * Test Method Overview
     * <p>
     * Test Case 1:
     * User1 Logs in, creates a note, saves, logs out.
     * Success if User1's note has been retained.
     * <p>
     * Test Case 2:
     * User1 Logs in, creates a note, saves.
     * User1 deletes the note, logs out.
     * Success if User1's note has been deleted
     * <p>
     * Test Case 3:
     * User1 logs in, creates a note, saves, logs out.
     * User2 logs in, creates a note, saves, logs out.
     * User1 logs in, views note, logs out.
     * User2 logs in, views note, logs out.
     * Success if User1's and User2's notes have been retained.
     * <p>
     * Test Case 4:
     * User1 logs in, creates a note, saves, logs out.
     * User2 logs in, creates a note, saves, logs out.
     * User1 logs in, deletes their note, logs out.
     * User2 logs in, views note, logs out.
     * Success if User1's note has been deleted but User2's note has been retained.
     * <p>
     * Test Case 5:
     * User1 Logs in, creates a note, saves, logs out.
     * User2 logs in, creates a note, saves, logs out.
     * User1 Logs in, views note, edits the note, saves, logs out.
     * User2 Logs in, views note, edits the note, saves, logs out.
     * Success if User1's and User2's notes and edits have been retained.
     * <p>
     * Test Case 6:
     * User1 logs in, creates a note, saves, logs out.
     * User2 logs in, coincidentally creates a note of the same name, saves and logs out.
     * User1 logs back in, loads their note, edits the note, saves, logs out.
     * User2 logs back in, loads their note, edits the note, saves, logs out.
     * Success if both text fields retain changes only made by their owners.
     */

    @Test
    public void testCase1() {
        // User1 logs in
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());
    }

    @Test
    public void testCase5() {
        // User1 logs in and creates a note
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());
        Note note1 = new Note(Note1_Name, Note1_Tags, Note1_Text_Before, null);
        NotesDAO.addNote(note1);

        List<Note> user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        assertEquals(Note1_Text_Before, user1Notes.get(0).getNoteText());

        // User1 logs out
        Session.clear();

        // User2 logs in and creates a note
        Session.setUser(User2.getEmail(), User2.getFirstName(), User2.getLastName());
        Note note3 = new Note(Note3_Name, Note3_Tags, Note3_Text_Before, null);
        NotesDAO.addNote(note3);

        List<Note> user2Notes = NotesDAO.getNotesByOwner(User2_Email);
        assertEquals(Note3_Text_Before, user2Notes.get(0).getNoteText());

        // User2 logs out
        Session.clear();

        // User1 logs back in and edits their note
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());
        user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        Note user1Note = user1Notes.get(0);
        user1Note.setNoteText(Note1_Text_After);
        NotesDAO.updateNote(user1Note);

        // User1 logs back out
        Session.clear();

        // User2 logs back in and edits their note
        Session.setUser(User2.getEmail(), User2.getFirstName(), User2.getLastName());
        user2Notes = NotesDAO.getNotesByOwner(User2_Email);
        Note user2Note = user2Notes.get(0);
        user2Note.setNoteText(Note3_Text_After);
        NotesDAO.updateNote(user2Note);

        // User2 logs out
        Session.clear();

        // Verify success criteria (User1's edits have been retained)
        user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        assertEquals(Note1_Text_After, user1Notes.get(0).getNoteText());

        // Verify success criteria (User2's edits have been retained)
        user2Notes = NotesDAO.getNotesByOwner(User2_Email);
        assertEquals(Note3_Text_After, user2Notes.get(0).getNoteText());
    }

    @Test
    public void testCase6() {
        // User1 logs in and creates a note with a specific name
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());
        Note note5User1 = new Note(Note5_Name, Note5_Tags, Note5_Text, null);
        NotesDAO.addNote(note5User1);

        // User1 edits their note
        List<Note> user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        Note user1Note = user1Notes.get(0);
        user1Note.setNoteText(Note1_Text_After);
        NotesDAO.updateNote(user1Note);

        Session.clear();

        // User2 logs in and creates a note with the same name
        Session.setUser(User2.getEmail(), User2.getFirstName(), User2.getLastName());
        Note note6User2 = new Note(Note6_Name, Note6_Tags, Note6_Text, null);
        NotesDAO.addNote(note6User2);

        // User2 edits their note
        List<Note> user2Notes = NotesDAO.getNotesByOwner(User2_Email);
        Note user2Note = user2Notes.get(0);
        user2Note.setNoteText(Note3_Text_After);
        NotesDAO.updateNote(user2Note);

        Session.clear();

        // Verify success criteria (User1's note was retained and edited correctly)
        user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        assertEquals(1, user1Notes.size());
        assertEquals(Note1_Text_After, user1Notes.get(0).getNoteText());
        assertEquals(User1_Email, user1Notes.get(0).getNoteOwner());

        // Verify success criteria (User2's note was retained and edited correctly)
        user2Notes = NotesDAO.getNotesByOwner(User2_Email);
        assertEquals(1, user2Notes.size());
        assertEquals(Note3_Text_After, user2Notes.get(0).getNoteText());
        assertEquals(User2_Email, user2Notes.get(0).getNoteOwner());
    }
}
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
    private static final String Note1_Owner = User1_Email;

    // Test Note 2
    private static final String Note2_Name = "BioMedNotes Week 2";
    private static final String Note2_Tags = "BioMed";
    private static final String Note2_Text = "Bones have something to do with anatomy... I think.";
    private static final  String Note2_Owner = User1_Email;

    // Test Note 3
    private static final String Note3_Name = "Maths Week 1";
    private static final String Note3_Tags = "Math";
    private static final String Note3_Text_Before = "1 + 1 = _ uh let me get the calculator.";
    private static final String Note3_Text_After = "1 + 1 = 2.1 heheh eazy.";
    private static final String Note3_Owner = User2_Email;

    // Test Note 4
    private static final String Note4_Name = "Maths Week 2";
    private static final String Note4_Tags = "Math";
    private static final String Note4_Text = "Ducks have eyes but not all eyes have a duck. Quack.";
    private static final String Note4_Owner = User2_Email;

    // Test Note 5
    private static final String Note5_Name = "DELETE ME!";
    private static final String Note5_Tags = "";
    private static final String Note5_Text = "I meant to load a note not make this ugh! Whatever shall I do!";
    private static final String Note5_Owner = User1_Email;

    // Test Note 6
    private static final String Note6_Name = "DELETE ME!";
    private static final String Note6_Tags = "";
    private static final String Note6_Text = "I meant to load a note not make this ugh! Whatever shall I do!";
    private static final String Note6_Owner = User2_Email;

    /**
     * Test Initialisations
     */
    private INoteDAO NotesDAO;
    private IUserDAO UserDAO;

    private User User1;
    private User User2;
    private List<User> userList;

    private Note Note1;
    private Note Note2;
    private Note Note3;
    private Note Note4;
    private Note Note5;
    private Note Note6;
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


        // Initialise test notes
        Note1 = new Note(Note1_Name, Note1_Tags, Note1_Text_Before, Note1_Owner);
        Note2 = new Note(Note2_Name, Note2_Tags, Note2_Text, Note2_Owner);
        Note3 = new Note(Note3_Name, Note3_Tags, Note3_Text_Before, Note3_Owner);
        Note4 = new Note(Note4_Name, Note4_Tags, Note4_Text, Note4_Owner);
        Note5 = new Note(Note5_Name, Note5_Tags, Note5_Text, Note5_Owner);
        Note6 = new Note(Note6_Name, Note6_Tags, Note6_Text, Note6_Owner);

    }

    /**
     * Test Method Overview
     *
     * Test Case 1:
     *      User1 Logs in, creates a note, saves, logs out.
     *      Success if User1's note has been retained.
     *
     * Test Case 2:
     *      User1 Logs in, creates a note, saves.
     *      User1 deletes the note, logs out.
     *      Success if User1;s note has been deleted
     *
     * Test Case 3:
     *      User1 logs in, creates a note, saves, logs out.
     *      User2 logs in, creates a note, saves, logs out.
     *      User1 logs in, views note, logs out.
     *      User2 logs in, views note, logs out.
     *      Success if User1's and User2's notes have been retained.
     *
     * Test Case 4:
     *      User1 logs in, creates a note, saves, logs out.
     *      User2 logs in, creates a note, saves, logs out.
     *      User1 logs in, deletes their note, logs out.
     *      User2 logs in, views note, logs out.
     *      Success if User1's note has been deleted but User2's note has been retained.
     *
     *  Test Case 5:
     *      User1 Logs in, creates a note, saves, logs out.
     *      User2 logs in, creates a note, saves, logs out.
     *      User1 Logs in, views note, edits the note, saves, logs out.
     *      User2 Logs in, views note, edits the note, saves, logs out.
     *      Success if User1's and User2's notes and edits have been retained.
     *
     *  Test Case 6:
     *      User1 logs in, creates a note, saves, logs out.
     *      User2 logs in, coincidentally creates a note of the same name, saves and logs out.
     *      User1 logs back in, loads their note, edits the note, saves, logs out.
     *      User2 logs back in, loads their note, edits the note, saves, logs out.
     *      Success if both text fields retain changes only made by their owners.
     *
     */

    @Test
    public void testCase1() {
        // User1 logs in
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());
        assertEquals(User1_Email, Session.getLoggedInEmail());

        // User1 Creates a note
        NotesDAO.addNote(Note1);
        List<Note> user1Notes = NotesDAO.getNotesByOwner(Session.getLoggedInEmail());
        assertEquals(1, user1Notes.size());
        assertEquals(User1_Email, user1Notes.getFirst().getNoteOwner());

        // User1 logs out
        Session.clear();
        assertNull(Session.getLoggedInEmail());

        // Forget User1's note from local test memory
        user1Notes.clear();

        // Get User1's notes now they are logged out
        user1Notes = NotesDAO.getNotesByOwner(User1_Email);

        // Check if success criteria met
        assertEquals(Note1.getNoteText(), user1Notes.getFirst().getNoteText());
    }

    @Test
    public void testCase2() {
        // User1 logs in
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());
        assertEquals(User1_Email, Session.getLoggedInEmail());

        // User1 creates a note
        NotesDAO.addNote(Note5);
        List<Note> user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        assertEquals(1, user1Notes.size());
        assertEquals(Note5_Name, user1Notes.getFirst().getNoteName());

        // User1 deletes the note
        NotesDAO.deleteNote(Note5);

        // User1 logs out
        Session.clear();
        assertNull(Session.getLoggedInEmail());

        // Verrify Success criteria (Ensure no notes remain)
        user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        assertTrue(user1Notes.isEmpty());
    }

    @Test
    public void testCase3() {
        // User1 logs in
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());

        // User1 creates a note
        NotesDAO.addNote(Note1);
        List<Note> user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        assertEquals(1, user1Notes.size());
        assertEquals(Note1.getNoteText(), user1Notes.getFirst().getNoteText());

        // User1 logs out
        Session.clear();

        // User2 logs in
        Session.setUser(User2.getEmail(), User2.getFirstName(), User2.getLastName());
        assertEquals(User2_Email, Session.getLoggedInEmail());

        // User2 creates a note
        NotesDAO.addNote(Note3);
        List<Note> user2Notes = NotesDAO.getNotesByOwner(User2_Email);
        assertEquals(1, user2Notes.size());
        assertEquals(Note3_Name, user2Notes.getFirst().getNoteName());

        // User2 logs out
        Session.clear();

        // Verify no user logged in
        assertNull(Session.getLoggedInEmail());

        // Verify Success criteria (User1's note persists)
        user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        assertEquals(1, user1Notes.size());
        assertEquals(Note1_Name, user1Notes.getFirst().getNoteName());

        // Verify Success criteria (User2's note persists)
        user2Notes = NotesDAO.getNotesByOwner(User2_Email);
        assertEquals(1, user2Notes.size());
        assertEquals(Note3_Name, user2Notes.getFirst().getNoteName());
    }

    @Test
    public void testCase4() {
        // User1 logs in
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());

        // User1 creates a note
        NotesDAO.addNote(Note1);
        List<Note> user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        assertEquals(1, user1Notes.size());
        assertEquals(Note1.getNoteText(), user1Notes.getFirst().getNoteText());

        // User1 logs out
        Session.clear();

        // User2 logs in
        Session.setUser(User2.getEmail(), User2.getFirstName(), User2.getLastName());

        // User2 creates a ntoe
        NotesDAO.addNote(Note3);
        List<Note> user2Notes = NotesDAO.getNotesByOwner(User2_Email);
        assertEquals(1, user2Notes.size());
        assertEquals(Note3.getNoteText(), user2Notes.getFirst().getNoteText());

        // User2 logs out
        Session.clear();

        // User1 logs in
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());

        // User1 deletes their note
        NotesDAO.deleteNote(Note1);

        // User1 logs out
        Session.clear();

        // Verify Success criteria (User1's note iss deleted)
        user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        assertTrue(user1Notes.isEmpty());

        // Verify success criteria (User2's note still exists)
        user2Notes = NotesDAO.getNotesByOwner(User2_Email);
        assertEquals(1, user2Notes.size());
        assertEquals(Note3.getNoteText(), user2Notes.getFirst().getNoteText());

    }

    @Test
    public void testCase5() {
        // User1 logs in
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());

        // User1 creates a note
        NotesDAO.addNote(Note1);
        List<Note> user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        assertEquals(Note1_Text_Before, user1Notes.getFirst().getNoteText());

        // User1 logs out
        Session.clear();

        // User2 logs in
        Session.setUser(User2.getEmail(), User2.getFirstName(), User2.getLastName());

        // User2 creates a note
        NotesDAO.addNote(Note3);
        List<Note> user2Notes = NotesDAO.getNotesByOwner(User2_Email);
        assertEquals(Note3_Text_Before, user2Notes.getFirst().getNoteText());

        // User2 logs out
        Session.clear();

        // User1 logs back in
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());

        // User1 edits note
        Note1.setNoteText(Note1_Text_After);
        NotesDAO.updateNote(Note1);
        user1Notes = NotesDAO.getNotesByOwner(User1_Email);

        // User1 logs back out
        Session.clear();

        // User2 logs back in
        Session.setUser(User2.getEmail(), User2.getFirstName(), User2.getLastName());


        // User2's edits note
        Note3.setNoteText(Note3_Text_After);
        NotesDAO.updateNote(Note3);
        user2Notes = NotesDAO.getNotesByOwner(User2_Email);

        // User2 logs out
        Session.clear();

        // Verify success criteria (User1's edits have been retained)
        assertEquals(Note1_Text_After, user1Notes.getFirst().getNoteText());

        // Verify success criteria (User2's edits have been retained
        assertEquals(Note3_Text_After, user2Notes.getFirst().getNoteText());



    }

    @Test
    public void testCase6() {
        // User1 logs in and creates a note
        Session.setUser(User1.getEmail(), User1.getFirstName(), User1.getLastName());
        NotesDAO.addNote(Note5); // "DELETE ME!" owned by User1
        Note5.setNoteText(Note1_Text_After); // Edit User1's note
        NotesDAO.updateNote(Note5);
        Session.clear();

        // User2 logs in and creates a note with the same name
        Session.setUser(User2.getEmail(), User2.getFirstName(), User2.getLastName());
        NotesDAO.addNote(Note6); // "DELETE ME!" owned by User2
        Note6.setNoteText(Note3_Text_After); // Edit User2's note
        NotesDAO.updateNote(Note6);
        Session.clear();

        // Verify success criteria (User1's note was retained and edited correctly)
        List<Note> user1Notes = NotesDAO.getNotesByOwner(User1_Email);
        assertEquals(1, user1Notes.size());
        assertEquals(Note5.getNoteText(), user1Notes.getFirst().getNoteText());

        // Verify success criteria (User2's note was retained and edited correctly)
        List<Note> user2Notes = NotesDAO.getNotesByOwner(User2_Email);
        assertEquals(1, user2Notes.size());
        assertEquals(Note6.getNoteText(), user2Notes.getFirst().getNoteText());
    }

    /**
     * Further test case ideas
     * try delete non existent note
     * try load note after deleting it
     * try delete note as different user to note owner
     * try load note as different user to note owner
     */





}

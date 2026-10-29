package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NoteTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Note(null));
    }

    @Test
    public void getErrorMessage() {
        assertEquals(Note.MESSAGE_CONSTRAINTS + " This note has 231, so shorten it by 31.",
                Note.getErrorMessage("a".repeat(231)));

        // one character over the limit
        assertEquals(Note.MESSAGE_CONSTRAINTS + " This note has 201, so shorten it by 1.",
                Note.getErrorMessage("a".repeat(Note.MAX_LENGTH + 1)));
    }

    @Test
    public void constructor_invalidNote_throwsIllegalArgumentException() {
        String tooLongNote = "a".repeat(Note.MAX_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, () -> new Note(tooLongNote));
    }

    @Test
    public void normalizeNote() {
        // null note
        assertThrows(NullPointerException.class, () -> Note.normalizeNote(null));

        // whitespace trimmed and collapsed
        assertEquals("Weak at algebra", Note.normalizeNote("  Weak   at\talgebra \n"));
        assertEquals("", Note.normalizeNote("  \t ")); // whitespace only

        // already normalized -> unchanged
        assertEquals("Weak at algebra, exam in March", Note.normalizeNote("Weak at algebra, exam in March"));
    }

    @Test
    public void isValidNote() {
        // null note
        assertThrows(NullPointerException.class, () -> Note.isValidNote(null));

        // valid notes
        assertTrue(Note.isValidNote("")); // empty note means no note
        assertTrue(Note.isValidNote("Weak at algebra, exam in March"));
        assertTrue(Note.isValidNote("Maths/Science 50% off (sibling discount)")); // symbols are fine
        assertTrue(Note.isValidNote("a".repeat(Note.MAX_LENGTH))); // exactly at the limit

        // invalid notes
        assertFalse(Note.isValidNote("a".repeat(Note.MAX_LENGTH + 1)));
    }

    @Test
    public void isEmpty() {
        assertTrue(Note.EMPTY.isEmpty());
        assertTrue(new Note("").isEmpty());
        assertFalse(new Note("Weak at algebra").isEmpty());
    }

    @Test
    public void equals() {
        Note note = new Note("Weak at algebra");

        // same values -> returns true
        assertTrue(note.equals(new Note("Weak at algebra")));

        // same object -> returns true
        assertTrue(note.equals(note));

        // null -> returns false
        assertFalse(note.equals(null));

        // different types -> returns false
        assertFalse(note.equals(5.0f));

        // different values -> returns false
        assertFalse(note.equals(new Note("Strong at algebra")));
        assertFalse(note.equals(Note.EMPTY));
    }

    @Test
    public void hashCodeMethod() {
        Note note = new Note("Weak at algebra");

        // same values -> same hash code
        assertEquals(note.hashCode(), new Note("Weak at algebra").hashCode());

        // different values -> different hash code
        assertNotEquals(note.hashCode(), Note.EMPTY.hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("Weak at algebra", new Note("Weak at algebra").toString());
        assertEquals("", Note.EMPTY.toString());
    }
}

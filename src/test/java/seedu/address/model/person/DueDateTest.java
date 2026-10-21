package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class DueDateTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DueDate(null));
    }

    @Test
    public void constructor_invalidDueDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new DueDate("tomorrow"));
        assertThrows(IllegalArgumentException.class, () -> new DueDate("30-02-2026"));
    }

    @Test
    public void normalizeDueDate() {
        // null due date
        assertThrows(NullPointerException.class, () -> DueDate.normalizeDueDate(null));

        // whitespace trimmed
        assertEquals("01-11-2026", DueDate.normalizeDueDate("  01-11-2026 \t"));
        assertEquals("", DueDate.normalizeDueDate("   ")); // whitespace only

        // already normalized -> unchanged
        assertEquals("01-11-2026", DueDate.normalizeDueDate("01-11-2026"));
    }

    @Test
    public void isValidDueDate() {
        // null due date
        assertThrows(NullPointerException.class, () -> DueDate.isValidDueDate(null));

        // valid due dates
        assertTrue(DueDate.isValidDueDate("")); // empty due date means none set
        assertTrue(DueDate.isValidDueDate("01-11-2026"));
        assertTrue(DueDate.isValidDueDate("29-02-2024")); // leap day
        assertTrue(DueDate.isValidDueDate("01-01-2020")); // past dates are allowed

        // invalid due dates
        assertFalse(DueDate.isValidDueDate(" ")); // spaces only
        assertFalse(DueDate.isValidDueDate("tomorrow"));
        assertFalse(DueDate.isValidDueDate("30-02-2026")); // not a real date
        assertFalse(DueDate.isValidDueDate("29-02-2025")); // not a leap year
        assertFalse(DueDate.isValidDueDate("01-13-2026")); // no month 13
        assertFalse(DueDate.isValidDueDate("1-1-2026")); // missing zero padding
        assertFalse(DueDate.isValidDueDate("2026-11-01")); // wrong order
        assertFalse(DueDate.isValidDueDate("01/11/2026")); // wrong separator
    }

    @Test
    public void isEmpty() {
        assertTrue(DueDate.EMPTY.isEmpty());
        assertTrue(new DueDate("").isEmpty());
        assertFalse(new DueDate("01-11-2026").isEmpty());
    }

    @Test
    public void equals() {
        DueDate dueDate = new DueDate("01-11-2026");

        // same values -> returns true
        assertTrue(dueDate.equals(new DueDate("01-11-2026")));

        // same object -> returns true
        assertTrue(dueDate.equals(dueDate));

        // null -> returns false
        assertFalse(dueDate.equals(null));

        // different types -> returns false
        assertFalse(dueDate.equals(5.0f));

        // different values -> returns false
        assertFalse(dueDate.equals(new DueDate("02-11-2026")));
        assertFalse(dueDate.equals(DueDate.EMPTY));
    }

    @Test
    public void hashCodeMethod() {
        DueDate dueDate = new DueDate("01-11-2026");

        // same values -> same hash code
        assertEquals(dueDate.hashCode(), new DueDate("01-11-2026").hashCode());

        // different values -> different hash code
        assertNotEquals(dueDate.hashCode(), DueDate.EMPTY.hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("01-11-2026", new DueDate("01-11-2026").toString());
        assertEquals("", DueDate.EMPTY.toString());
    }
}

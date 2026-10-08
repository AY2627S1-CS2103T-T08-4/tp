package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains disallowed symbol
        assertFalse(Name.isValidName("12345")); // numbers only
        assertFalse(Name.isValidName("2nd Peter")); // starts with a digit
        assertFalse(Name.isValidName("-Alex")); // starts with a hyphen
        assertFalse(Name.isValidName("'Alex")); // starts with an apostrophe
        assertFalse(Name.isValidName(" Alex")); // starts with a space
        assertFalse(Name.isValidName("Siti a/l Rahman")); // '/' is the prefix delimiter
        assertFalse(Name.isValidName("Rajesh s\\o Kumar")); // backslash
        assertFalse(Name.isValidName("a".repeat(Name.MAX_LENGTH + 1))); // one character over the limit

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("A")); // single letter
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("Liam O'Brien")); // apostrophe
        assertTrue(Name.isValidName("Nur-Aisyah Tan")); // hyphen
        assertTrue(Name.isValidName("David Ray Jr.")); // full stop
        assertTrue(Name.isValidName("A. Kumar")); // initial with full stop
        assertTrue(Name.isValidName("a".repeat(Name.MAX_LENGTH))); // exactly at the limit
    }

    @Test
    public void normalizeName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.normalizeName(null));

        // whitespace trimmed and collapsed
        assertEquals("Liam O'Brien", Name.normalizeName("  Liam   O'Brien ")); // leading, trailing and internal
        assertEquals("Liam O'Brien", Name.normalizeName("Liam\tO'Brien")); // tab
        assertEquals("", Name.normalizeName(" \t ")); // whitespace only

        // already normalized -> unchanged
        assertEquals("Nur-Aisyah Tan", Name.normalizeName("Nur-Aisyah Tan"));
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}

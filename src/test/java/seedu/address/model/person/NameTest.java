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
        assertFalse(Name.isValidName("(NHHS) John Tan")); // starts with a parenthesis
        assertFalse(Name.isValidName("Rajesh s\\o Kumar")); // backslash
        assertFalse(Name.isValidName("John\tTan")); // whitespace other than a space, which normalizeName replaces
        assertFalse(Name.isValidName("José Tan")); // accented letter, not supported (see the UG)
        assertFalse(Name.isValidName("陈伟明")); // starts with a Chinese character, not supported (see the UG)
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
        assertTrue(Name.isValidName("John Tan (NHHS)")); // identifier in parentheses
        assertTrue(Name.isValidName("a".repeat(Name.MAX_LENGTH))); // exactly at the limit
    }

    @Test
    public void normalizeName_extraWhitespaceAndCurlyApostrophe_bothNormalized() {
        // normalizeName only calls StringUtil.normalizeWhitespace and normalizeApostrophes, whose partitions
        // StringUtilTest and normalizeApostrophes below cover
        assertEquals("Liam O'Brien", Name.normalizeName("  Liam \t O\u2019Brien "));
    }

    @Test
    public void messageConstraints_writesPunctuationInWordsOnly() {
        assertTrue(Name.MESSAGE_CONSTRAINTS.contains("hyphens, apostrophes, full stops and parentheses"));
        // Angle brackets around each character could be read as allowed characters too
        assertFalse(Name.MESSAGE_CONSTRAINTS.contains("<"));
    }

    @Test
    public void getErrorMessage() {
        // only too long: the length is given instead of the character rules
        assertEquals("Invalid Name! Names cannot be more than 50 characters, but this one has 51.",
                Name.getErrorMessage("a".repeat(Name.MAX_LENGTH + 1)));

        // a character that is not allowed, whatever the length
        assertEquals(Name.MESSAGE_CONSTRAINTS, Name.getErrorMessage("Ravi s/o Kumar"));
        assertEquals(Name.MESSAGE_CONSTRAINTS, Name.getErrorMessage("a".repeat(Name.MAX_LENGTH) + "/"));
    }

    @Test
    public void normalizeApostrophes() {
        // each apostrophe-like character, written as an escape as it looks like ' in most fonts, becomes '
        assertEquals("O'Brien", Name.normalizeApostrophes("O\u2019Brien")); // right single quote, the usual one
        assertEquals("O'Brien", Name.normalizeApostrophes("O\u2018Brien")); // left single quote
        assertEquals("O'Brien", Name.normalizeApostrophes("O\u02BCBrien")); // modifier letter apostrophe
        assertEquals("O'Brien", Name.normalizeApostrophes("O\u2032Brien")); // prime

        // every one in the text is replaced
        assertEquals("'a'b'", Name.normalizeApostrophes("\u2018a\u2019b\u2019"));

        // text that already uses the straight ' is unchanged, and double quotes are not apostrophes
        assertEquals("O'Brien \"Jr\"", Name.normalizeApostrophes("O'Brien \"Jr\""));

        assertThrows(NullPointerException.class, () -> Name.normalizeApostrophes(null));
    }

    @Test
    public void isSameName() {
        Name name = new Name("John Tan");

        // same name, ignoring case -> returns true
        assertTrue(name.isSameName(new Name("John Tan")));
        assertTrue(name.isSameName(new Name("john tan")));
        assertTrue(name.isSameName(new Name("JOHN TAN")));

        // null -> returns false
        assertFalse(name.isSameName(null));

        // different name -> returns false
        assertFalse(name.isSameName(new Name("John Tan (NHHS)")));
        assertFalse(name.isSameName(new Name("Jon Tan")));
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

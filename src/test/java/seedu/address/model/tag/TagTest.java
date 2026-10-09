package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));

        // invalid tag names
        assertFalse(Tag.isValidTagName(""));
        assertFalse(Tag.isValidTagName(" "));
        assertFalse(Tag.isValidTagName("physics!"));
        assertFalse(Tag.isValidTagName("a".repeat(Tag.MAX_LENGTH + 1)));

        // valid tag names
        assertTrue(Tag.isValidTagName("physics"));
        assertTrue(Tag.isValidTagName("Secondary 4"));
        assertTrue(Tag.isValidTagName("a".repeat(Tag.MAX_LENGTH)));
    }

    @Test
    public void equals_tagsDifferOnlyInCase_returnsTrue() {
        Tag lowercaseTag = new Tag("physics");
        Tag uppercaseTag = new Tag("PHYSICS");

        assertEquals(lowercaseTag, uppercaseTag);
        assertEquals(lowercaseTag.hashCode(), uppercaseTag.hashCode());
    }

}

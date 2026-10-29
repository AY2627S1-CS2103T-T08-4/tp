package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;

public class JsonAdaptedTagTest {

    @Test
    public void toModelType_repeatedInnerSpaces_returnsTagWithSingleSpaces() throws Exception {
        assertEquals("Sec 4", new JsonAdaptedTag("Sec  4").toModelType().tagName);
    }

    @Test
    public void toModelType_invalidTagName_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, () -> new JsonAdaptedTag("#friend").toModelType());
    }

    @Test
    public void toModelType_nullTagName_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, () -> new JsonAdaptedTag((String) null).toModelType());
    }
}

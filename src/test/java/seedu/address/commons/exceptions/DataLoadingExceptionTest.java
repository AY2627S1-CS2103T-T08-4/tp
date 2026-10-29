package seedu.address.commons.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.JsonUtil;

public class DataLoadingExceptionTest {

    @Test
    public void getReason_nullMessage_returnsUnknownReason() {
        assertEquals(DataLoadingException.MESSAGE_UNKNOWN_REASON,
            new DataLoadingException(new IllegalValueException(null)).getReason());
    }

    @Test
    public void getReason_blankFirstLine_returnsUnknownReason() {
        assertEquals(DataLoadingException.MESSAGE_UNKNOWN_REASON,
            new DataLoadingException(new IllegalValueException("\nMore detail")).getReason());
    }

    @Test
    public void getReason_endsWithExclamationOrQuestion_notGivenExtraFullStop() {
        assertEquals("Bad phone!", new DataLoadingException(new IllegalValueException("Bad phone!")).getReason());
        assertEquals("Bad phone?", new DataLoadingException(new IllegalValueException("Bad phone?")).getReason());
    }

    @Test
    public void getReason_validationMessage_returnedAsSentence() {
        assertEquals("Bad phone.", new DataLoadingException(new IllegalValueException("Bad phone.")).getReason());
        assertEquals("Bad phone.", new DataLoadingException(new IllegalValueException("Bad phone")).getReason());
    }

    @Test
    public void getReason_multiLineMessage_usesFirstLine() {
        DataLoadingException e = new DataLoadingException(new IllegalValueException("Bad phone.\nMore detail"));
        assertEquals("Bad phone.", e.getReason());
    }

    @Test
    public void getReason_blankMessage_returnsUnknownReason() {
        assertEquals(DataLoadingException.MESSAGE_UNKNOWN_REASON,
            new DataLoadingException(new IllegalValueException("  ")).getReason());
    }

    @Test
    public void getReason_invalidJson_returnsBadFormatReason() {
        IOException cause = assertThrows(IOException.class, () -> JsonUtil.fromJsonString("{ invalid", Object.class));
        assertEquals(DataLoadingException.MESSAGE_BAD_FORMAT, new DataLoadingException(cause).getReason());
    }

    @Test
    public void getReason_otherException_returnsUnknownReason() {
        assertEquals(DataLoadingException.MESSAGE_UNKNOWN_REASON,
            new DataLoadingException(new Exception("technical detail")).getReason());
    }
}

package seedu.address.commons.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Represents an error during loading of data from a file.
 */
public class DataLoadingException extends Exception {

    public static final String MESSAGE_UNKNOWN_REASON = "The file could not be read.";
    public static final String MESSAGE_BAD_FORMAT = "The file is not valid JSON or is not in the expected format.";

    public DataLoadingException(Exception cause) {
        super(cause);
    }

    /**
     * Returns a short sentence telling the user what is wrong with the file.
     * Validation messages are shown as they are, and technical errors are replaced by a general reason.
     */
    public String getReason() {
        Throwable cause = getCause();
        if (cause instanceof IllegalValueException) {
            return getFirstLineAsSentence(cause.getMessage());
        }
        if (cause instanceof JsonProcessingException) {
            return MESSAGE_BAD_FORMAT;
        }
        return MESSAGE_UNKNOWN_REASON;
    }

    private static String getFirstLineAsSentence(String message) {
        if (message == null || message.isBlank()) {
            return MESSAGE_UNKNOWN_REASON;
        }
        String firstLine = message.lines().findFirst().orElse("").strip();
        if (firstLine.isEmpty()) {
            return MESSAGE_UNKNOWN_REASON;
        }
        boolean hasEndingPunctuation = firstLine.endsWith(".") || firstLine.endsWith("!")
            || firstLine.endsWith("?");
        return hasEndingPunctuation ? firstLine : firstLine + ".";
    }
}

package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/**
 * Represents a Person's free-text note in the address book.
 * An empty note means the client has no note.
 * Guarantees: immutable; is valid as declared in {@link #isValidNote(String)}
 */
public class Note {

    public static final int MAX_LENGTH = 200;
    public static final String MESSAGE_CONSTRAINTS = String.format(
            "Invalid Note! Notes cannot be more than %d characters.", MAX_LENGTH);
    public static final Note EMPTY = new Note("");

    public final String value;

    /**
     * Constructs a {@code Note}.
     *
     * @param note A valid note, which may be empty.
     */
    public Note(String note) {
        requireNonNull(note);
        checkArgument(isValidNote(note), MESSAGE_CONSTRAINTS);
        value = note;
    }

    /**
     * Returns {@code note} trimmed, with consecutive whitespaces collapsed into a single space.
     * The result may still be an invalid note.
     *
     * @param note A note as typed by the user.
     */
    public static String normalizeNote(String note) {
        return StringUtil.normalizeWhitespace(note);
    }

    /**
     * Returns true if a given string is a valid note.
     */
    public static boolean isValidNote(String test) {
        requireNonNull(test);
        return test.length() <= MAX_LENGTH;
    }

    /**
     * Returns true if this note is empty.
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Note otherNote)) {
            return false;
        }

        return value.equals(otherNote.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}

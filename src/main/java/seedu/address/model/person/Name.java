package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MAX_LENGTH = 50;
    public static final String MESSAGE_CONSTRAINTS = String.format(
            "Invalid Name! Names must start with a letter and can only contain letters, digits, spaces, "
                    + "hyphens, apostrophes, full stops and parentheses, up to %d characters.",
            MAX_LENGTH);

    /**
     * Punctuation allowed in a name, for use inside a regex character class.
     * Find ignores these when matching, so "obrien" matches "O'Brien".
     */
    public static final String NAME_PUNCTUATION = "'.()\\-";

    /**
     * Characters allowed in a name apart from spaces, for use inside a regex character class.
     * Shared with find so that search keywords accept the same characters as names.
     */
    public static final String NAME_CHARACTERS = "A-Za-z0-9" + NAME_PUNCTUATION;

    /*
     * The first character of the name must be a letter,
     * otherwise " " (a blank string) or "-" becomes a valid input.
     * Letters, digits, spaces, hyphens, apostrophes, full stops and parentheses may follow,
     * e.g. "Nur-Aisyah", "O'Brien", "David Ray Jr. 2nd" or "John Tan (NHHS)".
     * '/' is excluded as it is the prefix delimiter (e.g. "a/l" would be read as an address).
     */
    public static final String VALIDATION_REGEX = "[A-Za-z][" + NAME_CHARACTERS + " ]*";

    private static final String MESSAGE_TOO_LONG =
            "Invalid Name! Names cannot be more than %1$d characters, but this one has %2$d.";

    /**
     * Apostrophe-like characters that phones and word processors put in place of {@code '},
     * e.g. in "O’Brien" copied from a chat. Right and left single quotes, the modifier letter apostrophe
     * and the prime.
     */
    private static final String APOSTROPHE_LIKE_REGEX = "[\u2018\u2019\u02BC\u2032]";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns {@code name} trimmed, with consecutive whitespaces collapsed into a single space,
     * and apostrophe-like characters replaced as in {@link #normalizeApostrophes(String)}.
     * The result may still be an invalid name.
     *
     * @param name A name as typed by the user.
     */
    public static String normalizeName(String name) {
        return normalizeApostrophes(StringUtil.normalizeWhitespace(name));
    }

    /**
     * Returns {@code text} with every apostrophe-like character, such as the curly {@code ’},
     * replaced by a straight apostrophe {@code '}, so that "O’Brien" is read as "O'Brien".
     */
    public static String normalizeApostrophes(String text) {
        requireNonNull(text);
        return text.replaceAll(APOSTROPHE_LIKE_REGEX, "'");
    }

    /**
     * Returns the error message for the invalid name {@code invalidName}. A name that is only too long gets a
     * message with its length, so that the tutor is not sent to check characters that are all allowed.
     */
    public static String getErrorMessage(String invalidName) {
        requireNonNull(invalidName);
        assert !isValidName(invalidName) : "only an invalid name has an error message";
        if (invalidName.matches(VALIDATION_REGEX)) {
            return String.format(MESSAGE_TOO_LONG, MAX_LENGTH, invalidName.length());
        }
        return MESSAGE_CONSTRAINTS;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        return test.matches(VALIDATION_REGEX) && test.length() <= MAX_LENGTH;
    }

    /**
     * Returns true if {@code other} is the same name as this name, ignoring case.
     */
    public boolean isSameName(Name other) {
        return other != null && fullName.equalsIgnoreCase(other.fullName);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}

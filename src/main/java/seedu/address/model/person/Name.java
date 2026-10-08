package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MAX_LENGTH = 50;
    public static final String MESSAGE_CONSTRAINTS = String.format(
            "Invalid Name! Names must start with a letter and can only contain letters, digits, spaces, "
                    + "hyphens (-), apostrophes (') and full stops (.), up to %d characters.",
            MAX_LENGTH);

    /*
     * The first character of the name must be a letter,
     * otherwise " " (a blank string) or "-" becomes a valid input.
     * Letters, digits, spaces, hyphens, apostrophes and full stops may follow,
     * e.g. "Nur-Aisyah", "O'Brien" or "David Ray Jr. 2nd".
     * '/' is excluded as it is the prefix delimiter (e.g. "a/l" would be read as an address).
     */
    public static final String VALIDATION_REGEX = "[A-Za-z][A-Za-z0-9 '.\\-]*";

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
     * Returns {@code name} trimmed, with consecutive whitespaces collapsed into a single space.
     * The result may still be an invalid name.
     *
     * @param name A name as typed by the user.
     */
    public static String normalizeName(String name) {
        requireNonNull(name);
        return name.trim().replaceAll("\\s+", " ");
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        return test.matches(VALIDATION_REGEX) && test.length() <= MAX_LENGTH;
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

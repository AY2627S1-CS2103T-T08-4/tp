package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid Phone Number! Enter 8 digits (e.g. 91234567).";
    public static final String VALIDATION_REGEX = "\\d{8}";

    // Formatting characters users commonly type in phone numbers, e.g. "9123 4567" or "(9123)-4567".
    public static final String FORMATTING_CHARACTERS_REGEX = "[\\s\\-()]";

    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns {@code phone} with all whitespace, hyphens and parentheses removed.
     * All other characters are kept, so the result may still be an invalid phone number.
     *
     * @param phone A phone number as typed by the user.
     */
    public static String stripPhone(String phone) {
        requireNonNull(phone);
        return phone.replaceAll(FORMATTING_CHARACTERS_REGEX, "");
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX);
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
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}

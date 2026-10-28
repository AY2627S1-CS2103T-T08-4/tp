package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid Phone Number! Enter an 8-digit Singapore number starting with 3, 6, 8 or 9 (e.g. 91234567).";
    public static final String MESSAGE_COUNTRY_CODE = "Invalid Phone Number! Leave out the country code. "
            + "Sieve only stores Singapore numbers, so enter just the 8 digits (e.g. 91234567, not +65 9123 4567).";
    // Singapore numbers start with 3 (VoIP), 6 (landline), 8 or 9 (mobile)
    public static final String VALIDATION_REGEX = "[3689]\\d{7}";

    // Formatting characters users commonly type in phone numbers, e.g. "9123 4567" or "(9123)-4567".
    private static final String FORMATTING_CHARACTERS_REGEX = "[\\s\\-()]";
    // A number with a country code, e.g. "+6591234567", "+14155552671", or "6591234567" without the "+"
    private static final String COUNTRY_CODE_REGEX = "\\+.*|65\\d{8}";

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

    /**
     * Returns true if {@code strippedPhone}, a phone number with its formatting characters removed,
     * starts with a country code, which Sieve's phone numbers leave out.
     */
    public static boolean hasCountryCode(String strippedPhone) {
        return strippedPhone.matches(COUNTRY_CODE_REGEX);
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

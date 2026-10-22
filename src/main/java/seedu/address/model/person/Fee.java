package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.BigDecimal;

/**
 * Represents a Person's tuition fee in the address book.
 * An empty fee means the client has no fee set.
 * Guarantees: immutable; is valid as declared in {@link #isValidFee(String)}
 */
public class Fee {

    public static final String VALIDATION_REGEX = "\\d{1,7}(\\.\\d{1,2})?";
    public static final String MESSAGE_CONSTRAINTS = "Invalid Fee! Fees must be a non-negative amount with "
        + "at most 7 digits and 2 decimal places, for example 250 or 250.50.";

    public static final Fee EMPTY = new Fee("");

    /** Either empty, or the amount with exactly 2 decimal places. */
    public final String value;

    /**
     * Constructs a {@code Fee}.
     *
     * @param fee A valid fee, which may be empty.
     */
    public Fee(String fee) {
        requireNonNull(fee);
        checkArgument(isValidFee(fee), MESSAGE_CONSTRAINTS);
        value = fee.isEmpty() ? "" : new BigDecimal(fee).setScale(2).toPlainString();
    }

    /**
     * Returns {@code fee} with leading and trailing whitespaces removed.
     * The result may still be an invalid fee.
     *
     * @param fee A fee as typed by the user.
     */
    public static String normalizeFee(String fee) {
        requireNonNull(fee);
        return fee.trim();
    }

    /**
     * Returns true if a given string is a valid fee.
     */
    public static boolean isValidFee(String test) {
        requireNonNull(test);
        return test.isEmpty() || test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if this fee is empty.
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
        if (!(other instanceof Fee otherFee)) {
            return false;
        }

        return value.equals(otherFee.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}

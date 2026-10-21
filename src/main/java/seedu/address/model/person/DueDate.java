package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents a Person's payment due date in the address book, in the format DD-MM-YYYY.
 * An empty due date means the client has no due date set.
 * Past dates are allowed, so that overdue payments can be recorded.
 * Guarantees: immutable; is valid as declared in {@link #isValidDueDate(String)}
 */
public class DueDate {

    public static final String MESSAGE_CONSTRAINTS =
        "Invalid Due date! Due dates must be a real date in the format DD-MM-YYYY, for example 01-11-2026.";

    public static final DueDate EMPTY = new DueDate("");

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    public final String value;

    /**
     * Constructs a {@code DueDate}.
     *
     * @param dueDate A valid due date, which may be empty.
     */
    public DueDate(String dueDate) {
        requireNonNull(dueDate);
        checkArgument(isValidDueDate(dueDate), MESSAGE_CONSTRAINTS);
        value = dueDate;
    }

    /**
     * Returns {@code dueDate} with leading and trailing whitespaces removed.
     * The result may still be an invalid due date.
     *
     * @param dueDate A due date as typed by the user.
     */
    public static String normalizeDueDate(String dueDate) {
        requireNonNull(dueDate);
        return dueDate.trim();
    }

    /**
     * Returns true if a given string is a valid due date.
     */
    public static boolean isValidDueDate(String test) {
        requireNonNull(test);
        if (test.isEmpty()) {
            return true;
        }
        try {
            LocalDate.parse(test, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns true if this due date is empty.
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
        if (!(other instanceof DueDate otherDueDate)) {
            return false;
        }

        return value.equals(otherDueDate.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}

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
 * A due date typed into add or edit cannot be in the past, but past dates already stored still load,
 * so a due date that passes does not make the data file invalid.
 * Guarantees: immutable; is valid as declared in {@link #isValidDueDate(String)}
 */
public class DueDate {

    /** Earliest year accepted, so that a mistyped year such as 0000 is caught. */
    public static final int MIN_YEAR = 2000;
    /** Latest year accepted, as the format has four digits for the year. */
    public static final int MAX_YEAR = 9999;

    public static final String MESSAGE_CONSTRAINTS =
        "Invalid Due date! Due dates must be a real date from 01-01-" + MIN_YEAR + " to 31-12-" + MAX_YEAR
            + ", in the format DD-MM-YYYY, for example 01-11-2026.";
    public static final String MESSAGE_PAST_DATE =
        "Invalid Due date! Due dates cannot be in the past. Use today's date or a later date.";

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
     * Returns true if a given string is a valid due date: empty, or a real calendar date in the format
     * DD-MM-YYYY whose year is between {@code MIN_YEAR} and {@code MAX_YEAR}. Past dates are valid.
     */
    public static boolean isValidDueDate(String test) {
        requireNonNull(test);
        if (test.isEmpty()) {
            return true;
        }
        try {
            int year = LocalDate.parse(test, FORMATTER).getYear();
            return year >= MIN_YEAR && year <= MAX_YEAR;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns true if {@code test} is empty, or a date that is today or later.
     * {@code test} must already be a valid due date.
     */
    public static boolean isTodayOrLater(String test) {
        requireNonNull(test);
        assert isValidDueDate(test);
        return test.isEmpty() || !LocalDate.parse(test, FORMATTER).isBefore(LocalDate.now());
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

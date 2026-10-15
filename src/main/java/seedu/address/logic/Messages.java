package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    private static final String MESSAGE_INDEX_OUT_OF_BOUNDS =
            "Error: The index provided is out of bounds! Current list only contains %1$s.";
    private static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$s found matching your search term(s).";
    private static final String MESSAGE_CLIENT_ALREADY_EXISTS =
            "This client already exists in Sieve: %1$s with the phone number %2$s. They may have been added before.";
    private static final String MESSAGE_NAME_ALREADY_USED =
            "A client named %1$s already exists in Sieve, with the phone number %2$s. "
                    + "Check that the name and phone numbers are correct. "
                    + "If this is a different client, add a detail to tell them apart, e.g. \"%1$s (NHHS)\".";

    /**
     * Returns {@code count} followed by "client" or "clients", e.g. "0 client", "1 client" or "3 clients".
     */
    public static String formatClientCount(int count) {
        return count + (count == 1 ? " client" : " clients");
    }

    /**
     * Returns an error message for an index beyond the end of a displayed list of {@code listSize} clients.
     */
    public static String getIndexOutOfBoundsMessage(int listSize) {
        return String.format(MESSAGE_INDEX_OUT_OF_BOUNDS, formatClientCount(listSize));
    }

    /**
     * Returns a message stating that {@code count} clients matched a search.
     */
    public static String getPersonsListedOverviewMessage(int count) {
        return String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, formatClientCount(count));
    }

    /**
     * Returns an error message stating that the {@code existing} client is already in Sieve.
     * Used when a client with the same name and phone number is added again.
     */
    public static String getClientAlreadyExistsMessage(Person existing) {
        return String.format(MESSAGE_CLIENT_ALREADY_EXISTS, existing.getName(), existing.getPhone());
    }

    /**
     * Returns an error message stating that the name of the {@code existing} client is already used.
     * Used when a client with the same name but a different phone number is added.
     */
    public static String getNameAlreadyUsedMessage(Person existing) {
        return String.format(MESSAGE_NAME_ALREADY_USED, existing.getName(), existing.getPhone());
    }

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

}

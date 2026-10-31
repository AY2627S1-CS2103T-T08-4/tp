package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses one or more comma-separated client indices.
 */
public final class IndexListParser {

    public static final String MESSAGE_DUPLICATE_INDEX =
            "Duplicate indexes are not allowed.";
    public static final String MESSAGE_MISSING_COMMA =
            "Indices must be separated by commas, e.g. delete 1, 2, 3.";

    private IndexListParser() {
    }

    /**
     * Parses one or more comma-separated indices.
     *
     * <p>For example, both {@code "1"} and {@code "1, 2, 3"} are accepted.
     * Spaces surrounding each index are ignored.
     *
     * @param arguments arguments containing one or more indices
     * @param usageMessage command-specific usage message
     * @return parsed indices in their original order
     * @throws ParseException if an index is missing, duplicated or invalid
     */
    public static List<Index> parse(
            String arguments, String usageMessage)
            throws ParseException {
        requireNonNull(arguments);
        requireNonNull(usageMessage);

        String[] indexStrings =
                arguments.trim().split(",", -1);

        List<Index> indexes = new ArrayList<>();
        Set<Integer> seenIndexes = new HashSet<>();

        for (String indexString : indexStrings) {
            String trimmedIndex = indexString.trim();

            if (trimmedIndex.isEmpty()) {
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        usageMessage));
            }
            if (trimmedIndex.split("\\s+").length > 1) {
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        MESSAGE_MISSING_COMMA));
            }

            Index index;

            try {
                index = ParserUtil.parseIndex(trimmedIndex);
            } catch (ParseException pe) {
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        pe.getMessage()), pe);
            }

            if (!seenIndexes.add(index.getZeroBased())) {
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        MESSAGE_DUPLICATE_INDEX));
            }

            indexes.add(index);
        }

        return List.copyOf(indexes);
    }
}

package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.BulkDeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses comma-separated indices for a BulkDeleteCommand.
 */
public class BulkDeleteCommandParser
        implements Parser<BulkDeleteCommand> {

    @Override
    public BulkDeleteCommand parse(String args) throws ParseException {
        String[] indexStrings = args.trim().split(",", -1);

        if (indexStrings.length < 2) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT,
                    BulkDeleteCommand.MESSAGE_USAGE));
        }

        List<Index> indexes = new ArrayList<>();
        Set<Integer> seenIndexes = new HashSet<>();

        for (String indexString : indexStrings) {
            Index index;

            try {
                index = ParserUtil.parseIndex(indexString);
            } catch (ParseException pe) {
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        BulkDeleteCommand.MESSAGE_USAGE), pe);
            }

            if (!seenIndexes.add(index.getZeroBased())) {
                throw new ParseException(String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        BulkDeleteCommand.MESSAGE_DUPLICATE_INDEX));
            }

            indexes.add(index);
        }

        return new BulkDeleteCommand(indexes);
    }
}

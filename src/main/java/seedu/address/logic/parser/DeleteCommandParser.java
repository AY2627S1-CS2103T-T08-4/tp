package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a {@code DeleteCommand}.
 */
public class DeleteCommandParser
        implements Parser<DeleteCommand> {

    /**
     * Parses the given arguments in the context of the delete command.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public DeleteCommand parse(String args) throws ParseException {
        if (args.trim().isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteCommand.MESSAGE_INDEX_NOT_PRESENT));
        }

        List<Index> targetIndexes = IndexListParser.parse(
                args, DeleteCommand.MESSAGE_USAGE);

        return new DeleteCommand(
                targetIndexes.toArray(Index[]::new));
    }
}

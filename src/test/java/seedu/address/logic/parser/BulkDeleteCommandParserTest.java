package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.BulkDeleteCommand;

public class BulkDeleteCommandParserTest {
    private BulkDeleteCommandParser parser = new BulkDeleteCommandParser();

    @Test
    public void parse_singleIndex_throwsParseException() {
        assertParseFailure(
                parser,
                "1",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        BulkDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_multipleValidIndexes_returnsBulkDeleteCommand() {
        assertParseSuccess(
                parser,
                "1, 2, 3",
                new BulkDeleteCommand(List.of(
                        Index.fromOneBased(1),
                        Index.fromOneBased(2),
                        Index.fromOneBased(3))));
    }

    @Test
    public void parse_multipleValidIndexesWithoutSpaces_returnsBulkDeleteCommand() {
        assertParseSuccess(
                parser,
                "1,2,3",
                new BulkDeleteCommand(List.of(
                        Index.fromOneBased(1),
                        Index.fromOneBased(2),
                        Index.fromOneBased(3))));
    }

    @Test
    public void parse_nonPositiveIndex_throwsParseException() {
        assertParseFailure(
                parser,
                "0,1",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        BulkDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateIndexes_throwsParseException() {
        assertParseFailure(
                parser,
                "1, 1",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        BulkDeleteCommand.MESSAGE_DUPLICATE_INDEX));
    }

    @Test
    public void parse_emptyIndex_throwsParseException() {
        assertParseFailure(
                parser,
                "1,,3",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        BulkDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_trailingComma_throwsParseException() {
        assertParseFailure(
                parser,
                "1,2,",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        BulkDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_leadingComma_throwsParseException() {
        assertParseFailure(
                parser,
                ",1,2",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        BulkDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(
                parser,
                "1,a,3",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        BulkDeleteCommand.MESSAGE_USAGE));
    }
}

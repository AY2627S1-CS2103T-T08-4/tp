package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occurs inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(
                parser,
                "1",
                new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_zeroIndex_throwsParseException() {
        assertParseFailure(
                parser,
                "0",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        ParserUtil.MESSAGE_INVALID_INDEX));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(
                parser,
                "a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ParserUtil.MESSAGE_INVALID_INDEX));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        assertParseFailure(
                parser,
                "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_INDEX_NOT_PRESENT));
    }

    @Test
    public void parse_multipleValidIndexes_returnsBulkDeleteCommand() {
        assertParseSuccess(
                parser,
                "1, 2, 3",
                new DeleteCommand(
                        Index.fromOneBased(1),
                        Index.fromOneBased(2),
                        Index.fromOneBased(3)));
    }

    @Test
    public void parse_multipleValidIndexesWithoutSpaces_returnsBulkDeleteCommand() {
        assertParseSuccess(
                parser,
                "1,2,3",
                new DeleteCommand(
                        Index.fromOneBased(1),
                        Index.fromOneBased(2),
                        Index.fromOneBased(3)));
    }

    @Test
    public void parse_nonPositiveIndex_throwsParseException() {
        assertParseFailure(
                parser,
                "0,1",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        ParserUtil.MESSAGE_INVALID_INDEX));
    }

    @Test
    public void parse_duplicateIndexes_throwsParseException() {
        assertParseFailure(
                parser,
                "1, 1",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        IndexListParser.MESSAGE_DUPLICATE_INDEX));
    }

    @Test
    public void parse_emptyIndex_throwsParseException() {
        assertParseFailure(
                parser,
                "1,,3",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_trailingComma_throwsParseException() {
        assertParseFailure(
                parser,
                "1,2,",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_leadingComma_throwsParseException() {
        assertParseFailure(
                parser,
                ",1,2",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(
                parser,
                "1,a,3",
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        ParserUtil.MESSAGE_INVALID_INDEX));
    }
}

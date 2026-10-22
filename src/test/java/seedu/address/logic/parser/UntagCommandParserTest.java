package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UntagCommand;
import seedu.address.model.tag.Tag;

public class UntagCommandParserTest {

    private final UntagCommandParser parser = new UntagCommandParser();

    @Test
    public void parse_validArgs_returnsUntagCommand() {
        assertParseSuccess(parser, "1 t/physics t/Secondary 4",
                new UntagCommand(INDEX_FIRST_PERSON, List.of(new Tag("physics"), new Tag("Secondary 4"))));
    }

    @Test
    public void parse_duplicateTags_returnsUntagCommandWithOneCopy() {
        assertParseSuccess(parser, "1 t/physics t/physics",
                new UntagCommand(INDEX_FIRST_PERSON, List.of(new Tag("physics"))));
    }

    @Test
    public void parse_missingTag_throwsParseException() {
        assertParseFailure(parser, "1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, UntagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(parser, "one t/physics",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, UntagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        assertParseFailure(parser, "1 t/physics!", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_emptyTag_throwsParseException() {
        assertParseFailure(parser, "1 t/", Tag.MESSAGE_CONSTRAINTS);
    }
}

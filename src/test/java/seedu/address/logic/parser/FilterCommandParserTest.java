package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.PersonMatchesSearchPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class FilterCommandParserTest {

    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_tags_returnsFilterCommand() {
        FilterCommand expectedCommand = new FilterCommand(
                new PersonMatchesSearchPredicate(List.of(), List.of(),
                        List.of(new Tag("Physics"), new Tag("Secondary 4"))));

        assertParseSuccess(parser, " t/Physics t/Secondary 4", expectedCommand);
    }

    @Test
    public void parse_phone_returnsFilterCommand() {
        FilterCommand expectedCommand = new FilterCommand(
                new PersonMatchesSearchPredicate(List.of(), List.of(new Phone("91234567")), List.of()));

        assertParseSuccess(parser, " p/9123 4567", expectedCommand);
    }

    @Test
    public void parse_multiplePhonesAndTags_returnsFilterCommand() {
        FilterCommand expectedCommand = new FilterCommand(new PersonMatchesSearchPredicate(List.of(),
                List.of(new Phone("91234567"), new Phone("98765432")),
                List.of(new Tag("Physics"), new Tag("Secondary 4"))));

        assertParseSuccess(parser,
                " p/91234567 p/98765432 t/Physics t/Secondary 4", expectedCommand);
    }

    @Test
    public void parse_missingFilter_throwsParseException() {
        assertParseFailure(parser, " ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_nonEmptyPreamble_throwsParseException() {
        assertParseFailure(parser, "ignored text t/Physics",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        assertParseFailure(parser, " t/Physics!", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidPhone_throwsParseException() {
        assertParseFailure(parser, " p/9123", Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_emptyPhone_throwsParseException() {
        assertParseFailure(parser, " p/", Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_emptyTag_throwsParseException() {
        assertParseFailure(parser, " t/", Tag.MESSAGE_CONSTRAINTS);
    }
}

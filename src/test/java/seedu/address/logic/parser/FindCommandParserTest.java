package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.PersonMatchesSearchPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new PersonMatchesSearchPredicate(List.of("Alice", "Bob"), List.of(), List.of()));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_nameCharacterKeywords_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new PersonMatchesSearchPredicate(
                List.of("O'Brien", "Nur-Aisyah", "Jr.", "Ravi2", "(NHHS)"), List.of(), List.of()));
        assertParseSuccess(parser, "O'Brien Nur-Aisyah Jr. Ravi2 (NHHS)", expectedFindCommand);
    }

    @Test
    public void parse_phoneAndTag_returnsFindCommand() {
        // phone only, with a space inside the number
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesSearchPredicate(List.of(), List.of(new Phone("91234567")), List.of()));
        assertParseSuccess(parser, " p/9123 4567", expectedFindCommand);

        // tag only
        expectedFindCommand = new FindCommand(
                new PersonMatchesSearchPredicate(List.of(), List.of(), List.of(new Tag("sec4"))));
        assertParseSuccess(parser, " t/sec4", expectedFindCommand);

        // name keywords, multiple phones and multiple tags
        expectedFindCommand = new FindCommand(new PersonMatchesSearchPredicate(List.of("Liam", "O'Brien"),
                List.of(new Phone("91234567"), new Phone("98765432")),
                List.of(new Tag("sec4"), new Tag("physics"))));
        assertParseSuccess(parser, "Liam O'Brien p/91234567 p/98765432 t/sec4 t/physics", expectedFindCommand);
    }

    @Test
    public void parse_invalidPhoneOrTag_throwsParseException() {
        // phone too short
        assertParseFailure(parser, " p/9123", Phone.MESSAGE_CONSTRAINTS);

        // empty phone
        assertParseFailure(parser, "Liam p/", Phone.MESSAGE_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, " t/sec_4", Tag.MESSAGE_CONSTRAINTS);

        // empty tag
        assertParseFailure(parser, "Liam t/", Tag.MESSAGE_CONSTRAINTS);

        // invalid name keyword alongside a valid tag
        assertParseFailure(parser, "B_b t/sec4", FindCommand.MESSAGE_INVALID_KEYWORDS);
    }

    @Test
    public void parse_invalidKeywords_throwsParseException() {
        // underscore
        assertParseFailure(parser, "Amirah_Tan", FindCommand.MESSAGE_INVALID_KEYWORDS);

        // slash (prefix delimiter, not allowed in names)
        assertParseFailure(parser, "a/l", FindCommand.MESSAGE_INVALID_KEYWORDS);

        // punctuation only
        assertParseFailure(parser, "-", FindCommand.MESSAGE_INVALID_KEYWORDS);
        assertParseFailure(parser, "'.", FindCommand.MESSAGE_INVALID_KEYWORDS);
        assertParseFailure(parser, "()", FindCommand.MESSAGE_INVALID_KEYWORDS);

        // one invalid keyword among valid ones
        assertParseFailure(parser, "Alice B_b", FindCommand.MESSAGE_INVALID_KEYWORDS);
    }

}

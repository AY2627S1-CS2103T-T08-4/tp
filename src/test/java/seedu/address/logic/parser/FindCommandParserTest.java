package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;

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
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_nameCharacterKeywords_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new NameContainsKeywordsPredicate(
                List.of("O'Brien", "Nur-Aisyah", "Jr.", "Ravi2", "(NHHS)")));
        assertParseSuccess(parser, "O'Brien Nur-Aisyah Jr. Ravi2 (NHHS)", expectedFindCommand);
    }

    @Test
    public void parse_curlyApostrophe_readAsStraightApostrophe() {
        FindCommand expectedFindCommand = new FindCommand(new NameContainsKeywordsPredicate(List.of("O'Brien")));
        assertParseSuccess(parser, "O\u2019Brien", expectedFindCommand);
    }

    @Test
    public void parse_phoneOrTagPrefix_throwsParseException() {
        // phone and tag are searched with filter, so their prefixes are not keywords
        assertParseFailure(parser, " p/9123 4567", FindCommand.MESSAGE_INVALID_KEYWORDS);
        assertParseFailure(parser, " t/sec4", FindCommand.MESSAGE_INVALID_KEYWORDS);
        assertParseFailure(parser, "Liam t/sec4", FindCommand.MESSAGE_INVALID_KEYWORDS);
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

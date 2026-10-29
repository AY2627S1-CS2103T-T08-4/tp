package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIXES_EXCEPT_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Stream;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final Logger logger = LogsCenter.getLogger(FindCommandParser.class);

    /** A keyword uses only name characters and contains at least one letter or digit. */
    private static final String KEYWORD_VALIDATION_REGEX =
            "[" + Name.NAME_CHARACTERS + "]*[A-Za-z0-9][" + Name.NAME_CHARACTERS + "]*";

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        // Keywords accept curly apostrophes as names do, so that find O’Brien works
        String trimmedArgs = Name.normalizeApostrophes(args.trim());
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        List<String> nameKeywords = List.of(trimmedArgs.split("\\s+"));
        for (String keyword : nameKeywords) {
            // Checked first, as the invalid keyword message would not say that the prefix is the problem
            if (startsWithPrefix(keyword)) {
                logger.fine("Rejected find keyword with a prefix: " + keyword);
                throw new ParseException(FindCommand.MESSAGE_PREFIX_IN_KEYWORDS);
            }
            if (!keyword.matches(KEYWORD_VALIDATION_REGEX)) {
                logger.fine("Rejected find keyword: " + keyword);
                throw new ParseException(FindCommand.MESSAGE_INVALID_KEYWORDS);
            }
        }

        return new FindCommand(new NameContainsKeywordsPredicate(nameKeywords));
    }

    /**
     * Returns true if {@code keyword} starts with a prefix of add or edit, in any case,
     * such as n/Amy or T/Math typed out of habit.
     */
    private static boolean startsWithPrefix(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return Stream.concat(Stream.of(PREFIXES_EXCEPT_NOTE), Stream.of(PREFIX_NOTE))
                .anyMatch(prefix -> lowerCaseKeyword.startsWith(prefix.getPrefix()));
    }

}

package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.person.PersonMatchesSearchPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /** A keyword uses only name characters and contains at least one letter or digit. */
    private static final String KEYWORD_VALIDATION_REGEX =
            "[" + Name.NAME_CHARACTERS + "]*[A-Za-z0-9][" + Name.NAME_CHARACTERS + "]*";

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_PHONE, PREFIX_TAG);
        String preamble = argMultimap.getPreamble();
        List<String> phoneValues = argMultimap.getAllValues(PREFIX_PHONE);
        List<String> tagValues = argMultimap.getAllValues(PREFIX_TAG);

        if (preamble.isEmpty() && phoneValues.isEmpty() && tagValues.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        List<String> nameKeywords = parseNameKeywords(preamble);

        List<Phone> phones = new ArrayList<>();
        for (String phoneValue : phoneValues) {
            phones.add(ParserUtil.parsePhone(phoneValue));
        }

        List<Tag> tags = new ArrayList<>();
        for (String tagValue : tagValues) {
            tags.add(ParserUtil.parseTag(tagValue));
        }

        return new FindCommand(new PersonMatchesSearchPredicate(nameKeywords, phones, tags));
    }

    /**
     * Splits {@code preamble} into name keywords, or returns an empty list if there are none.
     * @throws ParseException if any keyword contains characters not allowed in names
     */
    private static List<String> parseNameKeywords(String preamble) throws ParseException {
        if (preamble.isEmpty()) {
            return List.of();
        }

        List<String> nameKeywords = List.of(preamble.split("\\s+"));
        for (String keyword : nameKeywords) {
            if (!keyword.matches(KEYWORD_VALIDATION_REGEX)) {
                throw new ParseException(FindCommand.MESSAGE_INVALID_KEYWORDS);
            }
        }
        return nameKeywords;
    }

}

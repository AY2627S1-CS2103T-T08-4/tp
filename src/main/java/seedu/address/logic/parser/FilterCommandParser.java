package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesSearchPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new {@code FilterCommand} object.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    /**
     * Parses the given {@code args} in the context of the {@code FilterCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_PHONE, PREFIX_TAG);
        List<String> phoneValues = argMultimap.getAllValues(PREFIX_PHONE);
        List<String> tagValues = argMultimap.getAllValues(PREFIX_TAG);

        if ((phoneValues.isEmpty() && tagValues.isEmpty()) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        List<Phone> phones = new ArrayList<>();
        for (String phoneValue : phoneValues) {
            phones.add(ParserUtil.parsePhone(phoneValue));
        }

        List<Tag> tags = new ArrayList<>();
        for (String tagValue : tagValues) {
            tags.add(ParserUtil.parseTag(tagValue));
        }
        return new FilterCommand(new PersonMatchesSearchPredicate(phones, tags));
    }
}

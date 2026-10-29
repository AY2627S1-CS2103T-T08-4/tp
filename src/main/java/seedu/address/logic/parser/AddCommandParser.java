package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIXES_EXCEPT_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DUE_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.DueDate;
import seedu.address.model.person.Email;
import seedu.address.model.person.Fee;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    public static final String MESSAGE_MISSING_FIELDS = "Invalid command format! Missing: %1$s\n%2$s";

    // The compulsory prefixes in the order the usage lists them, each with the placeholder the usage shows for it
    private static final List<Map.Entry<Prefix, String>> COMPULSORY_FIELDS = List.of(
        Map.entry(PREFIX_NAME, "NAME"), Map.entry(PREFIX_PHONE, "PHONE"),
        Map.entry(PREFIX_EMAIL, "EMAIL"), Map.entry(PREFIX_ADDRESS, "ADDRESS"));

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
            ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_NOTE,
                PREFIX_FEE, PREFIX_DUE_DATE, PREFIX_TAG);

        // Checked first, so that a mistyped prefix such as P/ is named, instead of reported as a missing p/
        argMultimap.verifyNoUnknownPrefixesInPreamble();
        // Notes are free text, where words such as "w/" (with) are normal
        argMultimap.verifyNoUnknownPrefixesIn(PREFIXES_EXCEPT_NOTE);
        // Checked before duplicates, so that nt/call before e/visit is not reported as a repeated e/
        ParserUtil.verifyNoteIsLast(args);
        String missingFields = getMissingFields(argMultimap);
        if (!missingFields.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_MISSING_FIELDS, missingFields, AddCommand.MESSAGE_USAGE));
        }
        if (!argMultimap
            .getPreamble()
            .isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_NOTE,
            PREFIX_FEE, PREFIX_DUE_DATE);
        Name name = ParserUtil.parseName(argMultimap
            .getValue(PREFIX_NAME)
            .get());
        Phone phone = ParserUtil.parsePhone(argMultimap
            .getValue(PREFIX_PHONE)
            .get());
        Email email = ParserUtil.parseEmail(argMultimap
            .getValue(PREFIX_EMAIL)
            .get());
        Address address = ParserUtil.parseAddress(argMultimap
            .getValue(PREFIX_ADDRESS)
            .get());
        Note note = ParserUtil.parseNote(argMultimap
            .getValue(PREFIX_NOTE)
            .orElse(""));
        Fee fee = ParserUtil.parseFee(argMultimap
            .getValue(PREFIX_FEE)
            .orElse(""));
        DueDate dueDate = ParserUtil.parseDueDate(argMultimap
            .getValue(PREFIX_DUE_DATE)
            .orElse(""));
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Person person = new Person(name, phone, email, address, note, fee, dueDate, tagList);

        return new AddCommand(person);
    }

    /**
     * Returns the compulsory fields missing from {@code argumentMultimap} as the usage shows them,
     * e.g. "e/EMAIL a/ADDRESS", or an empty string if none is missing.
     */
    private static String getMissingFields(ArgumentMultimap argumentMultimap) {
        return COMPULSORY_FIELDS
            .stream()
            .filter(field -> argumentMultimap
                .getValue(field.getKey())
                .isEmpty())
            .map(field -> field.getKey() + field.getValue())
            .collect(Collectors.joining(" "));
    }

}

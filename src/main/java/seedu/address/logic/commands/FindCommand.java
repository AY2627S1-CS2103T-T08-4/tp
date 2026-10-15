package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons in the address book who match the given name keywords, phone numbers and tags.
 * Name keywords match the start of a name part, and are not case-sensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all clients with a name part that starts with "
            + "any of the specified keywords (case-insensitive, ignoring punctuation), "
            + "and who have any of the specified phone numbers and tags, "
            + "and displays them as a list with index numbers.\n"
            + "Parameters: [KEYWORD]... [" + PREFIX_PHONE + "PHONE]... [" + PREFIX_TAG + "TAG]... "
            + "(at least one must be given)\n"
            + "Example: " + COMMAND_WORD + " immanuel krishna " + PREFIX_TAG + "sec4";

    public static final String MESSAGE_INVALID_KEYWORDS =
            "Search keywords can only contain letters, digits, hyphens <->, apostrophes <'>, full stops <.> "
                    + "and parentheses <()>, and must include at least one letter or digit.";

    public static final String MESSAGE_NO_CLIENTS_FOUND = Messages.getPersonsListedOverviewMessage(0)
            + " Keywords must match the start of a name part (e.g. Bri, not rien), phone numbers must be "
            + "complete, and a client must match every field you search by (name, phone, tag). "
            + "Check the spelling, or use list to see all clients.";

    private final Predicate<Person> predicate;

    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int matchCount = model.getFilteredPersonList().size();
        if (matchCount == 0) {
            return new CommandResult(MESSAGE_NO_CLIENTS_FOUND);
        }
        return new CommandResult(Messages.getPersonsListedOverviewMessage(matchCount));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}

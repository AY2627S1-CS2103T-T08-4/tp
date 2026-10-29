package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonMatchesSearchPredicate;

/**
 * Finds and lists all persons who match the specified phone and tag filters.
 * Phone numbers are matched exactly, while tag matching is case-insensitive.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows all clients who match the specified phone and tag filters "
            + "and displays them as a list with index numbers.\n"
            + "Parameters: [" + PREFIX_PHONE + "PHONE]... [" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_PHONE + "91234567 " + PREFIX_TAG + "Physics";

    public static final String MESSAGE_NO_CLIENTS_FOUND = Messages.getPersonsListedOverviewMessage(0)
            + " A client must have every tag given, and phone numbers must be complete. "
            + "Check the spelling, or use list to see all clients.";

    private final PersonMatchesSearchPredicate predicate;

    /**
     * Creates a FilterCommand that shows the clients matching {@code predicate}.
     */
    public FilterCommand(PersonMatchesSearchPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int matchCount = model.getFilteredPersonList().size();
        if (matchCount == 0) {
            // With no clients at all, the tips to check the spelling or use list cannot help
            boolean isAddressBookEmpty = model.getAddressBook().getPersonList().isEmpty();
            return CommandResult.forEmptySearch(isAddressBookEmpty
                    ? Messages.MESSAGE_NO_CLIENTS_TO_SEARCH : MESSAGE_NO_CLIENTS_FOUND);
        }
        return new CommandResult(Messages.getPersonsListedOverviewMessage(matchCount));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof FilterCommand otherFilterCommand)) {
            return false;
        }

        return predicate.equals(otherFilterCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}

package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonMatchesSearchPredicate;

/**
 * Finds and lists all persons whose tags match any specified tag.
 * Tag matching is case-insensitive.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all clients who have any of the specified tags "
            + "(case-insensitive and order-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: t/TAG [t/MORE_TAGS]...\n"
            + "Example: " + COMMAND_WORD + " t/Physics t/Sec4";

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
        return new CommandResult(
                Messages.getPersonsListedOverviewMessage(model.getFilteredPersonList().size()));
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

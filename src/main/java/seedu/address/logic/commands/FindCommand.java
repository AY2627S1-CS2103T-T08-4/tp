package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons in the address book whose name matches any of the given keywords.
 * Name keywords match the start of a name part, and are not case-sensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all clients with a name part that starts with "
            + "any of the specified keywords (case-insensitive, ignoring punctuation) "
            + "and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " immanuel krishna";

    public static final String MESSAGE_INVALID_KEYWORDS =
            "Search keywords can only contain letters, digits, hyphens <->, apostrophes <'>, full stops <.> "
                    + "and parentheses <()>, and must include at least one letter or digit.";

    public static final String MESSAGE_NO_CLIENTS_FOUND = Messages.getPersonsListedOverviewMessage(0)
            + " Keywords must match the start of a name part (e.g. Bri, not rien). "
            + "Check the spelling, or use list to see all clients.";

    private static final Logger logger = LogsCenter.getLogger(FindCommand.class);

    private final Predicate<Person> predicate;

    /**
     * Creates a {@code FindCommand} that shows the persons matching {@code predicate}.
     */
    public FindCommand(Predicate<Person> predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int matchCount = model.getFilteredPersonList().size();
        logger.fine("Find matched " + matchCount + " client(s) for " + predicate);
        if (matchCount == 0) {
            return CommandResult.forEmptySearch(MESSAGE_NO_CLIENTS_FOUND);
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

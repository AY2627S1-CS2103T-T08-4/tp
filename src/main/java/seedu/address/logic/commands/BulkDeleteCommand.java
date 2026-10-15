package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes multiple people identified by their displayed indices based on the input.
 */
public class BulkDeleteCommand extends Command {

    public static final String MESSAGE_USAGE =
            DeleteCommand.COMMAND_WORD
                    + ": Deletes multiple clients identified by their index numbers.\n"
                    + "Parameters: INDEX, INDEX [, INDEX]...\n"
                    + "Example: " + DeleteCommand.COMMAND_WORD + " 1, 2, 3";

    public static final String MESSAGE_DELETE_PERSONS_SUCCESS =
            "Deleted clients:\n%1$s";

    public static final String MESSAGE_DUPLICATE_INDEX =
            "Duplicate indexes are not allowed.";

    private final List<Index> targetIndexes;

    /**
     * Creates a BulkDeleteCommand that deletes clients at {@code targetIndexes}.
     */
    public BulkDeleteCommand(List<Index> targetIndexes) {
        requireNonNull(targetIndexes);

        if (targetIndexes.size() < 2) {
            throw new IllegalArgumentException(
                    "BulkDeleteCommand requires at least two indexes.");
        }

        this.targetIndexes = List.copyOf(targetIndexes);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // Take a snapshot because the filtered list changes as people are deleted.
        List<Person> lastShownList =
                List.copyOf(model.getFilteredPersonList());

        if (lastShownList.isEmpty()) {
            throw new CommandException(
                    DeleteCommand.MESSAGE_EMPTY_PERSON_LIST);
        }

        // Validate every index before changing the model.
        for (Index index : targetIndexes) {
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(
                        Messages.getIndexOutOfBoundsMessage(
                                lastShownList.size()));
            }
        }

        // Resolve every person against the original displayed list.
        List<Person> personsToDelete = targetIndexes.stream()
                .map(index -> lastShownList.get(index.getZeroBased()))
                .toList();

        personsToDelete.forEach(model::deletePerson);

        String deletedPersons = personsToDelete.stream()
                .map(Messages::format)
                .collect(joining("\n"));

        return new CommandResult(String.format(
                MESSAGE_DELETE_PERSONS_SUCCESS, deletedPersons));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof BulkDeleteCommand otherCommand)) {
            return false;
        }

        return targetIndexes.equals(otherCommand.targetIndexes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndexes", targetIndexes)
                .toString();
    }
}

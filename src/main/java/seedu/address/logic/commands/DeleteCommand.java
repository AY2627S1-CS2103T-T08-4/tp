package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Arrays;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using its displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD
            + ": Deletes the client identified by the index number used in the displayed client list.\n"
            + "Parameters: INDEX [, INDEX]...\n"
            + "Example: " + COMMAND_WORD + " 1, 2, 3";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted client: %1$s";
    public static final String MESSAGE_DELETE_PERSONS_SUCCESS = "Deleted clients:\n%1$s";

    public static final String MESSAGE_EMPTY_PERSON_LIST =
            "Error: Cannot delete from an empty client list.";

    public static final String MESSAGE_INDEX_NOT_PRESENT =
            "Error: Index not provided!\n"
                    + "Expected format: delete INDEX [, INDEX]...";

    private final List<Index> targetIndexes;

    /**
     * Creates a {@code DeleteCommand} that deletes the clients at
     * {@code targetIndexes}.
     */
    public DeleteCommand(Index... targetIndexes) {
        requireNonNull(targetIndexes);
        requireAllNonNull(List.of(targetIndexes));

        checkArgument(
                targetIndexes.length > 0,
                "At least one target index must be provided.");

        checkArgument(
                Arrays.stream(targetIndexes)
                        .mapToInt(Index::getZeroBased)
                        .distinct()
                        .count()
                        == targetIndexes.length,
                "Target indexes must not contain duplicates.");

        this.targetIndexes = List.copyOf(List.of(targetIndexes));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // The displayed list can change when clients are deleted, so all
        // indices must be resolved against an unchanged snapshot.
        List<Person> lastShownList =
                List.copyOf(model.getFilteredPersonList());

        if (lastShownList.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_PERSON_LIST);
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

        if (personsToDelete.size() == 1) {
            return new CommandResult(String.format(
                    MESSAGE_DELETE_PERSON_SUCCESS,
                    Messages.format(personsToDelete.get(0))));
        }

        String deletedPersons = personsToDelete.stream()
                .map(Messages::format)
                .collect(joining("\n"));

        return new CommandResult(String.format(
                MESSAGE_DELETE_PERSONS_SUCCESS,
                deletedPersons));
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetIndexes.equals(otherDeleteCommand.targetIndexes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndexes", targetIndexes)
                .toString();
    }
}

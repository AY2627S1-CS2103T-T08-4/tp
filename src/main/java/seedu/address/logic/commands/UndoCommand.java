package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Reverts the client list to its state before the last change.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_SUCCESS = "Undid the last change. Showing all clients.";
    public static final String MESSAGE_NOTHING_TO_UNDO = "Error: There is nothing to undo.";
    public static final String MESSAGE_NO_PARAMETERS =
            "undo takes no parameters. Run undo again to undo more changes.";

    /**
     * Reverts the last change to the client list and shows the full list,
     * so that a restored client is not hidden by an earlier {@code find} or {@code filter}.
     *
     * @param model The current state of the application model data. Must not be null.
     * @return The result message indicating the last change was undone.
     * @throws CommandException If there is no earlier state to undo to.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.canUndoAddressBook()) {
            throw new CommandException(MESSAGE_NOTHING_TO_UNDO);
        }

        model.undoAddressBook();
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}

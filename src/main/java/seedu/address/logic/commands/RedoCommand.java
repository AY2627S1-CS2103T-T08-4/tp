package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Reapplies the last change that was undone.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";
    public static final String MESSAGE_SUCCESS =
            "Redid the last undone change.\n" + Messages.MESSAGE_SHOWING_ALL_CLIENTS;
    public static final String MESSAGE_NOTHING_TO_REDO = "Error: There is nothing to redo.";
    public static final String MESSAGE_NO_PARAMETERS =
            "redo takes no parameters. Run redo again to redo more changes.";

    /**
     * Reapplies the last undone change to the client list and shows the full list,
     * so that a changed client is not hidden by an earlier {@code find} or {@code filter}.
     *
     * @param model The current state of the application model data. Must not be null.
     * @return The result message indicating the undone change was reapplied.
     * @throws CommandException If there is no undone change to redo.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.canRedoAddressBook()) {
            throw new CommandException(MESSAGE_NOTHING_TO_REDO);
        }

        model.redoAddressBook();
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}

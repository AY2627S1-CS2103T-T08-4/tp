package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears all client records from Sieve.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "All client data has been cleared.";
    public static final String MESSAGE_NO_CLIENTS = "There are no clients to clear.";
    public static final String MESSAGE_NO_PARAMETERS =
            "clear takes no parameters, as it deletes every client. To delete some clients, use delete INDEX.";

    /**
     * Executes the clear command by resetting the internal data to an empty state.
     *
     * @param model The current state of the application model data. Must not be null.
     * @return The result message indicating successful clearance of all data, or that there was none to clear.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (model.getAddressBook().getPersonList().isEmpty()) {
            return new CommandResult(MESSAGE_NO_CLIENTS);
        }
        model.setAddressBook(new AddressBook());
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}

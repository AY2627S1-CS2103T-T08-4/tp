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

    /**
     * Executes the clear command by resetting the internal data to an empty state.
     *
     * @param model The current state of the application model data. Must not be null.
     * @return The result message indicating successful clearance of all data.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setAddressBook(new AddressBook());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}

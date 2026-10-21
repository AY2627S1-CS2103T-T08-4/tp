package seedu.address.logic.commands;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model) throws CommandException;

    /**
     * Returns true if this command changes client data, so it is blocked when the data file is corrupted
     * (read-only mode). Commands that change data must override this to return true.
     */
    public boolean isMutating() {
        return false;
    }

    /**
     * Returns the error message for {@code person} having the same name as the {@code existing} client.
     * If their phone numbers also match, the client was most likely added before.
     * Otherwise, they may be a different client who happens to have the same name.
     */
    protected static String getDuplicateMessage(Person existing, Person person) {
        return existing.hasSamePhone(person)
                ? Messages.getClientAlreadyExistsMessage(existing)
                : Messages.getNameAlreadyUsedMessage(existing);
    }

}

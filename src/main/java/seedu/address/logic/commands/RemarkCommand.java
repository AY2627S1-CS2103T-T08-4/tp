package seedu.address.logic.commands;

import seedu.address.model.Model;

public class RemarkCommand extends Command {
    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a remark.";
    public static final String MESSAGE_SUCCESS = "Added remark successfully.";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(MESSAGE_SUCCESS);
    }
}

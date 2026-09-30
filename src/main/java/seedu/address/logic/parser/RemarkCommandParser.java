package seedu.address.logic.parser;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class RemarkCommandParser implements Parser<RemarkCommand> {
    public RemarkCommand parse(String args) throws ParseException {
        // Minimum implementation to bypass parsing
        return new RemarkCommand();
    }
}

package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class BulkDeleteCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_oneIndexOutOfBounds_modelUnchanged() {
        int listSize = model.getFilteredPersonList().size();

        BulkDeleteCommand command = new BulkDeleteCommand(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(listSize + 1)));

        Model expectedModel =
                new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandFailure(
                command,
                model,
                Messages.getIndexOutOfBoundsMessage(listSize));

        assertEquals(expectedModel, model);
    }
}

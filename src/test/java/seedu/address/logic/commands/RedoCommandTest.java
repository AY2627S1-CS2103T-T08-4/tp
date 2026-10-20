package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class RedoCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_nothingToRedo_throwsCommandException() {
        assertCommandFailure(new RedoCommand(), model, RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }

    @Test
    public void execute_afterUndo_reappliesChange() {
        model.deletePerson(ALICE);
        model.commitAddressBook();
        model.undoAddressBook();
        expectedModel.deletePerson(ALICE);

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_afterUndoClear_clearsAgain() {
        model.setAddressBook(new AddressBook());
        model.commitAddressBook();
        model.undoAddressBook();

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS,
                new ModelManager(new AddressBook(), new UserPrefs()));
    }

    @Test
    public void execute_filteredList_showsAllClients() {
        model.deletePerson(ALICE);
        model.commitAddressBook();
        model.undoAddressBook();
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        expectedModel.deletePerson(ALICE);

        // expectedModel shows the full list, so this also checks that the find filter is cleared
        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_allChangesRedone_throwsCommandException() {
        model.deletePerson(ALICE);
        model.commitAddressBook();
        model.undoAddressBook();
        expectedModel.deletePerson(ALICE);

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
        assertCommandFailure(new RedoCommand(), model, RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }
}

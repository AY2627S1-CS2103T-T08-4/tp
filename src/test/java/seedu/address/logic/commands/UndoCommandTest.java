package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class UndoCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_nothingToUndo_throwsCommandException() {
        assertCommandFailure(new UndoCommand(), model, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UndoCommand().execute(null));
    }

    @Test
    public void execute_afterChange_restoresPreviousState() {
        deleteAliceAndCommit();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_afterClear_restoresAllClients() {
        model.setAddressBook(new AddressBook());
        model.commitAddressBook();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_filteredList_showsAllClients() {
        deleteAliceAndCommit();
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        // expectedModel shows the full list, so this also checks that the find filter is cleared
        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_allChangesUndone_throwsCommandException() {
        deleteAliceAndCommit();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
        assertCommandFailure(new UndoCommand(), model, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void execute_afterNoChangeEdit_undoesOnlyTheEarlierChange() throws Exception {
        model.deletePerson(ALICE);
        model.commitAddressBook();

        // The edit tells the user that nothing changed, and leaves no entry of its own to undo
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor sameValues = new EditPersonDescriptorBuilder(firstPerson).build();
        CommandResult editResult = new EditCommand(INDEX_FIRST_PERSON, sameValues).execute(model);
        model.commitAddressBook();
        assertEquals(String.format(EditCommand.MESSAGE_NO_CHANGE, firstPerson.getName()),
                editResult.getFeedbackToUser());

        // So undo reverts the deletion, the last change the user was told about, and nothing before it
        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
        assertCommandFailure(new UndoCommand(), model, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void isMutating_undoCommand_returnsTrue() {
        assertTrue(new UndoCommand().isMutating());
    }

    /**
     * Deletes Alice from {@code model} and commits the change, so that it can be undone.
     */
    private void deleteAliceAndCommit() {
        model.deletePerson(ALICE);
        model.commitAddressBook();
    }
}

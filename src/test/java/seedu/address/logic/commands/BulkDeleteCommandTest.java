package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration and unit tests for {@code BulkDeleteCommand}.
 */
public class BulkDeleteCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullIndexList_throwsNullPointerException() {
        assertThrows(
                NullPointerException.class, () -> new BulkDeleteCommand(null));
    }

    @Test
    public void constructor_fewerThanTwoIndexes_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> new BulkDeleteCommand(List.of(INDEX_FIRST_PERSON)));
    }

    @Test
    public void execute_validIndexesUnfilteredList_success() {
        List<Person> lastShownList = model.getFilteredPersonList();
        Person firstPerson = lastShownList.get(
                INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = lastShownList.get(
                INDEX_SECOND_PERSON.getZeroBased());

        BulkDeleteCommand command = new BulkDeleteCommand(List.of(
                INDEX_FIRST_PERSON,
                INDEX_SECOND_PERSON));

        String deletedPersons = String.join(
                "\n",
                Messages.format(firstPerson),
                Messages.format(secondPerson));

        String expectedMessage = String.format(
                BulkDeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS,
                deletedPersons);

        Model expectedModel =
                new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(firstPerson);
        expectedModel.deletePerson(secondPerson);

        assertCommandSuccess(
                command,
                model,
                expectedMessage,
                expectedModel);
    }

    @Test
    public void execute_nonAscendingIndexes_success() {
        List<Person> lastShownList = model.getFilteredPersonList();
        Person firstPerson = lastShownList.get(
                INDEX_FIRST_PERSON.getZeroBased());
        Person thirdPerson = lastShownList.get(
                INDEX_THIRD_PERSON.getZeroBased());

        BulkDeleteCommand command = new BulkDeleteCommand(List.of(
                INDEX_THIRD_PERSON,
                INDEX_FIRST_PERSON));

        String deletedPersons = String.join(
                "\n",
                Messages.format(thirdPerson),
                Messages.format(firstPerson));

        String expectedMessage = String.format(
                BulkDeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS,
                deletedPersons);

        Model expectedModel =
                new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(thirdPerson);
        expectedModel.deletePerson(firstPerson);

        assertCommandSuccess(
                command,
                model,
                expectedMessage,
                expectedModel);
    }

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

    @Test
    public void execute_emptyAddressBook_throwsCommandException() {
        Model emptyModel = new ModelManager();

        BulkDeleteCommand command = new BulkDeleteCommand(List.of(
                INDEX_FIRST_PERSON,
                INDEX_SECOND_PERSON));

        assertCommandFailure(
                command,
                emptyModel,
                DeleteCommand.MESSAGE_EMPTY_PERSON_LIST);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        BulkDeleteCommand command = new BulkDeleteCommand(List.of(
                INDEX_FIRST_PERSON,
                INDEX_SECOND_PERSON));

        assertThrows(
                NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals() {
        BulkDeleteCommand firstCommand = new BulkDeleteCommand(List.of(
                INDEX_FIRST_PERSON,
                INDEX_SECOND_PERSON));

        BulkDeleteCommand firstCommandCopy =
                new BulkDeleteCommand(List.of(
                        INDEX_FIRST_PERSON,
                        INDEX_SECOND_PERSON));

        BulkDeleteCommand differentIndexesCommand =
                new BulkDeleteCommand(List.of(
                        INDEX_FIRST_PERSON,
                        INDEX_THIRD_PERSON));

        BulkDeleteCommand differentOrderCommand =
                new BulkDeleteCommand(List.of(
                        INDEX_SECOND_PERSON,
                        INDEX_FIRST_PERSON));

        // Same object.
        assertTrue(firstCommand.equals(firstCommand));

        // Same indices in the same order.
        assertTrue(firstCommand.equals(firstCommandCopy));

        // Different indices.
        assertFalse(firstCommand.equals(differentIndexesCommand));

        // Same indices in a different order.
        assertFalse(firstCommand.equals(differentOrderCommand));

        // Different type.
        assertFalse(firstCommand.equals(1));

        // Null.
        assertFalse(firstCommand.equals(null));
    }

    @Test
    public void toStringMethod() {
        List<Index> targetIndexes = List.of(
                INDEX_FIRST_PERSON,
                INDEX_SECOND_PERSON);

        BulkDeleteCommand command =
                new BulkDeleteCommand(targetIndexes);

        String expected = BulkDeleteCommand.class.getCanonicalName()
                + "{targetIndexes=" + targetIndexes + "}";

        assertEquals(expected, command.toString());
    }
}

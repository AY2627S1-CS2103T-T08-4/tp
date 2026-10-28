package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class VersionedAddressBookTest {

    private final VersionedAddressBook versionedAddressBook = new VersionedAddressBook(getTypicalAddressBook());
    private final Person aliceWithUpperCaseTag = new PersonBuilder(ALICE).withTags("FRIENDS").build();

    @Test
    public void constructor_typicalAddressBook_nothingToUndoOrRedo() {
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
        assertFalse(versionedAddressBook.canRedoAddressBook());
    }

    @Test
    public void commitAddressBook_afterChange_canUndo() {
        deleteAndCommit(ALICE);
        assertTrue(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void commitAddressBook_noChange_nothingToUndo() {
        versionedAddressBook.commitAddressBook();
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void commitAddressBook_noChangeAfterChange_undoesOnlyTheChange() {
        deleteAndCommit(ALICE);
        versionedAddressBook.commitAddressBook();

        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void commitAddressBook_changeThenRevert_bothUndoable() {
        AddressBook withoutAlice = deleteAndCommit(ALICE);
        addAndCommit(ALICE);

        // Re-adding Alice is a real change even though Alice was in an earlier state
        versionedAddressBook.undoAddressBook();
        assertEquals(withoutAlice, versionedAddressBook);

        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void commitAddressBook_tagCaseOnlyChange_canUndo() {
        versionedAddressBook.setPerson(ALICE, aliceWithUpperCaseTag);
        versionedAddressBook.commitAddressBook();
        assertTrue(versionedAddressBook.canUndoAddressBook());

        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
    }

    @Test
    public void undoAddressBook_tagCaseOnlyChangeAfterDelete_undoesOneAtATime() {
        AddressBook withoutBenson = deleteAndCommit(BENSON);
        versionedAddressBook.setPerson(ALICE, aliceWithUpperCaseTag);
        versionedAddressBook.commitAddressBook();
        AddressBook withUpperCaseTag = new AddressBook(versionedAddressBook);

        // The first undo reverts only the tag case change, so Benson stays deleted
        versionedAddressBook.undoAddressBook();
        assertEquals(withoutBenson, versionedAddressBook);
        assertTrue(versionedAddressBook.canUndoAddressBook());

        // The tag case change is not lost, and can be redone
        versionedAddressBook.redoAddressBook();
        assertEquals(withUpperCaseTag, versionedAddressBook);
    }

    @Test
    public void undoAddressBook_afterChange_restoresPreviousState() {
        deleteAndCommit(ALICE);

        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void undoAddressBook_afterClear_restoresPreviousState() {
        versionedAddressBook.resetData(new AddressBook());
        versionedAddressBook.commitAddressBook();

        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
    }

    @Test
    public void undoAddressBook_twoChanges_undoesOneAtATime() {
        AddressBook withoutAlice = deleteAndCommit(ALICE);
        addAndCommit(BOB);

        versionedAddressBook.undoAddressBook();
        assertEquals(withoutAlice, versionedAddressBook);
        assertTrue(versionedAddressBook.canUndoAddressBook());

        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void undoAddressBook_changeAfterUndo_doesNotBringBackUndoneChange() {
        deleteAndCommit(ALICE);
        versionedAddressBook.undoAddressBook();
        addAndCommit(BOB);

        // Undoing the add should go back to the state after the first undo, with Alice still present
        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void undoAddressBook_afterChange_canRedo() {
        deleteAndCommit(ALICE);
        assertFalse(versionedAddressBook.canRedoAddressBook());

        versionedAddressBook.undoAddressBook();
        assertTrue(versionedAddressBook.canRedoAddressBook());
    }

    @Test
    public void redoAddressBook_afterUndo_restoresUndoneState() {
        AddressBook withoutAlice = deleteAndCommit(ALICE);
        versionedAddressBook.undoAddressBook();

        versionedAddressBook.redoAddressBook();
        assertEquals(withoutAlice, versionedAddressBook);
        assertFalse(versionedAddressBook.canRedoAddressBook());

        // The redone change can be undone again
        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
    }

    @Test
    public void redoAddressBook_twoUndos_redoesInOrder() {
        AddressBook withoutAlice = deleteAndCommit(ALICE);
        AddressBook withBob = addAndCommit(BOB);
        versionedAddressBook.undoAddressBook();
        versionedAddressBook.undoAddressBook();

        versionedAddressBook.redoAddressBook();
        assertEquals(withoutAlice, versionedAddressBook);
        assertTrue(versionedAddressBook.canRedoAddressBook());

        versionedAddressBook.redoAddressBook();
        assertEquals(withBob, versionedAddressBook);
        assertFalse(versionedAddressBook.canRedoAddressBook());
    }

    @Test
    public void commitAddressBook_noChangeAfterUndo_canStillRedo() {
        deleteAndCommit(ALICE);
        versionedAddressBook.undoAddressBook();

        // LogicManager commits after undo too, which must not clear the redo history
        versionedAddressBook.commitAddressBook();
        assertTrue(versionedAddressBook.canRedoAddressBook());
    }

    @Test
    public void commitAddressBook_changeAfterUndo_nothingToRedo() {
        deleteAndCommit(ALICE);
        versionedAddressBook.undoAddressBook();
        addAndCommit(BOB);

        assertFalse(versionedAddressBook.canRedoAddressBook());
    }

    /**
     * Deletes {@code person}, commits the change, and returns a copy of the state after it.
     */
    private AddressBook deleteAndCommit(Person person) {
        versionedAddressBook.removePerson(person);
        versionedAddressBook.commitAddressBook();
        return new AddressBook(versionedAddressBook);
    }

    /**
     * Adds {@code person}, commits the change, and returns a copy of the state after it.
     */
    private AddressBook addAndCommit(Person person) {
        versionedAddressBook.addPerson(person);
        versionedAddressBook.commitAddressBook();
        return new AddressBook(versionedAddressBook);
    }
}

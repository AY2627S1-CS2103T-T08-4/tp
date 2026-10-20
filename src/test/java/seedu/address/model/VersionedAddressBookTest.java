package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

public class VersionedAddressBookTest {

    private final VersionedAddressBook versionedAddressBook = new VersionedAddressBook(getTypicalAddressBook());

    @Test
    public void constructor_nothingToUndoOrRedo() {
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
        assertFalse(versionedAddressBook.canRedoAddressBook());
    }

    @Test
    public void commitAddressBook_canUndo() {
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();
        assertTrue(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void commitAddressBook_noChange_nothingToUndo() {
        versionedAddressBook.commitAddressBook();
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void commitAddressBook_noChangeAfterChange_undoesOnlyTheChange() {
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();
        versionedAddressBook.commitAddressBook();

        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void commitAddressBook_changeThenRevert_bothUndoable() {
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();
        AddressBook withoutAlice = new AddressBook(versionedAddressBook);
        versionedAddressBook.addPerson(ALICE);
        versionedAddressBook.commitAddressBook();

        // Re-adding Alice is a real change even though Alice was in an earlier state
        versionedAddressBook.undoAddressBook();
        assertEquals(withoutAlice, versionedAddressBook);

        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void undoAddressBook_afterChange_restoresPreviousState() {
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();

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
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();
        AddressBook withoutAlice = new AddressBook(versionedAddressBook);
        versionedAddressBook.addPerson(BOB);
        versionedAddressBook.commitAddressBook();

        versionedAddressBook.undoAddressBook();
        assertEquals(withoutAlice, versionedAddressBook);
        assertTrue(versionedAddressBook.canUndoAddressBook());

        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void undoAddressBook_changeAfterUndo_doesNotBringBackUndoneChange() {
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();
        versionedAddressBook.undoAddressBook();
        versionedAddressBook.addPerson(BOB);
        versionedAddressBook.commitAddressBook();

        // Undoing the add should go back to the state after the first undo, with Alice still present
        versionedAddressBook.undoAddressBook();
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void undoAddressBook_afterChange_canRedo() {
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();
        assertFalse(versionedAddressBook.canRedoAddressBook());

        versionedAddressBook.undoAddressBook();
        assertTrue(versionedAddressBook.canRedoAddressBook());
    }

    @Test
    public void redoAddressBook_afterUndo_restoresUndoneState() {
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();
        AddressBook withoutAlice = new AddressBook(versionedAddressBook);
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
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();
        AddressBook withoutAlice = new AddressBook(versionedAddressBook);
        versionedAddressBook.addPerson(BOB);
        versionedAddressBook.commitAddressBook();
        AddressBook withBob = new AddressBook(versionedAddressBook);
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
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();
        versionedAddressBook.undoAddressBook();

        // LogicManager commits after undo too, which must not clear the redo history
        versionedAddressBook.commitAddressBook();
        assertTrue(versionedAddressBook.canRedoAddressBook());
    }

    @Test
    public void commitAddressBook_changeAfterUndo_nothingToRedo() {
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();
        versionedAddressBook.undoAddressBook();
        versionedAddressBook.addPerson(BOB);
        versionedAddressBook.commitAddressBook();

        assertFalse(versionedAddressBook.canRedoAddressBook());
    }
}

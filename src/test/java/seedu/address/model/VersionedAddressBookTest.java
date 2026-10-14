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
    public void constructor_nothingToUndo() {
        assertEquals(getTypicalAddressBook(), versionedAddressBook);
        assertFalse(versionedAddressBook.canUndoAddressBook());
    }

    @Test
    public void commitAddressBook_canUndo() {
        versionedAddressBook.removePerson(ALICE);
        versionedAddressBook.commitAddressBook();
        assertTrue(versionedAddressBook.canUndoAddressBook());
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
}

package seedu.address.model;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An {@code AddressBook} that remembers a history of past states, so that changes can be undone.
 * Changes can be undone one at a time, back to the state at startup.
 */
public class VersionedAddressBook extends AddressBook {
    private final Deque<ReadOnlyAddressBook> history = new ArrayDeque<>();
    private ReadOnlyAddressBook current;

    /**
     * Creates a {@code VersionedAddressBook} with a copy of the clients in {@code addressBook}
     * and nothing to undo.
     */
    public VersionedAddressBook(ReadOnlyAddressBook addressBook) {
        super(addressBook);
        current = new AddressBook(this);
    }

    /**
     * Saves a copy of the current state to history.
     * Call this after each change that can be undone.
     * Does nothing if the state has not changed since the last commit.
     */
    public void commitAddressBook() {
        if (current.equals(this)) {
            return;
        }
        history.push(current);
        current = new AddressBook(this);
    }

    /**
     * Returns true if there is a saved state to undo to.
     */
    public boolean canUndoAddressBook() {
        return !history.isEmpty();
    }

    /**
     * Restores the state saved by the last {@link #commitAddressBook()}.
     * Check {@link #canUndoAddressBook()} before calling this.
     */
    public void undoAddressBook() {
        assert canUndoAddressBook() : "No state to undo to";
        this.current = history.pop();
        this.resetData(this.current);
    }
}

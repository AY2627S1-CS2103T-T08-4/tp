package seedu.address.model;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An {@code AddressBook} that remembers a history of past states, so that changes can be undone and redone.
 * Changes can be undone one at a time, back to the state at startup.
 */
public class VersionedAddressBook extends AddressBook {
    private final Deque<ReadOnlyAddressBook> undoHistory = new ArrayDeque<>();
    private final Deque<ReadOnlyAddressBook> redoHistory = new ArrayDeque<>();
    private ReadOnlyAddressBook current;

    /**
     * Creates a {@code VersionedAddressBook} with a copy of the clients in {@code addressBook}
     * and nothing to undo or redo.
     */
    public VersionedAddressBook(ReadOnlyAddressBook addressBook) {
        super(addressBook);
        current = new AddressBook(this);
    }

    /**
     * Saves a copy of the current state to history.
     * Call this after each change that can be undone.
     * Does nothing if the state has not changed since the last commit.
     * Otherwise, the undone states can no longer be redone.
     */
    public void commitAddressBook() {
        if (current.equals(this)) {
            return;
        }
        undoHistory.push(current);
        redoHistory.clear();
        current = new AddressBook(this);
    }

    /**
     * Returns true if there is a saved state to undo to.
     */
    public boolean canUndoAddressBook() {
        return !undoHistory.isEmpty();
    }

    /**
     * Returns true if there is an undone state to redo.
     */
    public boolean canRedoAddressBook() {
        return !redoHistory.isEmpty();
    }

    /**
     * Restores the state saved by the last {@link #commitAddressBook()}.
     * Check {@link #canUndoAddressBook()} before calling this.
     */
    public void undoAddressBook() {
        assert canUndoAddressBook() : "No state to undo to";
        redoHistory.push(current);
        this.current = undoHistory.pop();
        this.resetData(this.current);
    }

    /**
     * Restores the state reverted by the last {@link #undoAddressBook()}.
     * Check {@link #canRedoAddressBook()} before calling this.
     */
    public void redoAddressBook() {
        assert canRedoAddressBook() : "No state to redo to";
        undoHistory.push(current);
        this.current = redoHistory.pop();
        this.resetData(this.current);
    }
}

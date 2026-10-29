package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {

    private final CommandHistory history = new CommandHistory();

    @Test
    public void add_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> history.add(null));
    }

    @Test
    public void getPreviousCommand_null_throwsNullPointerException() {
        history.add("list");
        assertThrows(NullPointerException.class, () -> history.getPreviousCommand(null));
    }

    @Test
    public void getPreviousCommand_emptyHistory_returnsEmpty() {
        assertEquals(Optional.empty(), history.getPreviousCommand(""));
    }

    @Test
    public void getPreviousCommand_twoCommands_returnsNewestFirst() {
        history.add("list");
        history.add("delete 1");

        assertEquals(Optional.of("delete 1"), history.getPreviousCommand(""));
        assertEquals(Optional.of("list"), history.getPreviousCommand(""));
    }

    @Test
    public void getPreviousCommand_atOldestCommand_returnsEmpty() {
        history.add("list");
        history.getPreviousCommand("");

        assertEquals(Optional.empty(), history.getPreviousCommand(""));
    }

    @Test
    public void getNextCommand_emptyHistory_returnsEmpty() {
        assertEquals(Optional.empty(), history.getNextCommand());
    }

    @Test
    public void getNextCommand_nothingRecalled_returnsEmpty() {
        history.add("list");

        // the user may be typing a new command, which must be kept
        assertEquals(Optional.empty(), history.getNextCommand());
    }

    @Test
    public void getNextCommand_afterGoingBack_returnsNewerThenEmptyString() {
        history.add("list");
        history.add("delete 1");
        history.getPreviousCommand("");
        history.getPreviousCommand("");

        assertEquals(Optional.of("delete 1"), history.getNextCommand());
        assertEquals(Optional.of(""), history.getNextCommand());
        assertEquals(Optional.empty(), history.getNextCommand());
    }

    @Test
    public void getNextCommand_pastNewestAfterTyping_returnsTypedText() {
        history.add("list");
        history.getPreviousCommand("add n/Half typed");

        assertEquals(Optional.of("add n/Half typed"), history.getNextCommand());
        assertEquals(Optional.empty(), history.getNextCommand());
    }

    @Test
    public void getPreviousCommand_whileRecalling_keepsTypedText() {
        history.add("list");
        history.add("delete 1");
        history.getPreviousCommand("add n/Half typed");
        // The box now shows the recalled command, which must not replace the typed text
        history.getPreviousCommand("delete 1");

        assertEquals(Optional.of("delete 1"), history.getNextCommand());
        assertEquals(Optional.of("add n/Half typed"), history.getNextCommand());
    }

    @Test
    public void add_afterGoingBack_recallsNewCommandFirst() {
        history.add("list");
        history.add("delete 1");
        history.getPreviousCommand("");
        history.getPreviousCommand("");
        history.add("clear");

        assertEquals(Optional.of("clear"), history.getPreviousCommand(""));
        assertEquals(Optional.of("delete 1"), history.getPreviousCommand(""));
    }

    @Test
    public void add_sameCommandTwice_recalledTwice() {
        history.add("list");
        history.add("list");

        assertEquals(Optional.of("list"), history.getPreviousCommand(""));
        assertEquals(Optional.of("list"), history.getPreviousCommand(""));
        assertEquals(Optional.empty(), history.getPreviousCommand(""));
    }
}

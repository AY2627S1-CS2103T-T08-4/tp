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
    public void recallPreviousCommand_null_throwsNullPointerException() {
        history.add("list");
        assertThrows(NullPointerException.class, () -> history.recallPreviousCommand(null));
    }

    @Test
    public void recallPreviousCommand_emptyHistory_returnsEmpty() {
        assertEquals(Optional.empty(), history.recallPreviousCommand(""));
    }

    @Test
    public void recallPreviousCommand_twoCommands_returnsNewestFirst() {
        history.add("list");
        history.add("delete 1");

        assertEquals(Optional.of("delete 1"), history.recallPreviousCommand(""));
        assertEquals(Optional.of("list"), history.recallPreviousCommand(""));
    }

    @Test
    public void recallPreviousCommand_atOldestCommand_returnsEmpty() {
        history.add("list");
        history.recallPreviousCommand("");

        assertEquals(Optional.empty(), history.recallPreviousCommand(""));
    }

    @Test
    public void recallNextCommand_emptyHistory_returnsEmpty() {
        assertEquals(Optional.empty(), history.recallNextCommand());
    }

    @Test
    public void recallNextCommand_nothingRecalled_returnsEmpty() {
        history.add("list");

        // the user may be typing a new command, which must be kept
        assertEquals(Optional.empty(), history.recallNextCommand());
    }

    @Test
    public void recallNextCommand_afterGoingBack_returnsNewerThenEmptyString() {
        history.add("list");
        history.add("delete 1");
        history.recallPreviousCommand("");
        history.recallPreviousCommand("");

        assertEquals(Optional.of("delete 1"), history.recallNextCommand());
        assertEquals(Optional.of(""), history.recallNextCommand());
        assertEquals(Optional.empty(), history.recallNextCommand());
    }

    @Test
    public void recallNextCommand_pastNewestAfterTyping_returnsTypedText() {
        history.add("list");
        history.recallPreviousCommand("add n/Half typed");

        assertEquals(Optional.of("add n/Half typed"), history.recallNextCommand());
        assertEquals(Optional.empty(), history.recallNextCommand());
    }

    @Test
    public void recallNextCommand_secondTripBack_returnsNewText() {
        history.add("list");
        history.recallPreviousCommand("first");
        history.recallNextCommand();
        history.recallPreviousCommand("second");

        assertEquals(Optional.of("second"), history.recallNextCommand());
    }

    @Test
    public void recallPreviousCommand_whileRecalling_keepsTypedText() {
        history.add("list");
        history.add("delete 1");
        history.recallPreviousCommand("add n/Half typed");
        // The box now shows the recalled command, which must not replace the typed text
        history.recallPreviousCommand("delete 1");

        assertEquals(Optional.of("delete 1"), history.recallNextCommand());
        assertEquals(Optional.of("add n/Half typed"), history.recallNextCommand());
    }

    @Test
    public void add_afterGoingBack_recallsNewCommandFirst() {
        history.add("list");
        history.add("delete 1");
        history.recallPreviousCommand("");
        history.recallPreviousCommand("");
        history.add("clear");

        assertEquals(Optional.of("clear"), history.recallPreviousCommand(""));
        assertEquals(Optional.of("delete 1"), history.recallPreviousCommand(""));
    }

    @Test
    public void add_sameCommandTwice_recalledTwice() {
        history.add("list");
        history.add("list");

        assertEquals(Optional.of("list"), history.recallPreviousCommand(""));
        assertEquals(Optional.of("list"), history.recallPreviousCommand(""));
        assertEquals(Optional.empty(), history.recallPreviousCommand(""));
    }
}

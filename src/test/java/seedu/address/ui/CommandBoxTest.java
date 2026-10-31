package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.event.ActionEvent;
import javafx.geometry.VerticalDirection;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

@ExtendWith(ApplicationExtension.class)
public class CommandBoxTest {

    private static final String VALID_COMMAND = "list";
    private static final String OTHER_VALID_COMMAND = "find Amirah";
    /** Parses, but fails when run, e.g. because the client list has no such index. */
    private static final String INVALID_COMMAND = "delete 99";
    /** Fails to parse, e.g. because of a typo in the command word. */
    private static final String UNPARSABLE_COMMAND = "lsit";

    private final List<String> executedCommands = new ArrayList<>();
    private final List<VerticalDirection> scrolledDirections = new ArrayList<>();
    private final List<VerticalDirection> clientListScrolledDirections = new ArrayList<>();
    private TextField commandTextField;

    @Start
    public void start(Stage stage) {
        CommandBox commandBox = new CommandBox(commandText -> {
            executedCommands.add(commandText);
            if (commandText.equals(INVALID_COMMAND)) {
                throw new CommandException("Invalid command");
            }
            if (commandText.equals(UNPARSABLE_COMMAND)) {
                throw new ParseException("Unknown command");
            }
            return new CommandResult("Command executed");
        }, scrolledDirections::add, clientListScrolledDirections::add);
        commandTextField = (TextField) commandBox.getRoot().lookup("#commandTextField");
        stage.setScene(new Scene(commandBox.getRoot()));
        stage.show();
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new CommandBox(null, scrolledDirections::add, clientListScrolledDirections::add));
        assertThrows(NullPointerException.class, () ->
                new CommandBox(commandText -> null, null, clientListScrolledDirections::add));
        assertThrows(NullPointerException.class, () ->
                new CommandBox(commandText -> null, scrolledDirections::add, null));
    }

    @Test
    public void handleCommandEntered_validCommand_executesAndClearsBox(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);

        assertEquals(List.of(VALID_COMMAND), executedCommands);
        assertEquals("", commandTextField.getText());
        assertFalse(hasErrorStyle());
    }

    @Test
    public void handleCommandEntered_invalidCommand_keepsTextAndShowsError(FxRobot robot) {
        enterCommand(robot, INVALID_COMMAND);

        assertEquals(INVALID_COMMAND, commandTextField.getText());
        assertTrue(hasErrorStyle());

        // entering it again must not add the error style twice
        enterCommand(robot, INVALID_COMMAND);
        assertEquals(1, commandTextField.getStyleClass().stream()
                .filter(CommandBox.ERROR_STYLE_CLASS::equals).count());
    }

    @Test
    public void handleCommandEntered_unparsableCommand_keepsTextAndShowsError(FxRobot robot) {
        enterCommand(robot, UNPARSABLE_COMMAND);

        assertEquals(List.of(UNPARSABLE_COMMAND), executedCommands);
        assertEquals(UNPARSABLE_COMMAND, commandTextField.getText());
        assertTrue(hasErrorStyle());
    }

    @Test
    public void commandTextField_editedAfterError_removesErrorStyle(FxRobot robot) {
        enterCommand(robot, INVALID_COMMAND);
        robot.interact(() -> commandTextField.setText("delete 9"));

        assertFalse(hasErrorStyle());
    }

    @Test
    public void handleCommandEntered_blankCommand_notExecutedOrRemembered(FxRobot robot) {
        enterCommand(robot, "   ");
        pressKey(robot, KeyCode.UP);

        assertTrue(executedCommands.isEmpty());
        assertEquals("   ", commandTextField.getText());
    }

    @Test
    public void handleKeyPressed_up_showsPreviousCommandsWithCaretAtEnd(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        enterCommand(robot, OTHER_VALID_COMMAND);

        pressKey(robot, KeyCode.UP);
        assertEquals(OTHER_VALID_COMMAND, commandTextField.getText());
        assertEquals(OTHER_VALID_COMMAND.length(), commandTextField.getCaretPosition());

        pressKey(robot, KeyCode.UP);
        assertEquals(VALID_COMMAND, commandTextField.getText());
    }

    @Test
    public void handleKeyPressed_upAfterFailedCommand_showsFailedCommand(FxRobot robot) {
        enterCommand(robot, INVALID_COMMAND);
        robot.interact(() -> commandTextField.setText(""));

        pressKey(robot, KeyCode.UP);
        assertEquals(INVALID_COMMAND, commandTextField.getText());
    }

    @Test
    public void handleKeyPressed_downPastNewest_clearsBox(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        enterCommand(robot, OTHER_VALID_COMMAND);
        pressKey(robot, KeyCode.UP);
        pressKey(robot, KeyCode.UP);

        pressKey(robot, KeyCode.DOWN);
        assertEquals(OTHER_VALID_COMMAND, commandTextField.getText());

        pressKey(robot, KeyCode.DOWN);
        assertEquals("", commandTextField.getText());
    }

    @Test
    public void handleKeyPressed_downWhileTyping_keepsTypedText(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        robot.interact(() -> commandTextField.setText("del"));

        pressKey(robot, KeyCode.DOWN);
        assertEquals("del", commandTextField.getText());
    }

    @Test
    public void handleKeyPressed_upThenDownWhileTyping_restoresTypedText(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        robot.interact(() -> commandTextField.setText("add n/Half typed"));

        pressKey(robot, KeyCode.UP);
        assertEquals(VALID_COMMAND, commandTextField.getText());

        pressKey(robot, KeyCode.DOWN);
        assertEquals("add n/Half typed", commandTextField.getText());
    }

    @Test
    public void handleCommandEntered_editedRecalledCommand_runsEditedText(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        pressKey(robot, KeyCode.UP);

        // The recalled command, changed before pressing Enter
        enterCommand(robot, OTHER_VALID_COMMAND);
        assertEquals(List.of(VALID_COMMAND, OTHER_VALID_COMMAND), executedCommands);

        // Both are remembered, the edited one as the newest
        pressKey(robot, KeyCode.UP);
        assertEquals(OTHER_VALID_COMMAND, commandTextField.getText());
        pressKey(robot, KeyCode.UP);
        assertEquals(VALID_COMMAND, commandTextField.getText());
    }

    @Test
    public void handleKeyPressed_downAfterEditingRecalledCommand_restoresTypedText(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        robot.interact(() -> commandTextField.setText("add n/Half typed"));
        pressKey(robot, KeyCode.UP);
        robot.interact(() -> commandTextField.setText(VALID_COMMAND + " edited"));

        // The edit to the recalled command is dropped, and the half-typed command comes back
        pressKey(robot, KeyCode.DOWN);
        assertEquals("add n/Half typed", commandTextField.getText());
    }

    @Test
    public void handleKeyPressed_upOrDownWithModifier_leavesTextUnchanged(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        enterCommand(robot, OTHER_VALID_COMMAND);
        robot.interact(() -> commandTextField.setText("del"));

        for (KeyCode key : new KeyCode[] {KeyCode.UP, KeyCode.DOWN}) {
            for (KeyCode modifier : new KeyCode[] {KeyCode.SHIFT, KeyCode.CONTROL, KeyCode.ALT, KeyCode.META}) {
                pressKey(robot, key, modifier);
                assertEquals("del", commandTextField.getText(), modifier + "+" + key);
            }
        }

        // The history has not moved, so a plain Up still shows the newest command
        pressKey(robot, KeyCode.UP);
        assertEquals(OTHER_VALID_COMMAND, commandTextField.getText());
    }

    @Test
    public void handleKeyPressed_otherKey_leavesTextUnchanged(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        robot.interact(() -> commandTextField.setText("del"));

        pressKey(robot, KeyCode.LEFT);
        assertEquals("del", commandTextField.getText());
        assertTrue(scrolledDirections.isEmpty());
    }

    @Test
    public void handleKeyPressed_pageUpAndPageDown_scrollResultAndKeepTypedText(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        robot.interact(() -> commandTextField.setText("del"));

        pressKey(robot, KeyCode.PAGE_DOWN);
        pressKey(robot, KeyCode.PAGE_UP);

        assertEquals(List.of(VerticalDirection.DOWN, VerticalDirection.UP), scrolledDirections);
        assertTrue(clientListScrolledDirections.isEmpty());
        assertEquals("del", commandTextField.getText());
        assertEquals(List.of(VALID_COMMAND), executedCommands);
    }

    @Test
    public void handleKeyPressed_shiftPageUpAndPageDown_scrollClientListAndKeepTypedText(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        robot.interact(() -> commandTextField.setText("del"));

        pressKey(robot, KeyCode.PAGE_DOWN, KeyCode.SHIFT);
        pressKey(robot, KeyCode.PAGE_UP, KeyCode.SHIFT);

        assertEquals(List.of(VerticalDirection.DOWN, VerticalDirection.UP), clientListScrolledDirections);
        assertTrue(scrolledDirections.isEmpty());
        assertEquals("del", commandTextField.getText());
        assertEquals(List.of(VALID_COMMAND), executedCommands);
    }

    @Test
    public void handleKeyPressed_pageDown_doesNotChangeCommandHistory(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        enterCommand(robot, OTHER_VALID_COMMAND);

        pressKey(robot, KeyCode.PAGE_DOWN);
        pressKey(robot, KeyCode.UP);

        assertEquals(OTHER_VALID_COMMAND, commandTextField.getText());
    }

    /**
     * Types {@code command} into the command box and presses Enter.
     */
    private void enterCommand(FxRobot robot, String command) {
        robot.interact(() -> {
            commandTextField.setText(command);
            commandTextField.fireEvent(new ActionEvent());
        });
    }

    /**
     * Presses {@code key} while the command box is focused, holding down any {@code modifiers}
     * ({@code SHIFT}, {@code CONTROL}, {@code ALT} or {@code META}).
     */
    private void pressKey(FxRobot robot, KeyCode key, KeyCode... modifiers) {
        List<KeyCode> heldKeys = List.of(modifiers);
        KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key, heldKeys.contains(KeyCode.SHIFT),
                heldKeys.contains(KeyCode.CONTROL), heldKeys.contains(KeyCode.ALT), heldKeys.contains(KeyCode.META));
        robot.interact(() -> commandTextField.fireEvent(event));
    }

    private boolean hasErrorStyle() {
        return commandTextField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS);
    }
}

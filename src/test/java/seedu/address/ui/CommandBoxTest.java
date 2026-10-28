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

@ExtendWith(ApplicationExtension.class)
public class CommandBoxTest {

    private static final String VALID_COMMAND = "list";
    private static final String OTHER_VALID_COMMAND = "find Amirah";
    private static final String INVALID_COMMAND = "edit 1 p/123";

    private final List<String> executedCommands = new ArrayList<>();
    private final List<VerticalDirection> scrolledDirections = new ArrayList<>();
    private TextField commandTextField;

    @Start
    public void start(Stage stage) {
        CommandBox commandBox = new CommandBox(commandText -> {
            executedCommands.add(commandText);
            if (commandText.equals(INVALID_COMMAND)) {
                throw new CommandException("Invalid command");
            }
            return new CommandResult("Command executed");
        }, scrolledDirections::add);
        commandTextField = (TextField) commandBox.getRoot().lookup("#commandTextField");
        stage.setScene(new Scene(commandBox.getRoot()));
        stage.show();
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandBox(null, scrolledDirections::add));
        assertThrows(NullPointerException.class, () -> new CommandBox(commandText -> null, null));
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
    public void handleCommandEntered_editAfterError_removesErrorStyle(FxRobot robot) {
        enterCommand(robot, INVALID_COMMAND);
        robot.interact(() -> commandTextField.setText("edit 1 p/91234567"));

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
    public void handleKeyPressed_upOrDownWithModifier_leavesTextUnchanged(FxRobot robot) {
        enterCommand(robot, VALID_COMMAND);
        enterCommand(robot, OTHER_VALID_COMMAND);
        robot.interact(() -> commandTextField.setText("del"));

        for (KeyCode key : new KeyCode[] {KeyCode.UP, KeyCode.DOWN}) {
            // Shift, Ctrl, Alt and Meta, one at a time
            for (int modifier = 0; modifier < 4; modifier++) {
                KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key,
                        modifier == 0, modifier == 1, modifier == 2, modifier == 3);
                robot.interact(() -> commandTextField.fireEvent(event));
                assertEquals("del", commandTextField.getText());
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
     * Presses {@code key} while the command box is focused.
     */
    private void pressKey(FxRobot robot, KeyCode key) {
        robot.interact(() -> commandTextField.fireEvent(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key, false, false, false, false)));
    }

    private boolean hasErrorStyle() {
        return commandTextField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS);
    }
}

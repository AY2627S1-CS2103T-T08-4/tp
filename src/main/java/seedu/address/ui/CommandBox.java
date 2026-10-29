package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.VerticalDirection;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandHistory commandHistory = new CommandHistory();
    private final CommandExecutor commandExecutor;
    private final ResultScroller resultScroller;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} that runs commands with {@code commandExecutor}, and scrolls the result of the
     * last command with {@code resultScroller}.
     */
    public CommandBox(CommandExecutor commandExecutor, ResultScroller resultScroller) {
        super(FXML);
        this.commandExecutor = requireNonNull(commandExecutor);
        this.resultScroller = requireNonNull(resultScroller);
        // A failed command stays red only until the user edits it
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        commandTextField.setOnKeyPressed(this::handleKeyPressed);
    }

    /**
     * Moves the keyboard focus to the command box, so that the user can keep typing.
     */
    public void requestFocus() {
        commandTextField.requestFocus();
    }

    /**
     * Shows the previous or next command in the history when the Up or Down keys are pressed, and scrolls the
     * result of the last command when the Page Up or Page Down keys are pressed.
     * The focus stays in the command box, so the user can keep typing.
     */
    private void handleKeyPressed(KeyEvent event) {
        KeyCode key = event.getCode();
        // Up and Down with a modifier are left to the text field, e.g. Shift+Up selects text
        if ((key == KeyCode.UP || key == KeyCode.DOWN) && hasModifierDown(event)) {
            return;
        }

        switch (key) {
            case PAGE_UP -> resultScroller.scrollPage(VerticalDirection.UP);
            case PAGE_DOWN -> resultScroller.scrollPage(VerticalDirection.DOWN);
            case UP -> showRecalledCommand(commandHistory.getPreviousCommand(commandTextField.getText()));
            case DOWN -> showRecalledCommand(commandHistory.getNextCommand());
            default -> {
                return;
            }
        }
        event.consume();
    }

    /**
     * Returns true if Shift, Ctrl, Alt or Meta is held down during {@code event}.
     */
    private static boolean hasModifierDown(KeyEvent event) {
        return event.isShiftDown() || event.isControlDown() || event.isAltDown() || event.isMetaDown();
    }

    /**
     * Shows {@code recalledCommand} in the command box with the cursor at the end, if there is one.
     */
    private void showRecalledCommand(Optional<String> recalledCommand) {
        recalledCommand.ifPresent(command -> {
            commandTextField.setText(command);
            commandTextField.end();
        });
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.isBlank()) {
            return;
        }

        commandHistory.add(commandText);

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

    /**
     * Represents a function that scrolls the result of the last command.
     */
    @FunctionalInterface
    public interface ResultScroller {
        /**
         * Scrolls the result one page in {@code direction}.
         *
         * @see ResultDisplay#scrollPage(VerticalDirection)
         */
        void scrollPage(VerticalDirection direction);
    }

}

package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

@ExtendWith(ApplicationExtension.class)
public class MainWindowTest {

    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private static final PseudoClass EMPTY_SEARCH_PSEUDO_CLASS = PseudoClass.getPseudoClass("empty-search");

    @TempDir
    public Path temporaryFolder;

    private Stage stage;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        Path addressBookFilePath = temporaryFolder.resolve("addressBook.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(addressBookFilePath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        MainWindow mainWindow = new MainWindow(stage, new LogicManager(new ModelManager(), storage),
                addressBookFilePath);
        mainWindow.fillInnerParts();
        stage.show();
    }

    @Test
    public void executeCommand_validCommand_showsSuccess(FxRobot robot) {
        enterCommand(robot, ListCommand.COMMAND_WORD);

        assertTrue(getResultMessage().getText().startsWith(ListCommand.MESSAGE_SUCCESS));
        assertFalse(getResultDisplay().getPseudoClassStates().contains(ERROR_PSEUDO_CLASS));
    }

    @Test
    public void executeCommand_searchWithNoMatches_showsEmptySearch(FxRobot robot) {
        enterCommand(robot, FindCommand.COMMAND_WORD + " Amirah");

        assertTrue(getResultMessage().getText().startsWith(FindCommand.MESSAGE_NO_CLIENTS_FOUND));
        assertTrue(getResultDisplay().getPseudoClassStates().contains(EMPTY_SEARCH_PSEUDO_CLASS));
    }

    @Test
    public void executeCommand_invalidCommand_showsError(FxRobot robot) {
        enterCommand(robot, "delete 0");

        assertTrue(getResultMessage().getText().startsWith("Invalid command format!"));
        assertTrue(getResultDisplay().getPseudoClassStates().contains(ERROR_PSEUDO_CLASS));
    }

    /**
     * Types {@code command} into the command box and presses Enter.
     */
    private void enterCommand(FxRobot robot, String command) {
        TextField commandTextField = (TextField) stage.getScene().lookup("#commandTextField");
        robot.interact(() -> {
            commandTextField.setText(command);
            commandTextField.fireEvent(new ActionEvent());
        });
    }

    private Label getResultMessage() {
        return (Label) stage.getScene().lookup("#resultMessage");
    }

    private Node getResultDisplay() {
        return stage.getScene().lookup(".result-display");
    }
}

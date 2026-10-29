package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

@ExtendWith(ApplicationExtension.class)
public class MainWindowTest {

    @TempDir
    public Path temporaryFolder;

    private Stage stage;
    private MainWindow mainWindow;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        Path addressBookFilePath = temporaryFolder.resolve("addressBook.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(addressBookFilePath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        mainWindow = new MainWindow(stage, new LogicManager(new ModelManager(), storage),
                addressBookFilePath);
        mainWindow.fillInnerParts();
        stage.show();
    }

    @Test
    public void executeCommand_validCommand_showsSuccess(FxRobot robot) {
        enterCommand(robot, ListCommand.COMMAND_WORD);

        assertTrue(getResultMessage().getText().startsWith(ListCommand.MESSAGE_SUCCESS));
        assertFalse(ResultDisplayTest.isShownAsError(getResultDisplay()));
    }

    @Test
    public void executeCommand_searchWithNoMatches_showsEmptySearch(FxRobot robot) {
        enterCommand(robot, FindCommand.COMMAND_WORD + " Amirah");

        assertTrue(getResultMessage().getText().startsWith(FindCommand.MESSAGE_NO_CLIENTS_FOUND));
        assertTrue(ResultDisplayTest.isShownAsEmptySearch(getResultDisplay()));
    }

    @Test
    public void executeCommand_errorWrapsAtMinimumWidth_shownInFullWithoutScrolling(FxRobot robot) {
        robot.interact(() -> stage.setWidth(stage.getMinWidth()));
        enterCommand(robot, "edit 1 p/123");

        Label resultMessage = getResultMessage();
        Node resultScrollPane = stage.getScene().lookup("#resultScrollPane");
        assertEquals(Phone.MESSAGE_CONSTRAINTS, ((Text) resultMessage.lookup(".text")).getText());
        assertEquals(resultMessage.getHeight(), resultScrollPane.getLayoutBounds().getHeight(), 0.5);
    }

    @Test
    public void pageDownInCommandBox_longResult_scrollsResult(FxRobot robot) {
        robot.interact(() -> stage.setWidth(stage.getMinWidth()));
        // Too long to show in full at the minimum width
        enterCommand(robot, "add n/Amirah Tan p/91234567 e/amirah@example.com a/" + "Blk 123 Yishun Ave 5 ".repeat(7)
                + "nt/" + "Weak at algebra ".repeat(12));

        ScrollPane resultScrollPane = (ScrollPane) stage.getScene().lookup("#resultScrollPane");
        TextField commandTextField = (TextField) stage.getScene().lookup("#commandTextField");
        robot.interact(() -> commandTextField.fireEvent(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.PAGE_DOWN, false, false, false, false)));

        assertTrue(resultScrollPane.getVvalue() > resultScrollPane.getVmin());
    }

    @Test
    public void commandBox_minimumWidth_promptTextFits(FxRobot robot) {
        robot.interact(() -> stage.setWidth(stage.getMinWidth()));
        TextField commandTextField = (TextField) stage.getScene().lookup("#commandTextField");

        Text promptText = new Text(commandTextField.getPromptText());
        promptText.setFont(commandTextField.getFont());
        Insets insets = commandTextField.getInsets();
        double textWidth = commandTextField.getWidth() - insets.getLeft() - insets.getRight();
        assertTrue(promptText.getLayoutBounds().getWidth() <= textWidth);
    }

    @Test
    public void executeCommand_invalidCommand_showsError(FxRobot robot) {
        enterCommand(robot, "delete 0");

        assertTrue(getResultMessage().getText().startsWith("Invalid command format!"));
        assertTrue(ResultDisplayTest.isShownAsError(getResultDisplay()));
    }

    @Test
    public void toggleTheme_switchesBetweenDarkAndLightThemes(FxRobot robot) {
        Button themeToggleButton = (Button) stage.getScene().lookup("#themeToggleButton");

        assertTrue(stage.getScene().getStylesheets().stream()
                .noneMatch(stylesheet -> stylesheet.endsWith("DarkTheme.css")));
        assertTrue(themeToggleButton.getText().equals("Dark theme"));

        robot.interact(themeToggleButton::fire);

        assertTrue(stage.getScene().getStylesheets().stream()
                .anyMatch(stylesheet -> stylesheet.endsWith("DarkTheme.css")));
        assertTrue(themeToggleButton.getText().equals("Light theme"));

        robot.interact(themeToggleButton::fire);

        assertTrue(stage.getScene().getStylesheets().stream()
                .noneMatch(stylesheet -> stylesheet.endsWith("DarkTheme.css")));
        assertTrue(themeToggleButton.getText().equals("Dark theme"));
    }

    @Test
    public void selectClients_thenClose_detailPanelUpdatesAndListSelectionClears(FxRobot robot)
            throws TimeoutException {
        enterCommand(robot, "add n/Ravi Kumar p/87654321 e/ravi.k@edu.sg a/10 Queenstown Rd t/physics");
        enterCommand(robot, "add n/Amirah Tan p/91234567 e/amirah@edu.sg a/20 Tampines Rd t/english");
        ListView<Person> personListView = getPersonListView();

        robot.interact(() -> personListView.getSelectionModel().select(0));
        assertEquals("Ravi Kumar", getDetailLabel("#name").getText());
        assertTrue(stage.getScene().lookup("#personDetailPanelPlaceholder").isVisible());

        robot.interact(() -> personListView.getSelectionModel().select(1));
        assertEquals("Amirah Tan", getDetailLabel("#name").getText());

        Button closeButton = (Button) stage.getScene().lookup("#closeDetailButton");
        robot.interact(closeButton::fire);
        assertTrue(personListView.getSelectionModel().isEmpty());
        waitForDetailPanelToClose();
    }

    @Test
    public void deleteSelectedClient_closesDetailPanel(FxRobot robot) throws TimeoutException {
        enterCommand(robot, "add n/Ravi Kumar p/87654321 e/ravi.k@edu.sg a/10 Queenstown Rd");
        ListView<Person> personListView = getPersonListView();
        robot.interact(() -> personListView.getSelectionModel().select(0));

        enterCommand(robot, "delete 1");

        assertTrue(personListView.getSelectionModel().isEmpty());
        waitForDetailPanelToClose();
    }

    @Test
    public void editSelectedClient_updatesOpenDetailPanel(FxRobot robot) {
        enterCommand(robot, "add n/Ravi Kumar p/87654321 e/ravi.k@edu.sg a/10 Queenstown Rd");
        ListView<Person> personListView = getPersonListView();
        robot.interact(() -> personListView.getSelectionModel().select(0));

        enterCommand(robot, "edit 1 p/81234567");

        assertEquals(0, personListView.getSelectionModel().getSelectedIndex());
        assertEquals("81234567", getDetailLabel("#phone").getText());
    }

    @Test
    public void escapeWithDetailOpen_closesDetailPanelAndConsumesEvent(FxRobot robot) throws TimeoutException {
        enterCommand(robot, "add n/Ravi Kumar p/87654321 e/ravi.k@edu.sg a/10 Queenstown Rd");
        ListView<Person> personListView = getPersonListView();
        robot.interact(() -> personListView.getSelectionModel().select(0));

        KeyEvent nonEscapeEvent = createKeyPressedEvent(KeyCode.ENTER);
        robot.interact(() -> stage.getScene().getRoot().fireEvent(nonEscapeEvent));
        assertFalse(nonEscapeEvent.isConsumed());
        assertFalse(personListView.getSelectionModel().isEmpty());

        AtomicBoolean escapeReachedHandler = new AtomicBoolean();
        stage.getScene().getRoot().addEventHandler(KeyEvent.KEY_PRESSED,
                event -> escapeReachedHandler.set(true));
        KeyEvent escapeEvent = createKeyPressedEvent(KeyCode.ESCAPE);
        robot.interact(() -> stage.getScene().getRoot().fireEvent(escapeEvent));
        assertFalse(escapeReachedHandler.get());
        assertTrue(personListView.getSelectionModel().isEmpty());
        waitForDetailPanelToClose();
    }

    @Test
    public void selectClientBeforeContentLayout_usesStageWidthForDetailWidth(FxRobot robot)
            throws TimeoutException {
        enterCommand(robot, "add n/Ravi Kumar p/87654321 e/ravi.k@edu.sg a/10 Queenstown Rd");
        Region clientContent = (Region) stage.getScene().lookup("#clientContent");
        ListView<Person> personListView = getPersonListView();

        robot.interact(() -> {
            stage.hide();
            clientContent.resize(0, clientContent.getHeight());
            personListView.getSelectionModel().select(0);
        });

        assertEquals(0, clientContent.getWidth());
        Node detailPlaceholder = stage.getScene().lookup("#personDetailPanelPlaceholder");
        WaitForAsyncUtils.waitFor(1, TimeUnit.SECONDS, () -> ((Region) detailPlaceholder).getPrefWidth() > 300);
    }

    @Test
    public void hidePersonDetail_detailAlreadyClosed_keepsDetailPanelHidden(FxRobot robot) {
        Node detailPlaceholder = stage.getScene().lookup("#personDetailPanelPlaceholder");

        robot.interact(this::invokeHidePersonDetail);

        assertFalse(detailPlaceholder.isVisible());
        assertFalse(detailPlaceholder.isManaged());
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

    @SuppressWarnings("unchecked")
    private ListView<Person> getPersonListView() {
        return (ListView<Person>) stage.getScene().lookup("#personListView");
    }

    private Label getDetailLabel(String selector) {
        Node detailPanel = stage.getScene().lookup("#personDetailPanel");
        return (Label) detailPanel.lookup(selector);
    }

    private void waitForDetailPanelToClose() throws TimeoutException {
        WaitForAsyncUtils.waitFor(1, TimeUnit.SECONDS, () ->
                !stage.getScene().lookup("#personDetailPanelPlaceholder").isVisible());
    }

    private KeyEvent createKeyPressedEvent(KeyCode keyCode) {
        return new KeyEvent(KeyEvent.KEY_PRESSED, "", "", keyCode, false, false, false, false);
    }

    private void invokeHidePersonDetail() {
        try {
            Method hidePersonDetail = MainWindow.class.getDeclaredMethod("hidePersonDetail");
            hidePersonDetail.setAccessible(true);
            hidePersonDetail.invoke(mainWindow);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new AssertionError(exception);
        }
    }
}

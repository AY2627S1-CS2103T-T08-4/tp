package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.control.Alert.AlertType;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Contains headless unit tests for the storage-corruption alert flow in {@code UiManager}.
 * The real dialog is never created: {@code showAlertDialogAndWait} is overridden to record its arguments.
 */
public class UiManagerTest {

    @TempDir
    public Path temporaryFolder;

    private Path dataFilePath;
    private int alertCount;
    private AlertType shownType;
    private String shownTitle;
    private String shownHeader;
    private String shownContent;

    @BeforeEach
    public void setUp() {
        dataFilePath = temporaryFolder.resolve("addressBook.json");
        alertCount = 0;
        shownType = null;
        shownTitle = null;
        shownHeader = null;
        shownContent = null;
    }

    @Test
    public void verifyStorageStateAndAlert_readOnlyMode_showsCorruptionWarning() {
        UiManager uiManager = createUiManager(createLogic(true));

        uiManager.verifyStorageStateAndAlert();

        assertEquals(1, alertCount);
        assertEquals(AlertType.WARNING, shownType);
        assertEquals("Data File Corruption Error", shownTitle);
        assertEquals("Local data file is corrupted.", shownHeader);
        assertEquals("Displaying empty list. Please fix or delete the data file at "
            + dataFilePath.toAbsolutePath() + " and restart.", shownContent);
    }

    @Test
    public void verifyStorageStateAndAlert_normalMode_showsNoAlert() {
        UiManager uiManager = createUiManager(createLogic(false));

        uiManager.verifyStorageStateAndAlert();

        assertEquals(0, alertCount);
        assertNull(shownType);
    }

    @Test
    public void triggerCorruptionAlert_errorLevel_usesGivenAlertType() {
        UiManager uiManager = createUiManager(createLogic(true));

        uiManager.triggerCorruptionAlert("ERROR");

        assertEquals(1, alertCount);
        assertEquals(AlertType.ERROR, shownType);
    }

    /**
     * Returns a {@code Logic} whose storage either throws on read (corrupted) or has no data file (healthy).
     */
    private Logic createLogic(boolean isDataCorrupted) {
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(dataFilePath) {
            @Override
            public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
                if (isDataCorrupted) {
                    throw new DataLoadingException(new Exception("Simulated failure"));
                }
                return Optional.empty();
            }
        };
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        return new LogicManager(new ModelManager(), storage, isDataCorrupted);
    }

    /**
     * Returns a {@code UiManager} that records alert arguments instead of showing a JavaFX dialog.
     */
    private UiManager createUiManager(Logic logic) {
        return new UiManager(logic, dataFilePath) {
            @Override
            void showAlertDialogAndWait(AlertType type, String title, String headerText, String contentText) {
                alertCount++;
                shownType = type;
                shownTitle = title;
                shownHeader = headerText;
                shownContent = contentText;
            }
        };
    }
}

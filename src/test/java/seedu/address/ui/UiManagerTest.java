package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class UiManagerTest {

    @TempDir
    public Path temporaryFolder;

    private UiManager uiManager;
    private Logic logic;
    private boolean isAlertCalled;

    @BeforeEach
    public void setUp() {
        Path dataFilePath = temporaryFolder.resolve("addressBook.json");
        Model model = new ModelManager();

        JsonAddressBookStorage corruptStorage = new JsonAddressBookStorage(dataFilePath) {
            @Override
            public java.util.Optional<seedu.address.model.ReadOnlyAddressBook> readAddressBook()
                    throws seedu.address.commons.exceptions.DataLoadingException {
                throw new seedu.address.commons.exceptions.DataLoadingException(new Exception("Simulated failure"));
            }
        };
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(corruptStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);
        isAlertCalled = false;

        uiManager = new UiManager(logic, dataFilePath) {
            @Override
            void triggerCorruptionAlert(String alertLevel) {
                isAlertCalled = true;
            }
        };
    }

    @Test
    public void verifyStorageStateAndAlert_readOnlyModeActive_triggersAlertFlow() {
        uiManager.verifyStorageStateAndAlert();

        assertTrue(isAlertCalled, "The warning dialog sequence was not triggered!");
        assertNotNull(uiManager);
    }
}

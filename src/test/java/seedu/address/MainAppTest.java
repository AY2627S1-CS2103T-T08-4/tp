package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Contains headless unit tests for the startup data-loading logic in {@code MainApp}.
 * {@code MainApp} is used as a plain object, so no JavaFX toolkit is started.
 */
public class MainAppTest {

    @TempDir
    public Path temporaryFolder;

    private MainApp mainApp;
    private JsonUserPrefsStorage userPrefsStorage;

    @BeforeEach
    public void setUp() {
        mainApp = new MainApp();
        userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
    }

    @Test
    public void initModelManager_corruptedDataFile_flagsCorruptionAndUsesEmptyBook() {
        JsonAddressBookStorage corruptStorage =
            new JsonAddressBookStorage(temporaryFolder.resolve("corrupted.json")) {
                @Override
                public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
                    throw new DataLoadingException(new Exception("Simulated file corruption."));
                }
            };
        StorageManager storage = new StorageManager(corruptStorage, userPrefsStorage);

        Model model = mainApp.initModelManager(storage, new UserPrefs());

        assertTrue(mainApp.isInitialDataCorrupted);
        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(DataLoadingException.MESSAGE_UNKNOWN_REASON, mainApp.initialDataCorruptionReason);
    }

    @Test
    public void initModelManager_missingDataFile_usesSampleDataAndNoCorruptionFlag() {
        JsonAddressBookStorage missingStorage =
            new JsonAddressBookStorage(temporaryFolder.resolve("missing.json"));
        StorageManager storage = new StorageManager(missingStorage, userPrefsStorage);

        Model model = mainApp.initModelManager(storage, new UserPrefs());

        assertFalse(mainApp.isInitialDataCorrupted);
        assertEquals(SampleDataUtil.getSampleAddressBook(), model.getAddressBook());
    }

    @Test
    public void initModelManager_validDataFile_loadsDataAndNoCorruptionFlag() throws IOException {
        JsonAddressBookStorage validStorage =
            new JsonAddressBookStorage(temporaryFolder.resolve("valid.json"));
        validStorage.saveAddressBook(getTypicalAddressBook());
        StorageManager storage = new StorageManager(validStorage, userPrefsStorage);

        Model model = mainApp.initModelManager(storage, new UserPrefs());

        assertFalse(mainApp.isInitialDataCorrupted);
        assertEquals(getTypicalAddressBook(), model.getAddressBook());
    }

    @Test
    public void init_corruptedDataFile_logicIsReadOnly() throws Exception {
        Path dataFile = temporaryFolder.resolve("corruptedSieve.json");
        Files.writeString(dataFile, "{ this is not valid json");
        mainApp.userPrefsFilePath = temporaryFolder.resolve("initPrefs.json");
        mainApp.addressBookFilePath = dataFile;

        mainApp.init();

        assertTrue(mainApp.isInitialDataCorrupted);
        assertEquals(DataLoadingException.MESSAGE_BAD_FORMAT, mainApp.initialDataCorruptionReason);
        assertThrows(CommandException.class,
            String.format(LogicManager.READ_ONLY_ERROR_FORMAT, dataFile, DataLoadingException.MESSAGE_BAD_FORMAT), () ->
                mainApp.logic.execute("clear"));
    }

    @Test
    public void init_invalidPhoneInDataFile_reasonNamesClient() throws Exception {
        Path dataFile = temporaryFolder.resolve("badPhoneSieve.json");
        Files.copy(Paths.get("src", "test", "data", "JsonSerializableAddressBookTest",
            "invalidPhonePersonAddressBook.json"), dataFile);
        mainApp.userPrefsFilePath = temporaryFolder.resolve("initPrefs.json");
        mainApp.addressBookFilePath = dataFile;

        mainApp.init();

        assertTrue(mainApp.isInitialDataCorrupted);
        assertTrue(mainApp.initialDataCorruptionReason.startsWith("Client 'Amirah Tan': "));
        assertThrows(CommandException.class,
            String.format(LogicManager.READ_ONLY_ERROR_FORMAT, dataFile, mainApp.initialDataCorruptionReason), () ->
                mainApp.logic.execute("clear"));
    }

    @Test
    public void init_nullPersonInDataFile_logicIsReadOnly() throws Exception {
        Path dataFile = temporaryFolder.resolve("nullPersonSieve.json");
        Files.writeString(dataFile, "{\"persons\":[null]}");
        mainApp.userPrefsFilePath = temporaryFolder.resolve("initPrefs.json");
        mainApp.addressBookFilePath = dataFile;

        mainApp.init();

        assertTrue(mainApp.isInitialDataCorrupted);
        assertEquals(new AddressBook(), mainApp.model.getAddressBook());
        assertThrows(CommandException.class, () -> mainApp.logic.execute("clear"));
    }

    @Test
    public void init_topLevelNullDataFile_logicIsReadOnly() throws Exception {
        Path dataFile = temporaryFolder.resolve("topLevelNullSieve.json");
        Files.writeString(dataFile, "null");
        mainApp.userPrefsFilePath = temporaryFolder.resolve("initPrefs.json");
        mainApp.addressBookFilePath = dataFile;

        mainApp.init();

        assertTrue(mainApp.isInitialDataCorrupted);
        assertEquals(new AddressBook(), mainApp.model.getAddressBook());
        assertThrows(CommandException.class, () -> mainApp.logic.execute("clear"));
    }

    @Test
    public void initPrefs_nullGuiSettings_usesAndSavesDefaultPreferences() throws Exception {
        Path prefsFile = temporaryFolder.resolve("nullGuiSettings.json");
        Files.writeString(prefsFile, "{\"guiSettings\":null}");
        JsonUserPrefsStorage storage = new JsonUserPrefsStorage(prefsFile);

        UserPrefs initializedPrefs = mainApp.initPrefs(storage);

        assertEquals(new UserPrefs(), initializedPrefs);
        assertEquals(new UserPrefs(), storage.readUserPrefs().get());
    }

    @Test
    public void init_missingDataFile_logicIsNotReadOnly() throws Exception {
        mainApp.userPrefsFilePath = temporaryFolder.resolve("initPrefs.json");
        mainApp.addressBookFilePath = temporaryFolder.resolve("missingSieve.json");

        mainApp.init();

        assertFalse(mainApp.isInitialDataCorrupted);
        assertEquals(SampleDataUtil.getSampleAddressBook(), mainApp.model.getAddressBook());
    }
}

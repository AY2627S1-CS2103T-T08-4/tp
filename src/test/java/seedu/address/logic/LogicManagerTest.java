package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains automated unit testing verifying command flow inside {@code LogicManager}.
 */
public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;
    private StorageManager storage;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
            new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, DeleteCommand.MESSAGE_EMPTY_PERSON_LIST);
    }

    @Test
    public void execute_indexTooLargeForInt_showsOutOfBounds() {
        assertCommandException("edit 2147483648 n/Amy", Messages.getIndexOutOfBoundsMessage(0));
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
            LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
            LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_successfulMutatingCommand_canUndo() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
        assertTrue(model.canUndoAddressBook());
    }

    @Test
    public void execute_nonMutatingCommand_nothingToUndo() throws Exception {
        logic.execute(ListCommand.COMMAND_WORD);
        assertFalse(model.canUndoAddressBook());
    }

    @Test
    public void execute_undoAfterAdd_removesAddedClient() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
        // list changes nothing, so it must not use up the undo
        logic.execute(ListCommand.COMMAND_WORD);

        assertCommandSuccess(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_SUCCESS, new ModelManager());
        assertCommandException(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void execute_undoAfterEditThatChangesNothing_revertsChangeBeforeIt() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
        // Amy already has this phone number, so the edit succeeds but changes nothing
        logic.execute(EditCommand.COMMAND_WORD + " 1" + PHONE_DESC_AMY);

        assertCommandSuccess(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_SUCCESS, new ModelManager());
        assertCommandException(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void execute_undoAfterUntag_restoresRemovedTag() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + " t/friends";
        logic.execute(addCommand);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        logic.execute(UntagCommand.COMMAND_WORD + " 1 t/friends");

        assertCommandSuccess(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_redoAfterUndo_restoresAddedClient() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        logic.execute(UndoCommand.COMMAND_WORD);
        // list changes nothing, so it must not clear the redo history
        logic.execute(ListCommand.COMMAND_WORD);

        assertCommandSuccess(RedoCommand.COMMAND_WORD, RedoCommand.MESSAGE_SUCCESS, expectedModel);
        assertCommandException(RedoCommand.COMMAND_WORD, RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }

    @Test
    public void execute_redoAfterNewChange_throwsCommandException() throws Exception {
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
        logic.execute(UndoCommand.COMMAND_WORD);
        // adding Amy again is a new change, so the undone add can no longer be redone
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);

        assertCommandException(RedoCommand.COMMAND_WORD, RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic
            .getFilteredPersonList()
            .remove(0));
    }

    @Test
    public void execute_mutatingCommandInReadOnlyMode_throwsCommandException() {
        StorageManager storage = createCorruptStorageHelper("corruptedAddressBook.json", "corruptPrefs.json");
        Logic logicInReadOnly = new LogicManager(model, storage, true);
        String expectedError = "Error: Local data file is corrupted at "
            + temporaryFolder.resolve("corruptedAddressBook.json")
            + ". Displaying empty list. Please fix or delete the data file and restart.";

        assertThrows(CommandException.class, expectedError, () -> logicInReadOnly.execute(
            "add n/Amirah Tan p/91234567 e/amirah@gmail.com a/Blk 123 Yishun"));
        assertThrows(CommandException.class, expectedError, () -> logicInReadOnly.execute("delete 1"));
        assertThrows(CommandException.class, expectedError, () -> logicInReadOnly.execute("delete 1, 2"));
        assertThrows(CommandException.class, expectedError, () -> logicInReadOnly.execute("edit 1 n/New Name"));
        assertThrows(CommandException.class, expectedError, () -> logicInReadOnly.execute("clear"));
        assertThrows(CommandException.class, expectedError, () -> logicInReadOnly.execute("tag 1 t/Math"));
        assertThrows(CommandException.class, expectedError, () -> logicInReadOnly.execute("untag 1 t/Math"));
        assertThrows(CommandException.class, expectedError, () -> logicInReadOnly.execute("undo"));
        assertThrows(CommandException.class, expectedError, () -> logicInReadOnly.execute("redo"));
    }

    @Test
    public void execute_nonMutatingCommandInReadOnlyMode_success() throws Exception {
        StorageManager storage =
            createCorruptStorageHelper("corruptedAddressBookOnlyList.json", "corruptListPrefs.json");
        Logic logicInReadOnly = new LogicManager(model, storage, true);

        String listCommand = ListCommand.COMMAND_WORD;
        CommandResult result = logicInReadOnly.execute(listCommand);
        assertEquals(ListCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertThrows(ParseException.class, () -> logicInReadOnly.execute("   "));
    }

    @Test
    public void execute_mutatingCommandInReadOnlyMode_fileRemainsByteIdentical() throws Exception {
        Path corruptPath = temporaryFolder.resolve("untouchedCorruptedFile.json");
        String invalidJson = "{ invalid raw data json context }";
        Files.writeString(corruptPath, invalidJson);

        JsonAddressBookStorage corruptStorageBackend = new JsonAddressBookStorage(corruptPath);
        JsonUserPrefsStorage userPrefsStorage =
            new JsonUserPrefsStorage(temporaryFolder.resolve("untouchedPrefs.json"));
        StorageManager storage = new StorageManager(corruptStorageBackend, userPrefsStorage);

        Logic logicInReadOnly = new LogicManager(model, storage, true);
        byte[] initialBytes = Files.readAllBytes(corruptPath);

        assertThrows(CommandException.class, () -> logicInReadOnly.execute(
            "add n/Amirah Tan p/91234567 e/amirah@gmail.com a/Blk 123 Yishun"));

        byte[] postExecutionBytes = Files.readAllBytes(corruptPath);
        assertArrayEquals(initialBytes, postExecutionBytes, "The corrupted file was mutated on disk!");
    }

    @Test
    public void execute_malformedMutatingCommandInReadOnlyMode_throwsParseException() {
        Logic logicInReadOnly = new LogicManager(model, storage, true);

        assertThrows(ParseException.class, () -> logicInReadOnly.execute("add n/John"));
    }

    /**
     * Helper method to generate an isolated, simulated file corruption StorageManager.
     */
    private StorageManager createCorruptStorageHelper(String addressBookFileName, String userPrefsFileName) {
        Path corruptPath = temporaryFolder.resolve(addressBookFileName);
        JsonAddressBookStorage corruptStorageBackend = new JsonAddressBookStorage(corruptPath) {
            @Override
            public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
                throw new DataLoadingException(new Exception("Simulated file corruption context."));
            }
        };
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve(userPrefsFileName));
        return new StorageManager(corruptStorageBackend, userPrefsStorage);
    }

    @Test
    public void isReadOnlyMode_corruptStorage_returnsTrue() {
        Path corruptPath = temporaryFolder.resolve("coverageReadOnlyBook.json");
        JsonAddressBookStorage corruptStorageBackend = new JsonAddressBookStorage(corruptPath) {
            @Override
            public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
                throw new DataLoadingException(new Exception("Simulated file corruption context."));
            }
        };
        JsonUserPrefsStorage userPrefsStorage =
            new JsonUserPrefsStorage(temporaryFolder.resolve("coverageReadOnlyPrefs.json"));
        StorageManager storage = new StorageManager(corruptStorageBackend, userPrefsStorage);

        Logic logicInReadOnly = new LogicManager(model, storage, true);
        org.junit.jupiter.api.Assertions.assertTrue(logicInReadOnly.isReadOnlyMode());
    }

    @Test
    public void constructor_readOnlyExplicitInitialization_setsReadOnlyMode() {
        LogicManager logicManager = new LogicManager(model, storage, true);
        assertThrows(CommandException.class, () -> logicManager.execute(
            AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
                                      Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
                                      String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     *
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
                                      String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e               the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
            new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
            + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY)
            .withTags()
            .build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}

package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_filteredList_showsAllClients() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person validPerson = new PersonBuilder().build();

        // expectedModel shows the full list, so this also checks that the filter is cleared
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)), expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                Messages.getClientAlreadyExistsMessage(personInList));
    }

    @Test
    public void execute_sameNameDifferentCaseAndPhone_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person samePersonDifferentPhone = new PersonBuilder(personInList)
                .withName(personInList.getName().fullName.toUpperCase()).withPhone("80000000").build();
        assertCommandFailure(new AddCommand(samePersonDifferentPhone), model,
                Messages.getNameAlreadyUsedMessage(personInList));
    }

    @Test
    public void execute_samePhoneDifferentName_success() {
        // siblings may share a parent's phone number, so this is not a duplicate
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person sibling = new PersonBuilder(personInList).withName("Sibling Of First").build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(sibling);

        assertCommandSuccess(new AddCommand(sibling), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(sibling)), expectedModel);
    }

}

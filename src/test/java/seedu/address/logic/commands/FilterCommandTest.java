package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonMatchesSearchPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class FilterCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        FilterCommand firstCommand = commandWith("friends");
        FilterCommand secondCommand = commandWith("owesMoney");

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(commandWith("friends")));
        assertFalse(firstCommand.equals(secondCommand));
        assertFalse(firstCommand.equals(1));
        assertFalse(firstCommand.equals(null));
    }

    @Test
    public void execute_singleTag_returnsAllMatchingPersons() {
        PersonMatchesSearchPredicate predicate = predicateWith("friends");
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, "3 clients found matching your search term(s).", expectedModel);
        assertEquals(List.of(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleTags_returnsPersonsWithAllTags() {
        PersonMatchesSearchPredicate predicate = predicateWith("FRIENDS", "owesmoney");
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, "1 client found matching your search term(s).", expectedModel);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_phone_returnsMatchingPerson() {
        PersonMatchesSearchPredicate predicate = new PersonMatchesSearchPredicate(
                List.of(), List.of(new Phone("94351253")), List.of());
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, "1 client found matching your search term(s).", expectedModel);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleDifferentPhones_returnsMatchingPersons() {
        PersonMatchesSearchPredicate predicate = new PersonMatchesSearchPredicate(List.of(),
                List.of(new Phone("94351253"), new Phone("98765432")), List.of());
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, "2 clients found matching your search term(s).", expectedModel);
        assertEquals(List.of(ALICE, BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_phoneAndTag_returnsPersonMatchingBoth() {
        PersonMatchesSearchPredicate predicate = new PersonMatchesSearchPredicate(
                List.of(), List.of(new Phone("98765432")), List.of(new Tag("friends")));
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, "1 client found matching your search term(s).", expectedModel);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void isMutating_filterCommand_returnsFalse() {
        assertFalse(commandWith("friends").isMutating());
    }

    @Test
    public void toStringMethod() {
        PersonMatchesSearchPredicate predicate = predicateWith("friends");
        FilterCommand command = new FilterCommand(predicate);
        String expected = FilterCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";

        assertEquals(expected, command.toString());
    }

    private FilterCommand commandWith(String... tagNames) {
        return new FilterCommand(predicateWith(tagNames));
    }

    private PersonMatchesSearchPredicate predicateWith(String... tagNames) {
        List<Tag> tags = List.of(tagNames).stream().map(Tag::new).toList();
        return new PersonMatchesSearchPredicate(List.of(), List.of(), tags);
    }
}

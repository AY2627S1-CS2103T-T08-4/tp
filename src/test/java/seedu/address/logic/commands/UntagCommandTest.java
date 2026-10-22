package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

public class UntagCommandTest {

    private static final Tag FRIENDS = new Tag("friends");
    private static final Tag OWES_MONEY = new Tag("owesMoney");
    private static final Tag PHYSICS = new Tag("physics");

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_emptyTagList_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new UntagCommand(INDEX_FIRST_PERSON, List.of()));
    }

    @Test
    public void execute_existingTag_removesTagWhilePreservingOtherTags() {
        Person personToUntag = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        UntagCommand untagCommand = new UntagCommand(INDEX_SECOND_PERSON, List.of(FRIENDS));

        String expectedMessage = String.format(UntagCommand.MESSAGE_UNTAG_PERSON_SUCCESS,
                personToUntag.getName(), FRIENDS.tagName);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToUntag, withoutTags(personToUntag, Set.of(FRIENDS)));

        assertCommandSuccess(untagCommand, model, expectedMessage, expectedModel);
        assertEquals(Set.of(OWES_MONEY),
                model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased()).getTags());
    }

    @Test
    public void execute_existingAndMissingTags_messageListsOnlyRemovedTags() {
        Person personToUntag = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        UntagCommand untagCommand = new UntagCommand(INDEX_FIRST_PERSON, List.of(FRIENDS, PHYSICS));

        String expectedMessage = String.format(UntagCommand.MESSAGE_UNTAG_PERSON_SUCCESS,
                personToUntag.getName(), FRIENDS.tagName);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToUntag, withoutTags(personToUntag, Set.of(FRIENDS)));

        assertCommandSuccess(untagCommand, model, expectedMessage, expectedModel);
        assertEquals(new UntagCommand(INDEX_FIRST_PERSON, List.of(FRIENDS, PHYSICS)), untagCommand);
    }

    @Test
    public void execute_existingTagWithDifferentCase_removesTag() {
        Person personToUntag = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag differentlyCasedTag = new Tag(FRIENDS.tagName.toUpperCase(Locale.ROOT));
        UntagCommand untagCommand = new UntagCommand(INDEX_FIRST_PERSON, List.of(differentlyCasedTag));

        String expectedMessage = String.format(UntagCommand.MESSAGE_UNTAG_PERSON_SUCCESS,
                personToUntag.getName(), differentlyCasedTag.tagName);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToUntag, withoutTags(personToUntag, Set.of(FRIENDS)));

        assertCommandSuccess(untagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_missingTag_returnsNotTaggedMessageWithoutChangingPerson() {
        Person personToUntag = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        UntagCommand untagCommand = new UntagCommand(INDEX_FIRST_PERSON, List.of(PHYSICS));

        String expectedMessage = String.format(UntagCommand.MESSAGE_PERSON_NOT_TAGGED, personToUntag.getName());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(untagCommand, model, expectedMessage, expectedModel);
        assertEquals(personToUntag, model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()));
    }

    @Test
    public void execute_validIndexFilteredList_untagsDisplayedPerson() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person personToUntag = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        UntagCommand untagCommand = new UntagCommand(INDEX_FIRST_PERSON, List.of(OWES_MONEY));

        String expectedMessage = String.format(UntagCommand.MESSAGE_UNTAG_PERSON_SUCCESS,
                personToUntag.getName(), OWES_MONEY.tagName);
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person expectedPerson = expectedModel.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        expectedModel.setPerson(expectedPerson, withoutTags(expectedPerson, Set.of(OWES_MONEY)));
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);

        assertCommandSuccess(untagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        int listSize = model.getFilteredPersonList().size();
        Index outOfBoundIndex = Index.fromOneBased(listSize + 1);
        UntagCommand untagCommand = new UntagCommand(outOfBoundIndex, List.of(FRIENDS));

        assertCommandFailure(untagCommand, model, Messages.getIndexOutOfBoundsMessage(listSize));
    }

    @Test
    public void execute_emptyList_throwsOutOfBoundsCommandException() {
        Model emptyModel = new ModelManager();
        UntagCommand untagCommand = new UntagCommand(INDEX_FIRST_PERSON, List.of(FRIENDS));

        assertCommandFailure(untagCommand, emptyModel,
                "Error: The index provided is out of bounds! Current list only contains 0 clients.");
    }

    @Test
    public void equals() {
        UntagCommand firstCommand = new UntagCommand(INDEX_FIRST_PERSON, List.of(FRIENDS));
        UntagCommand firstCommandCopy = new UntagCommand(INDEX_FIRST_PERSON, List.of(FRIENDS));
        UntagCommand differentIndexCommand = new UntagCommand(INDEX_SECOND_PERSON, List.of(FRIENDS));
        UntagCommand differentTagsCommand = new UntagCommand(INDEX_FIRST_PERSON, List.of(OWES_MONEY));

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(firstCommandCopy));
        assertFalse(firstCommand.equals(differentIndexCommand));
        assertFalse(firstCommand.equals(differentTagsCommand));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        UntagCommand untagCommand = new UntagCommand(INDEX_FIRST_PERSON, List.of(FRIENDS));
        String expected = UntagCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", tagsToRemove=" + List.of(FRIENDS) + "}";

        assertEquals(expected, untagCommand.toString());
    }

    private Person withoutTags(Person person, Set<Tag> tagsToRemove) {
        Set<Tag> remainingTags = new HashSet<>(person.getTags());
        remainingTags.removeAll(tagsToRemove);
        return new Person(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(), person.getNote(),
                remainingTags);
    }
}

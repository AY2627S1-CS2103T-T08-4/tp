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
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

public class TagCommandTest {

    private static final Tag PHYSICS = new Tag("physics");
    private static final Tag SECONDARY_FOUR = new Tag("Secondary 4");

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_emptyTagList_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TagCommand(INDEX_FIRST_PERSON, List.of()));
    }

    @Test
    public void execute_validIndex_addsTagsWhilePreservingExistingTags() {
        Person personToTag = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, List.of(PHYSICS, SECONDARY_FOUR));

        String expectedMessage = String.format(TagCommand.MESSAGE_TAG_PERSON_SUCCESS,
                personToTag.getName(), "physics, Secondary 4");
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToTag, withAdditionalTags(personToTag, Set.of(PHYSICS, SECONDARY_FOUR)));

        assertCommandSuccess(tagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_existingTagWithDifferentCase_successWithoutDuplicate() {
        Person personToTag = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag existingTag = personToTag.getTags().iterator().next();
        Tag differentlyCasedTag = new Tag(existingTag.tagName.toUpperCase(Locale.ROOT));
        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, List.of(differentlyCasedTag));

        String expectedMessage = String.format(TagCommand.MESSAGE_TAG_PERSON_SUCCESS,
                personToTag.getName(), differentlyCasedTag.tagName);
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(tagCommand, model, expectedMessage, expectedModel);
        Person updatedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(personToTag.getTags(), updatedPerson.getTags());
        assertEquals(personToTag.getTags().size(), updatedPerson.getTags().size());
    }

    @Test
    public void execute_validIndexFilteredList_tagsDisplayedPerson() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person personToTag = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, List.of(PHYSICS));

        String expectedMessage = String.format(TagCommand.MESSAGE_TAG_PERSON_SUCCESS,
                personToTag.getName(), PHYSICS.tagName);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToTag, withAdditionalTags(personToTag, Set.of(PHYSICS)));
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(tagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        int listSize = model.getFilteredPersonList().size();
        Index outOfBoundIndex = Index.fromOneBased(listSize + 1);
        TagCommand tagCommand = new TagCommand(outOfBoundIndex, List.of(PHYSICS));

        assertCommandFailure(tagCommand, model,
                String.format(TagCommand.MESSAGE_INDEX_OUT_OF_BOUNDS, listSize));
    }

    @Test
    public void execute_emptyList_throwsOutOfBoundsCommandException() {
        Model emptyModel = new ModelManager();
        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, List.of(PHYSICS));

        assertCommandFailure(tagCommand, emptyModel,
                String.format(TagCommand.MESSAGE_INDEX_OUT_OF_BOUNDS, 0));
    }

    @Test
    public void equals() {
        TagCommand tagFirstCommand = new TagCommand(INDEX_FIRST_PERSON, List.of(PHYSICS));
        TagCommand tagFirstCommandCopy = new TagCommand(INDEX_FIRST_PERSON, List.of(PHYSICS));
        TagCommand tagSecondIndexCommand = new TagCommand(INDEX_SECOND_PERSON, List.of(PHYSICS));
        TagCommand tagDifferentTagsCommand = new TagCommand(INDEX_FIRST_PERSON, List.of(SECONDARY_FOUR));

        assertTrue(tagFirstCommand.equals(tagFirstCommand));
        assertTrue(tagFirstCommand.equals(tagFirstCommandCopy));
        assertFalse(tagFirstCommand.equals(tagSecondIndexCommand));
        assertFalse(tagFirstCommand.equals(tagDifferentTagsCommand));
        assertFalse(tagFirstCommand.equals(null));
        assertFalse(tagFirstCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, List.of(PHYSICS));
        String expected = TagCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", tagsToAdd=" + List.of(PHYSICS) + "}";

        assertEquals(expected, tagCommand.toString());
    }

    private Person withAdditionalTags(Person person, Set<Tag> additionalTags) {
        Set<Tag> combinedTags = new HashSet<>(person.getTags());
        combinedTags.addAll(additionalTags);
        return new Person(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(), combinedTags);
    }
}

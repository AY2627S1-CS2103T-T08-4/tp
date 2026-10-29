package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Adds one or more tags to the person identified by their displayed index.
 */
public class TagCommand extends Command {

    public static final String COMMAND_WORD = "tag";

    public static final String MESSAGE_USAGE = "Expected format: " + COMMAND_WORD
        + " INDEX t/TAG [t/MORE_TAGS]...";

    public static final String MESSAGE_TAG_PERSON_SUCCESS = "Tagged %1$s with: %2$s.";
    public static final String MESSAGE_TAGS_ALREADY_PRESENT = "\nAlready present: %s.";
    public static final String MESSAGE_PERSON_ALREADY_TAGGED =
            "No tags added to %1$s. Already present: %2$s.";

    private final Index targetIndex;
    private final List<Tag> tagsToAdd;

    /**
     * Creates a {@code TagCommand} that adds {@code tagsToAdd} to the person at {@code targetIndex}.
     */
    public TagCommand(Index targetIndex, List<Tag> tagsToAdd) {
        requireNonNull(targetIndex);
        requireAllNonNull(tagsToAdd);
        checkArgument(!tagsToAdd.isEmpty(), "At least one tag must be provided.");

        this.targetIndex = targetIndex;
        this.tagsToAdd = List.copyOf(new LinkedHashSet<>(tagsToAdd));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.getIndexOutOfBoundsMessage(lastShownList.size()));
        }

        Person personToTag = lastShownList.get(targetIndex.getZeroBased());
        List<Tag> newTagsToAdd = getNewTags(personToTag);
        List<Tag> existingTags = getExistingTags(personToTag);

        if (newTagsToAdd.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_PERSON_ALREADY_TAGGED,
                    personToTag.getName(), getTagNames(existingTags)));
        }

        Person taggedPerson = createTaggedPerson(personToTag, newTagsToAdd);
        Set<Person> displayedPersons = new HashSet<>(lastShownList);
        model.setPerson(personToTag, taggedPerson);
        updateDisplayedPersons(model, displayedPersons, personToTag, taggedPerson);

        String message = String.format(MESSAGE_TAG_PERSON_SUCCESS, personToTag.getName(), getTagNames(newTagsToAdd));
        if (!existingTags.isEmpty()) {
            message += String.format(MESSAGE_TAGS_ALREADY_PRESENT, getTagNames(existingTags));
        }

        return new CommandResult(message);
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    /**
     * Returns the requested tags that {@code person} does not already have.
     */
    private List<Tag> getNewTags(Person person) {
        return tagsToAdd
                .stream()
                .filter(tag -> !person.getTags().contains(tag))
                .toList();
    }

    /**
     * Returns the requested tags that {@code person} already has, using their stored names.
     */
    private List<Tag> getExistingTags(Person person) {
        return tagsToAdd.stream()
                .flatMap(requestedTag -> person.getTags().stream().filter(requestedTag::equals))
                .toList();
    }

    /**
     * Returns the tag names as a comma-separated string.
     */
    private String getTagNames(List<Tag> tags) {
        return tags.stream()
                .map(tag -> tag.tagName)
                .collect(Collectors.joining(", "));
    }

    /**
     * Returns a copy of {@code personToTag} containing both its existing tags and the requested new tags.
     */
    private Person createTaggedPerson(Person personToTag, List<Tag> newTagsToAdd) {
        Set<Tag> updatedTags = new HashSet<>(personToTag.getTags());
        updatedTags.addAll(newTagsToAdd);
        return new Person(personToTag.getName(), personToTag.getPhone(), personToTag.getEmail(),
            personToTag.getAddress(), personToTag.getNote(), personToTag.getFee(), personToTag.getDueDate(),
            updatedTags);
    }

    /**
     * Keeps the same clients displayed after replacing one of them with its updated version.
     */
    private void updateDisplayedPersons(Model model, Set<Person> displayedPersons, Person originalPerson,
                                        Person updatedPerson) {
        displayedPersons.remove(originalPerson);
        displayedPersons.add(updatedPerson);
        model.updateFilteredPersonList(displayedPersons::contains);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagCommand otherTagCommand)) {
            return false;
        }

        return targetIndex.equals(otherTagCommand.targetIndex)
            && tagsToAdd.equals(otherTagCommand.tagsToAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .add("targetIndex", targetIndex)
            .add("tagsToAdd", tagsToAdd)
            .toString();
    }
}

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

    public static final String MESSAGE_INDEX_OUT_OF_BOUNDS =
            "Error: The index provided is out of bounds! Current list only contains %1$d items.";

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
            throw new CommandException(String.format(MESSAGE_INDEX_OUT_OF_BOUNDS, lastShownList.size()));
        }

        Person personToTag = lastShownList.get(targetIndex.getZeroBased());
        Person taggedPerson = createTaggedPerson(personToTag);
        model.setPerson(personToTag, taggedPerson);

        String tagNames = tagsToAdd.stream()
                .map(tag -> tag.tagName)
                .collect(Collectors.joining(", "));
        return new CommandResult(String.format(MESSAGE_TAG_PERSON_SUCCESS, personToTag.getName(), tagNames));
    }

    /**
     * Returns a copy of {@code personToTag} containing both its existing tags and the requested new tags.
     */
    private Person createTaggedPerson(Person personToTag) {
        Set<Tag> updatedTags = new HashSet<>(personToTag.getTags());
        updatedTags.addAll(tagsToAdd);
        return new Person(personToTag.getName(), personToTag.getPhone(), personToTag.getEmail(),
                personToTag.getAddress(), updatedTags);
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

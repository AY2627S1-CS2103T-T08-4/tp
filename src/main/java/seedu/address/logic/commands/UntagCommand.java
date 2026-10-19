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
 * Removes one or more tags from the person identified by their displayed index.
 */
public class UntagCommand extends Command {

    public static final String COMMAND_WORD = "untag";

    public static final String MESSAGE_USAGE = "Expected format: " + COMMAND_WORD
            + " INDEX t/TAG [t/MORE_TAGS]...";

    public static final String MESSAGE_UNTAG_PERSON_SUCCESS = "Removed from %1$s: %2$s.";
    public static final String MESSAGE_PERSON_NOT_TAGGED = "%1$s does not have any of the specified tags.";

    private final Index targetIndex;
    private final List<Tag> tagsToRemove;

    /**
     * Creates an {@code UntagCommand} that removes {@code tagsToRemove} from the person at {@code targetIndex}.
     */
    public UntagCommand(Index targetIndex, List<Tag> tagsToRemove) {
        requireNonNull(targetIndex);
        requireAllNonNull(tagsToRemove);
        checkArgument(!tagsToRemove.isEmpty(), "At least one tag must be provided.");

        this.targetIndex = targetIndex;
        this.tagsToRemove = List.copyOf(new LinkedHashSet<>(tagsToRemove));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.getIndexOutOfBoundsMessage(lastShownList.size()));
        }

        Person personToUntag = lastShownList.get(targetIndex.getZeroBased());
        List<Tag> existingTagsToRemove = getExistingTags(personToUntag);

        if (existingTagsToRemove.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_PERSON_NOT_TAGGED, personToUntag.getName()));
        }

        Person untaggedPerson = createUntaggedPerson(personToUntag, existingTagsToRemove);
        model.setPerson(personToUntag, untaggedPerson);

        String tagNames = existingTagsToRemove.stream()
                .map(tag -> tag.tagName)
                .collect(Collectors.joining(", "));

        return new CommandResult(String.format(MESSAGE_UNTAG_PERSON_SUCCESS, personToUntag.getName(), tagNames));
    }

    /**
     * Returns the requested tags that {@code person} currently has.
     */
    private List<Tag> getExistingTags(Person person) {
        return tagsToRemove
                .stream()
                .filter(person.getTags()::contains)
                .toList();
    }

    /**
     * Returns a copy of {@code personToUntag} without the specified existing tags.
     */
    private Person createUntaggedPerson(Person personToUntag, List<Tag> existingTagsToRemove) {
        Set<Tag> updatedTags = new HashSet<>(personToUntag.getTags());
        updatedTags.removeAll(existingTagsToRemove);
        return new Person(personToUntag.getName(), personToUntag.getPhone(), personToUntag.getEmail(),
                personToUntag.getAddress(), updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UntagCommand otherUntagCommand)) {
            return false;
        }

        return targetIndex.equals(otherUntagCommand.targetIndex)
                && tagsToRemove.equals(otherUntagCommand.tagsToRemove);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("tagsToRemove", tagsToRemove)
                .toString();
    }
}

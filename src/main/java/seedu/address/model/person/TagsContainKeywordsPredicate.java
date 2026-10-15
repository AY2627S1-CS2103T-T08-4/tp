package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Tests that a {@code Person} has any of the specified tags.
 */
public class TagsContainKeywordsPredicate implements Predicate<Person> {

    private final Set<Tag> keywords;

    /**
     * Creates a predicate that matches a person who has any tag in {@code keywords}.
     */
    public TagsContainKeywordsPredicate(Collection<Tag> keywords) {
        requireNonNull(keywords);
        this.keywords = Set.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        return person.getTags().stream().anyMatch(keywords::contains);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TagsContainKeywordsPredicate otherTagsContainKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherTagsContainKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}

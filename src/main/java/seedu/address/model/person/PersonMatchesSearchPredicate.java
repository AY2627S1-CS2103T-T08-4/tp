package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Tests that a {@code Person} matches every search field given: phone numbers and tags.
 * Matching any phone number is enough, while every specified tag must match.
 * A field with no values places no restriction on the person.
 */
public class PersonMatchesSearchPredicate implements Predicate<Person> {
    private final PhoneMatchesKeywordsPredicate phonePredicate;
    private final TagsContainKeywordsPredicate tagPredicate;
    private final Set<Phone> phones;
    private final Set<Tag> tags;

    /**
     * Creates a predicate for the given phone numbers and tags, either of which may be empty.
     */
    public PersonMatchesSearchPredicate(Collection<Phone> phones, Collection<Tag> tags) {
        requireNonNull(phones);
        requireNonNull(tags);
        this.phones = Set.copyOf(phones);
        this.phonePredicate = new PhoneMatchesKeywordsPredicate(this.phones);
        this.tags = Set.copyOf(tags);
        this.tagPredicate = new TagsContainKeywordsPredicate(this.tags);
    }

    @Override
    public boolean test(Person person) {
        boolean phoneMatches = phones.isEmpty() || phonePredicate.test(person);
        boolean tagMatches = tags.isEmpty() || tagPredicate.test(person);
        return phoneMatches && tagMatches;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonMatchesSearchPredicate otherPredicate)) {
            return false;
        }

        return phones.equals(otherPredicate.phones)
                && tags.equals(otherPredicate.tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("phones", phones)
                .add("tags", tags)
                .toString();
    }
}

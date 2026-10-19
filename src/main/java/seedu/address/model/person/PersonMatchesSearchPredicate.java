package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Tests that a {@code Person} matches every search field given: name keywords, phone numbers and tags.
 * Matching any name keyword is enough, while every specified phone number and tag must match.
 * A field with no values places no restriction on the person.
 */
public class PersonMatchesSearchPredicate implements Predicate<Person> {
    private final NameContainsKeywordsPredicate namePredicate;
    private final TagsContainKeywordsPredicate tagPredicate;
    private final List<String> nameKeywords;
    private final Set<Phone> phones;
    private final Set<Tag> tags;

    /**
     * Creates a predicate for the given name keywords, phone numbers and tags, any of which may be empty.
     */
    public PersonMatchesSearchPredicate(List<String> nameKeywords, Collection<Phone> phones, Collection<Tag> tags) {
        requireNonNull(nameKeywords);
        requireNonNull(phones);
        requireNonNull(tags);
        this.nameKeywords = List.copyOf(nameKeywords);
        this.namePredicate = new NameContainsKeywordsPredicate(this.nameKeywords);
        this.phones = Set.copyOf(phones);
        this.tags = Set.copyOf(tags);
        this.tagPredicate = new TagsContainKeywordsPredicate(this.tags);
    }

    @Override
    public boolean test(Person person) {
        boolean nameMatches = nameKeywords.isEmpty() || namePredicate.test(person);
        boolean phoneMatches = phones.isEmpty() || phones.stream().allMatch(person.getPhone()::equals);
        boolean tagMatches = tags.isEmpty() || tagPredicate.test(person);
        return nameMatches && phoneMatches && tagMatches;
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

        return nameKeywords.equals(otherPredicate.nameKeywords)
                && phones.equals(otherPredicate.phones)
                && tags.equals(otherPredicate.tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeywords", nameKeywords)
                .add("phones", phones)
                .add("tags", tags)
                .toString();
    }
}

package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s phone number matches any of the specified phone numbers.
 */
public class PhoneMatchesKeywordsPredicate implements Predicate<Person> {

    private final Set<Phone> keywords;

    /**
     * Creates a predicate that matches a person whose phone number matches any {@code keywords}.
     */
    public PhoneMatchesKeywordsPredicate(Collection<Phone> keywords) {
        requireNonNull(keywords);
        this.keywords = Set.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream().anyMatch(person.getPhone()::equals);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PhoneMatchesKeywordsPredicate otherPhoneMatchesKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherPhoneMatchesKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}

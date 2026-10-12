package seedu.address.model.person;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 * A keyword matches a word of the name, or a part of a word split on punctuation
 * (e.g. "Brien" matches "O'Brien", "Aisyah" matches "Nur-Aisyah"), ignoring case and punctuation.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private static final String PUNCTUATION_REGEX = "[" + Name.NAME_PUNCTUATION + "]";

    private final List<String> keywords;

    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        Set<String> nameParts = getSearchableParts(person.getName().fullName);
        return keywords.stream()
                .map(NameContainsKeywordsPredicate::normalize)
                .anyMatch(keyword -> !keyword.isEmpty() && nameParts.contains(keyword));
    }

    /**
     * Returns the normalized parts of {@code fullName} that a keyword can match:
     * each word with punctuation removed (so "obrien" matches "O'Brien"), and
     * each piece of a word split on punctuation (so "Brien" matches "O'Brien").
     */
    private static Set<String> getSearchableParts(String fullName) {
        Set<String> parts = new HashSet<>();
        for (String word : fullName.split("\\s+")) {
            parts.add(normalize(word));
            for (String piece : word.split(PUNCTUATION_REGEX)) {
                parts.add(normalize(piece));
            }
        }
        return parts;
    }

    /**
     * Returns {@code text} in lower case with name punctuation removed.
     */
    private static String normalize(String text) {
        return text.replaceAll(PUNCTUATION_REGEX, "").toLowerCase();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}

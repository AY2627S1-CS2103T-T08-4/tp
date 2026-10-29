package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 * A keyword matches the start of a word of the name, or the start of a part of a word split on punctuation
 * (e.g. "Bri" matches "O'Brien", "Ais" matches "Nur-Aisyah"), ignoring case and punctuation.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private static final String PUNCTUATION_REGEX = "[" + Name.NAME_PUNCTUATION + "]";

    private final List<String> keywords;

    /**
     * Creates a predicate that matches names against a copy of {@code keywords}.
     */
    public NameContainsKeywordsPredicate(List<String> keywords) {
        requireNonNull(keywords);
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        Set<String> nameParts = getSearchableParts(person.getName().fullName);
        return keywords.stream()
                .map(NameContainsKeywordsPredicate::normalize)
                .anyMatch(keyword -> !keyword.isEmpty() && isPrefixOfAnyPart(keyword, nameParts));
    }

    /**
     * Returns true if some part in {@code nameParts} starts with {@code keyword}.
     */
    private static boolean isPrefixOfAnyPart(String keyword, Set<String> nameParts) {
        return nameParts.stream().anyMatch(part -> part.startsWith(keyword));
    }

    /**
     * Returns the normalized parts of {@code fullName} that a keyword can match:
     * each word with punctuation removed (so "obri" matches "O'Brien"), and
     * each piece of a word split on punctuation (so "Bri" matches "O'Brien").
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
        return text.replaceAll(PUNCTUATION_REGEX, "").toLowerCase(Locale.ROOT);
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
    public int hashCode() {
        return keywords.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}

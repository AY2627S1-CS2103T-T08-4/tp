package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate(List.of("Alice", "Bob"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Bob", "Carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate(List.of("aLIce", "bOB"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_keywordMatchesNamePart_returnsTrue() {
        Person liam = new PersonBuilder().withName("Liam O'Brien").build();
        Person nurAisyah = new PersonBuilder().withName("Nur-Aisyah Tan").build();

        // whole word with punctuation
        assertTrue(new NameContainsKeywordsPredicate(List.of("O'Brien")).test(liam));

        // part of a word after an apostrophe
        assertTrue(new NameContainsKeywordsPredicate(List.of("Brien")).test(liam));

        // punctuation left out of the keyword
        assertTrue(new NameContainsKeywordsPredicate(List.of("obrien")).test(liam));

        // parts of a hyphenated word
        assertTrue(new NameContainsKeywordsPredicate(List.of("Nur")).test(nurAisyah));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Aisyah")).test(nurAisyah));
        assertTrue(new NameContainsKeywordsPredicate(List.of("NurAisyah")).test(nurAisyah));

        // punctuation in the keyword but not in the name
        assertTrue(new NameContainsKeywordsPredicate(List.of("O'Brien"))
                .test(new PersonBuilder().withName("Liam OBrien").build()));

        // full stop
        assertTrue(new NameContainsKeywordsPredicate(List.of("Jr"))
                .test(new PersonBuilder().withName("David Ray Jr.").build()));
    }

    @Test
    public void test_keywordDoesNotMatchNamePart_returnsFalse() {
        Person liam = new PersonBuilder().withName("Liam O'Brien").build();

        // part of a name part
        assertFalse(new NameContainsKeywordsPredicate(List.of("Bri")).test(liam));

        // punctuation-only keyword
        assertFalse(new NameContainsKeywordsPredicate(List.of("'")).test(liam));

        // keyword spanning two words
        assertFalse(new NameContainsKeywordsPredicate(List.of("Aisyah-Tan"))
                .test(new PersonBuilder().withName("Nur-Aisyah Tan").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Keywords match phone, email and address, but do not match name
        predicate = new NameContainsKeywordsPredicate(List.of("12345678", "alice@email.com", "Main", "Street"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345678")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywords);

        String expected = NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}

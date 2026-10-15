package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class PersonMatchesSearchPredicateTest {

    private static final Person LIAM = new PersonBuilder().withName("Liam O'Brien").withPhone("91234567")
            .withTags("sec4", "physics").build();

    @Test
    public void equals() {
        PersonMatchesSearchPredicate firstPredicate = new PersonMatchesSearchPredicate(
                List.of("Liam"), List.of(new Phone("91234567")), List.of(new Tag("sec4")));

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        assertTrue(firstPredicate.equals(new PersonMatchesSearchPredicate(
                List.of("Liam"), List.of(new Phone("91234567")), List.of(new Tag("sec4")))));

        // tags differing only in case -> returns true
        assertTrue(firstPredicate.equals(new PersonMatchesSearchPredicate(
                List.of("Liam"), List.of(new Phone("91234567")), List.of(new Tag("SEC4")))));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different name keywords -> returns false
        assertFalse(firstPredicate.equals(new PersonMatchesSearchPredicate(
                List.of("Nur"), List.of(new Phone("91234567")), List.of(new Tag("sec4")))));

        // different phones -> returns false
        assertFalse(firstPredicate.equals(new PersonMatchesSearchPredicate(
                List.of("Liam"), List.of(new Phone("98765432")), List.of(new Tag("sec4")))));

        // different tags -> returns false
        assertFalse(firstPredicate.equals(new PersonMatchesSearchPredicate(
                List.of("Liam"), List.of(new Phone("91234567")), List.of(new Tag("sec3")))));
    }

    @Test
    public void test_singleFieldMatches_returnsTrue() {
        // name only, using name part matching
        assertTrue(new PersonMatchesSearchPredicate(List.of("Brien"), List.of(), List.of()).test(LIAM));

        // phone only
        assertTrue(new PersonMatchesSearchPredicate(List.of(), List.of(new Phone("91234567")), List.of())
                .test(LIAM));

        // tag only, different case
        assertTrue(new PersonMatchesSearchPredicate(List.of(), List.of(), List.of(new Tag("SEC4"))).test(LIAM));
    }

    @Test
    public void test_anyValueWithinFieldMatches_returnsTrue() {
        // one of two phones matches
        assertTrue(new PersonMatchesSearchPredicate(List.of(),
                List.of(new Phone("98765432"), new Phone("91234567")), List.of()).test(LIAM));

        // one of two tags matches
        assertTrue(new PersonMatchesSearchPredicate(List.of(), List.of(),
                List.of(new Tag("sec3"), new Tag("physics"))).test(LIAM));
    }

    @Test
    public void test_everyFieldMustMatch() {
        // name and tag both match -> returns true
        assertTrue(new PersonMatchesSearchPredicate(List.of("Liam"), List.of(), List.of(new Tag("sec4")))
                .test(LIAM));

        // name matches, tag does not -> returns false
        assertFalse(new PersonMatchesSearchPredicate(List.of("Liam"), List.of(), List.of(new Tag("sec3")))
                .test(LIAM));

        // tag matches, phone does not -> returns false
        assertFalse(new PersonMatchesSearchPredicate(List.of(), List.of(new Phone("98765432")),
                List.of(new Tag("sec4"))).test(LIAM));

        // all three match -> returns true
        assertTrue(new PersonMatchesSearchPredicate(List.of("obrien"), List.of(new Phone("91234567")),
                List.of(new Tag("physics"))).test(LIAM));
    }

    @Test
    public void test_noFieldMatches_returnsFalse() {
        // non-matching name
        assertFalse(new PersonMatchesSearchPredicate(List.of("Nur"), List.of(), List.of()).test(LIAM));

        // non-matching phone
        assertFalse(new PersonMatchesSearchPredicate(List.of(), List.of(new Phone("98765432")), List.of())
                .test(LIAM));

        // person without tags
        Person untagged = new PersonBuilder().withName("Liam O'Brien").withTags().build();
        assertFalse(new PersonMatchesSearchPredicate(List.of(), List.of(), List.of(new Tag("sec4")))
                .test(untagged));
    }

    @Test
    public void toStringMethod() {
        List<String> nameKeywords = List.of("Liam");
        List<Phone> phones = List.of(new Phone("91234567"));
        List<Tag> tags = List.of(new Tag("sec4"));
        PersonMatchesSearchPredicate predicate = new PersonMatchesSearchPredicate(nameKeywords, phones, tags);

        String expected = PersonMatchesSearchPredicate.class.getCanonicalName() + "{nameKeywords=" + nameKeywords
                + ", phones=" + phones + ", tags=" + tags + "}";
        assertEquals(expected, predicate.toString());
    }
}

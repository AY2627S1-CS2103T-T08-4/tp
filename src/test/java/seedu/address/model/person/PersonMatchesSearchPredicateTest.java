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
                List.of(new Phone("91234567")), List.of(new Tag("sec4")));

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        assertTrue(firstPredicate.equals(new PersonMatchesSearchPredicate(
                List.of(new Phone("91234567")), List.of(new Tag("sec4")))));

        // tags differing only in case -> returns true
        assertTrue(firstPredicate.equals(new PersonMatchesSearchPredicate(
                List.of(new Phone("91234567")), List.of(new Tag("SEC4")))));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different phones -> returns false
        assertFalse(firstPredicate.equals(new PersonMatchesSearchPredicate(
                List.of(new Phone("98765432")), List.of(new Tag("sec4")))));

        // different tags -> returns false
        assertFalse(firstPredicate.equals(new PersonMatchesSearchPredicate(
                List.of(new Phone("91234567")), List.of(new Tag("sec3")))));
    }

    @Test
    public void test_singleFieldMatches_returnsTrue() {
        // phone only
        assertTrue(new PersonMatchesSearchPredicate(List.of(new Phone("91234567")), List.of()).test(LIAM));

        // tag only, different case
        assertTrue(new PersonMatchesSearchPredicate(List.of(), List.of(new Tag("SEC4"))).test(LIAM));
    }

    @Test
    public void test_anyPhoneMayMatch() {
        assertTrue(new PersonMatchesSearchPredicate(
                List.of(new Phone("91234567"), new Phone("91234567")), List.of()).test(LIAM));

        assertTrue(new PersonMatchesSearchPredicate(
                List.of(new Phone("98765432"), new Phone("91234567")), List.of()).test(LIAM));
    }

    @Test
    public void test_everyTagMustMatch() {
        assertTrue(new PersonMatchesSearchPredicate(List.of(),
                List.of(new Tag("sec4"), new Tag("physics"))).test(LIAM));

        assertTrue(new PersonMatchesSearchPredicate(List.of(), List.of(new Tag("physics"))).test(LIAM));

        assertFalse(new PersonMatchesSearchPredicate(List.of(),
                List.of(new Tag("sec3"), new Tag("physics"))).test(LIAM));
    }

    @Test
    public void test_everyFieldMustMatch() {
        // phone and tag both match -> returns true
        assertTrue(new PersonMatchesSearchPredicate(List.of(new Phone("91234567")),
                List.of(new Tag("physics"))).test(LIAM));

        // tag matches, phone does not -> returns false
        assertFalse(new PersonMatchesSearchPredicate(List.of(new Phone("98765432")),
                List.of(new Tag("sec4"))).test(LIAM));
    }

    @Test
    public void test_noFieldMatches_returnsFalse() {
        // non-matching phone
        assertFalse(new PersonMatchesSearchPredicate(List.of(new Phone("98765432")), List.of()).test(LIAM));

        // person without tags
        Person untagged = new PersonBuilder().withName("Liam O'Brien").withTags().build();
        assertFalse(new PersonMatchesSearchPredicate(List.of(), List.of(new Tag("sec4"))).test(untagged));
    }

    @Test
    public void toStringMethod() {
        List<Phone> phones = List.of(new Phone("91234567"));
        List<Tag> tags = List.of(new Tag("sec4"));
        PersonMatchesSearchPredicate predicate = new PersonMatchesSearchPredicate(phones, tags);

        String expected = PersonMatchesSearchPredicate.class.getCanonicalName() + "{phones=" + phones
                + ", tags=" + tags + "}";
        assertEquals(expected, predicate.toString());
    }
}

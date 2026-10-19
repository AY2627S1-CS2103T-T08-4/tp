package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PhoneMatchesKeywordsPredicateTest {

    private static final Phone MATCHING_PHONE = new Phone("91234567");
    private static final Phone OTHER_PHONE = new Phone("98765432");
    private static final Person PERSON = new PersonBuilder().withPhone(MATCHING_PHONE.value).build();

    @Test
    public void equals() {
        PhoneMatchesKeywordsPredicate firstPredicate = predicateWith(MATCHING_PHONE);
        PhoneMatchesKeywordsPredicate secondPredicate = predicateWith(OTHER_PHONE);

        assertTrue(firstPredicate.equals(firstPredicate));
        assertTrue(firstPredicate.equals(predicateWith(MATCHING_PHONE)));
        assertFalse(firstPredicate.equals(secondPredicate));
        assertFalse(firstPredicate.equals(1));
        assertFalse(firstPredicate.equals(null));
    }

    @Test
    public void test_allKeywordsMatch_returnsTrue() {
        assertTrue(predicateWith(MATCHING_PHONE).test(PERSON));
        assertTrue(predicateWith(MATCHING_PHONE, MATCHING_PHONE).test(PERSON));
    }

    @Test
    public void test_notAllKeywordsMatch_returnsFalse() {
        assertFalse(predicateWith(OTHER_PHONE).test(PERSON));
        assertFalse(predicateWith(MATCHING_PHONE, OTHER_PHONE).test(PERSON));
    }

    @Test
    public void test_noKeywords_returnsTrue() {
        assertTrue(predicateWith().test(PERSON));
    }

    @Test
    public void toStringMethod() {
        Set<Phone> keywords = Set.of(MATCHING_PHONE);
        PhoneMatchesKeywordsPredicate predicate = new PhoneMatchesKeywordsPredicate(keywords);
        String expected = PhoneMatchesKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";

        assertEquals(expected, predicate.toString());
    }

    private PhoneMatchesKeywordsPredicate predicateWith(Phone... phones) {
        return new PhoneMatchesKeywordsPredicate(List.of(phones));
    }
}

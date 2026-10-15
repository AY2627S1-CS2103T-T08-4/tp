package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class TagsContainKeywordsPredicateTest {

    private static final Person PERSON_WITH_TAGS = new PersonBuilder().withTags("Physics", "Secondary 4").build();

    @Test
    public void equals() {
        TagsContainKeywordsPredicate firstPredicate = predicateWith("Physics", "Secondary 4");
        TagsContainKeywordsPredicate secondPredicate = predicateWith("Chemistry");

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values in a different order and case -> returns true
        assertTrue(firstPredicate.equals(predicateWith("secondary 4", "PHYSICS")));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different tags -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_anyKeywordMatches_returnsTrue() {
        assertTrue(predicateWith("Physics").test(PERSON_WITH_TAGS));
        assertTrue(predicateWith("Chemistry", "Secondary 4").test(PERSON_WITH_TAGS));
        assertTrue(predicateWith("PHYSICS").test(PERSON_WITH_TAGS));
    }

    @Test
    public void test_noKeywordMatches_returnsFalse() {
        assertFalse(predicateWith().test(PERSON_WITH_TAGS));
        assertFalse(predicateWith("Chemistry").test(PERSON_WITH_TAGS));

        Person personWithoutTags = new PersonBuilder().withTags().build();
        assertFalse(predicateWith("Physics").test(personWithoutTags));
    }

    @Test
    public void toStringMethod() {
        Set<Tag> keywords = Set.of(new Tag("Physics"));
        TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(keywords);
        String expected = TagsContainKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";

        assertEquals(expected, predicate.toString());
    }

    private TagsContainKeywordsPredicate predicateWith(String... tagNames) {
        return new TagsContainKeywordsPredicate(List.of(tagNames).stream().map(Tag::new).toList());
    }
}

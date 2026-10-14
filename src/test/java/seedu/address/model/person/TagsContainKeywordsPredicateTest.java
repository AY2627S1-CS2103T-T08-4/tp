package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class TagsContainKeywordsPredicateTest {

    private static final Person PERSON = new PersonBuilder().withTags("Physics", "Secondary 4").build();

    @Test
    public void equals() {
        TagsContainKeywordsPredicate firstPredicate = predicateWith("Physics");
        TagsContainKeywordsPredicate secondPredicate = predicateWith("Secondary 4");

        assertTrue(firstPredicate.equals(firstPredicate));
        assertTrue(firstPredicate.equals(predicateWith("Physics")));
        assertFalse(firstPredicate.equals(secondPredicate));
        assertFalse(firstPredicate.equals(1));
        assertFalse(firstPredicate.equals(null));
    }

    @Test
    public void test_personHasAllTags_returnsTrue() {
        assertTrue(predicateWith("Physics").test(PERSON));
        assertTrue(predicateWith("Physics", "Secondary 4").test(PERSON));
        assertTrue(predicateWith("secondary 4", "PHYSICS").test(PERSON));
    }

    @Test
    public void test_personDoesNotHaveAllTags_returnsFalse() {
        assertFalse(predicateWith("Chemistry").test(PERSON));
        assertFalse(predicateWith("Physics", "Chemistry").test(PERSON));
    }

    @Test
    public void toStringMethod() {
        List<Tag> tags = List.of(new Tag("Physics"), new Tag("Secondary 4"));
        TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(tags);
        String expected = TagsContainKeywordsPredicate.class.getCanonicalName() + "{keywords=" + tags + "}";

        assertEquals(expected, predicate.toString());
    }

    private TagsContainKeywordsPredicate predicateWith(String... tagNames) {
        return new TagsContainKeywordsPredicate(List.of(tagNames).stream().map(Tag::new).toList());
    }
}

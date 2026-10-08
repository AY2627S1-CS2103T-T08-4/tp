package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;

/**
 * Contains automated unit tests verifying behaviors inside {@code SampleDataUtil}.
 */
public class SampleDataUtilTest {

    @Test
    public void getSamplePersons_validData_returnsSixProfiles() {
        assertNotNull(SampleDataUtil.getSamplePersons());
        assertEquals(6, SampleDataUtil.getSamplePersons().length);
    }

    @Test
    public void getSampleAddressBook_validInitialization_containsAllSampleRecords() {
        assertNotNull(SampleDataUtil.getSampleAddressBook());
        assertEquals(6, SampleDataUtil.getSampleAddressBook().getPersonList().size());
    }

    @Test
    public void getTagSet_duplicateInputStrings_excludesDuplicates() {
        Set<Tag> tagSet = SampleDataUtil.getTagSet("Math", "Math");
        assertNotNull(tagSet);
        assertEquals(1, tagSet.size());
        assertTrue(tagSet.contains(new Tag("Math")));
    }
}

package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with tuition-style sample data.
 */
public class SampleDataUtil {

    /**
     * Generates an array of tuition sample client profiles.
     * All sample clients match the parameter formatting requirements of Sieve.
     *
     * @return An array of sample {@code Person} objects representing tuition students.
     */
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Amirah Tan"), new Phone("91234567"), new Email("amirahtan@sample.com"),
                new Address("Blk 123 Yishun Ave 5 #04-12"),
                getTagSet("Math", "Sec4", "Urgent")),
            new Person(new Name("Brandon Sim"), new Phone("92345678"), new Email("brandonsim@sample.com"),
                new Address("10 Queenstown Rd"),
                getTagSet("Physics", "Sec4")),
            new Person(new Name("Charlotte Oliveiro"), new Phone("98765432"), new Email("chloe.ng@sample.com"),
                new Address("Blk 456 Bishan St 21 #11-22"),
                getTagSet("English", "Sec2")),
            new Person(new Name("Devi Ramasamy"), new Phone("81234567"), new Email("devi.r@sample.com"),
                new Address("Blk 789 Pasir Ris Drive 3 #02-05"),
                getTagSet("Chemistry", "JC1", "Paid")),
            new Person(new Name("Evelyn Rodrigues"), new Phone("87654321"), new Email("evelyn.r@sample.com"),
                new Address("Blk 321 Ang Mo Kio Ave 4 #08-99"),
                getTagSet("Biology", "JC2", "Weekday")),
            new Person(new Name("Farhan Ali"), new Phone("91122334"), new Email("farhanali@sample.com"),
                new Address("Blk 45 Aljunied Street 85 #11-31"),
                getTagSet("Math", "Sec1"))
        };
    }

    /**
     * Generates a sample {@code ReadOnlyAddressBook} pre-populated with tuition client records.
     * Used on initial application startup when no persistent storage data file is found.
     *
     * @return A read-only representation of a populated sample address book database.
     */
    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     * Duplicates within the input arguments are excluded.
     *
     * @param strings A sequence of string elements to parse into tag domains.
     * @return A unique set containing the mapped {@code Tag} representations.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays
            .stream(strings)
            .map(Tag::new)
            .collect(Collectors.toSet());
    }

}

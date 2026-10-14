package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void formatClientCount() {
        assertEquals("0 client", Messages.formatClientCount(0));
        assertEquals("1 client", Messages.formatClientCount(1));
        assertEquals("2 clients", Messages.formatClientCount(2));
    }

    @Test
    public void getIndexOutOfBoundsMessage() {
        String prefix = "Error: The index provided is out of bounds! Current list only contains ";
        assertEquals(prefix + "0 client.", Messages.getIndexOutOfBoundsMessage(0));
        assertEquals(prefix + "1 client.", Messages.getIndexOutOfBoundsMessage(1));
        assertEquals(prefix + "2 clients.", Messages.getIndexOutOfBoundsMessage(2));
    }

    @Test
    public void getPersonsListedOverviewMessage() {
        assertEquals("0 client found matching your search term(s).", Messages.getPersonsListedOverviewMessage(0));
        assertEquals("1 client found matching your search term(s).", Messages.getPersonsListedOverviewMessage(1));
        assertEquals("2 clients found matching your search term(s).", Messages.getPersonsListedOverviewMessage(2));
    }

    @Test
    public void getClientAlreadyExistsMessage() {
        Person existing = new PersonBuilder().withName("John Tan").withPhone("91234567").build();
        assertEquals("This client already exists in Sieve: John Tan with the phone number 91234567. "
                        + "They may have been added before.",
                Messages.getClientAlreadyExistsMessage(existing));
    }

    @Test
    public void getNameAlreadyUsedMessage() {
        Person existing = new PersonBuilder().withName("John Tan").withPhone("91234567").build();
        assertEquals("A client named John Tan already exists in Sieve, with the phone number 91234567. "
                        + "Check that the name and phone numbers are correct. "
                        + "If this is a different client, add a detail to tell them apart, e.g. \"John Tan (NHHS)\".",
                Messages.getNameAlreadyUsedMessage(existing));
    }
}

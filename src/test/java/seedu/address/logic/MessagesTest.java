package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MessagesTest {

    @Test
    public void formatClientCount() {
        assertEquals("0 clients", Messages.formatClientCount(0));
        assertEquals("1 client", Messages.formatClientCount(1));
        assertEquals("2 clients", Messages.formatClientCount(2));
    }

    @Test
    public void getIndexOutOfBoundsMessage() {
        String prefix = "Error: The index provided is out of bounds! Current list only contains ";
        assertEquals(prefix + "0 clients.", Messages.getIndexOutOfBoundsMessage(0));
        assertEquals(prefix + "1 client.", Messages.getIndexOutOfBoundsMessage(1));
        assertEquals(prefix + "2 clients.", Messages.getIndexOutOfBoundsMessage(2));
    }

    @Test
    public void getPersonsListedOverviewMessage() {
        assertEquals("0 clients found matching your search term(s).", Messages.getPersonsListedOverviewMessage(0));
        assertEquals("1 client found matching your search term(s).", Messages.getPersonsListedOverviewMessage(1));
        assertEquals("2 clients found matching your search term(s).", Messages.getPersonsListedOverviewMessage(2));
    }
}

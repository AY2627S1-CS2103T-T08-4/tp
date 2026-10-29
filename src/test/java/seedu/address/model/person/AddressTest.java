package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.person.Address.MAX_LENGTH;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AddressTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Address(null));
    }

    @Test
    public void constructor_invalidAddress_throwsIllegalArgumentException() {
        String invalidAddress = "";
        assertThrows(IllegalArgumentException.class, () -> new Address(invalidAddress));
    }

    @Test
    public void normalizeAddress_extraWhitespace_collapsed() {
        // normalizeAddress only calls StringUtil.normalizeWhitespace, whose partitions StringUtilTest covers
        assertEquals("Blk 123 Yishun Ave", Address.normalizeAddress("  Blk 123\tYishun \n Ave "));
    }

    @Test
    public void isValidAddress() {
        // null address
        assertThrows(NullPointerException.class, () -> Address.isValidAddress(null));

        // invalid addresses
        assertFalse(Address.isValidAddress("")); // empty string
        assertFalse(Address.isValidAddress(" ")); // spaces only
        assertFalse(Address.isValidAddress(" Blk 1")); // starts with a space
        assertFalse(Address.isValidAddress("-")); // no letter or digit
        assertFalse(Address.isValidAddress("\u00A0")); // non-breaking space only, which looks blank
        assertFalse(Address.isValidAddress("\u200B")); // zero-width space only, which looks blank
        assertFalse(Address.isValidAddress("a".repeat(MAX_LENGTH + 1))); // one character over the limit

        // valid addresses
        assertTrue(Address.isValidAddress("Blk 456, Den Road, #01-355"));
        assertTrue(Address.isValidAddress("1")); // one character
        assertTrue(Address.isValidAddress("\u00A0Blk 1")); // leading non-breaking space, which is kept
        assertTrue(Address.isValidAddress("\u65B0\u52A0\u5761")); // letters of another language (Chinese)
        assertTrue(Address.isValidAddress("123")); // digits only
        assertTrue(Address.isValidAddress("#04-12")); // symbols and digits only
        assertTrue(Address.isValidAddress("Online")); // the placeholder that MESSAGE_CONSTRAINTS suggests
        assertTrue(Address.isValidAddress("Leng Inc; 1234 Market St; San Francisco CA 2349879; USA")); // long address
        assertTrue(Address.isValidAddress("a".repeat(MAX_LENGTH))); // exactly at the limit
    }

    @Test
    public void equals() {
        Address address = new Address("Valid Address");

        // same values -> returns true
        assertTrue(address.equals(new Address("Valid Address")));

        // same object -> returns true
        assertTrue(address.equals(address));

        // null -> returns false
        assertFalse(address.equals(null));

        // different types -> returns false
        assertFalse(address.equals(5.0f));

        // different values -> returns false
        assertFalse(address.equals(new Address("Other Valid Address")));
    }
}

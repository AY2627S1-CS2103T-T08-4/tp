package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class FeeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Fee(null));
    }

    @Test
    public void constructor_invalidFee_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Fee("abc"));
        assertThrows(IllegalArgumentException.class, () -> new Fee("-5"));
    }

    @Test
    public void constructor_validFee_storedWithTwoDecimalPlaces() {
        assertEquals("250.00", new Fee("250").value);
        assertEquals("250.50", new Fee("250.5").value);
        assertEquals("250.55", new Fee("250.55").value);
        assertEquals("0.00", new Fee("0").value);
        assertEquals("", new Fee("").value);
    }

    @Test
    public void normalizeFee() {
        // null fee
        assertThrows(NullPointerException.class, () -> Fee.normalizeFee(null));

        // whitespace trimmed
        assertEquals("250", Fee.normalizeFee("  250 \t"));
        assertEquals("", Fee.normalizeFee("   ")); // whitespace only

        // already normalized -> unchanged
        assertEquals("250.50", Fee.normalizeFee("250.50"));
    }

    @Test
    public void isValidFee() {
        // null fee
        assertThrows(NullPointerException.class, () -> Fee.isValidFee(null));

        // valid fees
        assertTrue(Fee.isValidFee("")); // empty fee means no fee
        assertTrue(Fee.isValidFee("0"));
        assertTrue(Fee.isValidFee("250"));
        assertTrue(Fee.isValidFee("250.5"));
        assertTrue(Fee.isValidFee("250.50"));
        assertTrue(Fee.isValidFee("1234567.99")); // at the limit

        // invalid fees
        assertFalse(Fee.isValidFee(" ")); // spaces only
        assertFalse(Fee.isValidFee("abc"));
        assertFalse(Fee.isValidFee("-5")); // negative
        assertFalse(Fee.isValidFee("250.555")); // more than 2 decimal places
        assertFalse(Fee.isValidFee("12345678")); // more than 7 digits
        assertFalse(Fee.isValidFee(".5"));
        assertFalse(Fee.isValidFee("5."));
        assertFalse(Fee.isValidFee("2 50"));
        assertFalse(Fee.isValidFee("1e3"));
        assertFalse(Fee.isValidFee("$250"));
    }

    @Test
    public void isEmpty() {
        assertTrue(Fee.EMPTY.isEmpty());
        assertTrue(new Fee("").isEmpty());
        assertFalse(new Fee("0").isEmpty()); // a fee of zero is still a fee
    }

    @Test
    public void equals() {
        Fee fee = new Fee("250");

        // same values -> returns true
        assertTrue(fee.equals(new Fee("250")));
        assertTrue(fee.equals(new Fee("250.00"))); // same canonical value

        // same object -> returns true
        assertTrue(fee.equals(fee));

        // null -> returns false
        assertFalse(fee.equals(null));

        // different types -> returns false
        assertFalse(fee.equals(5.0f));

        // different values -> returns false
        assertFalse(fee.equals(new Fee("250.50")));
        assertFalse(fee.equals(Fee.EMPTY));
    }

    @Test
    public void hashCodeMethod() {
        Fee fee = new Fee("250");

        // same values -> same hash code
        assertEquals(fee.hashCode(), new Fee("250.00").hashCode());

        // different values -> different hash code
        assertNotEquals(fee.hashCode(), Fee.EMPTY.hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("250.00", new Fee("250").toString());
        assertEquals("", Fee.EMPTY.toString());
    }
}

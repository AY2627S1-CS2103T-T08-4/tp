package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("9123456")); // 7 digits
        assertFalse(Phone.isValidPhone("912345678")); // 9 digits
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("9312 1534")); // formatting characters are not stripped by isValidPhone
        assertFalse(Phone.isValidPhone("+6591234567")); // country code

        // valid phone numbers
        assertTrue(Phone.isValidPhone("93121534")); // exactly 8 digits
    }

    @Test
    public void stripPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.stripPhone(null));

        // formatting characters removed
        assertEquals("91234567", Phone.stripPhone("9123 4567")); // space
        assertEquals("91234567", Phone.stripPhone("9123-4567")); // hyphen
        assertEquals("91234567", Phone.stripPhone("(9123)-4567")); // parentheses and hyphen
        assertEquals("91234567", Phone.stripPhone("\t91234567 ")); // leading and trailing whitespace
        assertEquals("", Phone.stripPhone("- ( )")); // formatting characters only

        // other characters kept
        assertEquals("+65811878", Phone.stripPhone("+65 811878")); // country code
        assertEquals("9123.4567", Phone.stripPhone("9123.4567")); // period
        assertEquals("9123a4567", Phone.stripPhone("9123a4567")); // letter
    }

    @Test
    public void equals() {
        Phone phone = new Phone("99999999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("99999999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("99999995")));
    }
}

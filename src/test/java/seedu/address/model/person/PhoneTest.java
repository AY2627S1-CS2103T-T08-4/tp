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
        assertFalse(Phone.isValidPhone("00000000")); // not a Singapore number
        assertFalse(Phone.isValidPhone("21234567")); // starts with 2, just below 3
        assertFalse(Phone.isValidPhone("41234567")); // starts with 4, between 3 and 6
        assertFalse(Phone.isValidPhone("51234567")); // starts with 5, just below 6
        assertFalse(Phone.isValidPhone("71234567")); // starts with 7, between 6 and 8

        // valid phone numbers: 8 digits starting with 3, 6, 8 or 9
        assertTrue(Phone.isValidPhone("31234567")); // VoIP
        assertTrue(Phone.isValidPhone("61234567")); // landline
        assertTrue(Phone.isValidPhone("81234567")); // mobile
        assertTrue(Phone.isValidPhone("93121534")); // mobile
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
    public void hasCountryCode() {
        // with a country code
        assertTrue(Phone.hasCountryCode("+6591234567")); // Singapore
        assertTrue(Phone.hasCountryCode("+14155552671")); // another country
        assertTrue(Phone.hasCountryCode("+")); // '+' alone
        assertTrue(Phone.hasCountryCode("6591234567")); // Singapore without '+'
        assertTrue(Phone.hasCountryCode("006591234567")); // Singapore with '00' in place of '+'

        // without a country code
        assertFalse(Phone.hasCountryCode("91234567")); // Singapore number
        assertFalse(Phone.hasCountryCode("65123456")); // landline starting with 65
        assertFalse(Phone.hasCountryCode("659123456")); // 65 followed by 7 digits
        assertFalse(Phone.hasCountryCode("65912345678")); // 65 followed by 9 digits
        assertFalse(Phone.hasCountryCode("9123+6545")); // '+' not at the start
        assertFalse(Phone.hasCountryCode("00912345")); // 00 followed by 6 digits, an invalid 8-digit number
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

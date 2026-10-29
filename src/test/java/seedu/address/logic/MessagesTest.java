package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    private final PersonBuilder johnTan = new PersonBuilder().withName("John Tan").withPhone("91234567")
            .withEmail("john@example.com").withAddress("Blk 1").withTags("sec4");

    @Test
    public void formatClientCount_zeroOneAndTwo_pluralExceptForOne() {
        assertEquals("0 clients", Messages.formatClientCount(0));
        assertEquals("1 client", Messages.formatClientCount(1));
        assertEquals("2 clients", Messages.formatClientCount(2));
    }

    @Test
    public void format_personWithFee_includesFee() {
        assertEquals("John Tan; Phone: 91234567; Email: john@example.com; Address: Blk 1; "
                + "Fee: 80.00; Tags: [sec4]", Messages.format(johnTan.withFee("80").build()));
    }

    @Test
    public void format_personWithZeroFee_includesFee() {
        assertEquals("John Tan; Phone: 91234567; Email: john@example.com; Address: Blk 1; "
                + "Fee: 0.00; Tags: [sec4]", Messages.format(johnTan.withFee("0").build()));
    }

    @Test
    public void format_personWithDueDate_includesDueDate() {
        assertEquals("John Tan; Phone: 91234567; Email: john@example.com; Address: Blk 1; "
                + "Due: 01-12-2026; Tags: [sec4]",
                Messages.format(johnTan.withDueDate("01-12-2026").build()));
    }

    @Test
    public void format_personWithoutNote_omitsNote() {
        assertEquals("John Tan; Phone: 91234567; Email: john@example.com; Address: Blk 1; Tags: [sec4]",
                Messages.format(johnTan.withNote("").build()));
    }

    @Test
    public void format_personWithoutTags_omitsTags() {
        assertEquals("John Tan; Phone: 91234567; Email: john@example.com; Address: Blk 1",
                Messages.format(johnTan.withTags().build()));
    }

    @Test
    public void format_personWithNote_includesNote() {
        assertEquals("John Tan; Phone: 91234567; Email: john@example.com; Address: Blk 1; "
                + "Note: Weak at algebra; Tags: [sec4]", Messages.format(johnTan.withNote("Weak at algebra").build()));
    }

    @Test
    public void format_personWithNoteFeeAndDueDate_includesAllInOrder() {
        assertEquals("John Tan; Phone: 91234567; Email: john@example.com; Address: Blk 1; "
                + "Note: Weak at algebra; Fee: 80.00; Due: 01-12-2026; Tags: [sec4]",
                Messages.format(johnTan.withNote("Weak at algebra").withFee("80").withDueDate("01-12-2026").build()));
    }

    @Test
    public void getIndexOutOfBoundsMessage_oneClient_includesListSize() {
        // The count comes from formatClientCount, which is tested with 0, 1 and 2 above
        assertEquals("Error: The index provided is out of bounds! Current list only contains 1 client.",
                Messages.getIndexOutOfBoundsMessage(1));
    }

    @Test
    public void getPersonsListedOverviewMessage_oneClient_includesCount() {
        assertEquals("1 client found matching your search term(s).", Messages.getPersonsListedOverviewMessage(1));
    }

    @Test
    public void getErrorMessageForUnknownPrefix_prefixLikeWord_namesItAndHowToFix() {
        assertEquals("Unknown prefix z/. Check it for a typo, "
                        + "or type help to see the prefixes each command accepts.",
                Messages.getErrorMessageForUnknownPrefix("z/"));
    }

    @Test
    public void getClientAlreadyExistsMessage_existingClient_namesClientAndPhone() {
        assertEquals("This client already exists in Sieve: John Tan with the phone number 91234567. "
                        + "They may have been added before.",
                Messages.getClientAlreadyExistsMessage(johnTan.build()));
    }

    @Test
    public void getNameAlreadyUsedMessage_existingClient_namesClientAndSuggestsDetail() {
        assertEquals("A client named John Tan already exists in Sieve, with the phone number 91234567. "
                        + "Check that the name and phone numbers are correct. "
                        + "If this is a different client, add a detail to tell them apart, e.g. \"John Tan (NHHS)\".",
                Messages.getNameAlreadyUsedMessage(johnTan.build()));
    }
}

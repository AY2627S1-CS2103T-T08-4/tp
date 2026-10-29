package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.DueDate;
import seedu.address.model.person.Email;
import seedu.address.model.person.Fee;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_FEE = "-5";
    private static final String INVALID_DUE_DATE = "30-02-2026";
    private static final String INVALID_TAG = "#friend";

    private static final String OUT_OF_RANGE_DUE_DATE = "01-01-0000";
    private static final String PAST_DUE_DATE = "01-01-2020";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_NOTE = BENSON.getNote().toString();
    private static final String VALID_FEE = BENSON.getFee().toString();
    private static final String VALID_DUE_DATE = BENSON.getDueDate().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_dueDateYearOutOfRange_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
            VALID_NOTE, VALID_FEE, OUT_OF_RANGE_DUE_DATE, VALID_TAGS);
        assertThrows(IllegalValueException.class, DueDate.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_pastDueDate_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
            VALID_NOTE, VALID_FEE, PAST_DUE_DATE, VALID_TAGS);
        assertEquals(new DueDate(PAST_DUE_DATE), person.toModelType().getDueDate());
    }

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_NOTE,
                    VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nameWithExtraWhitespace_returnsPersonWithCollapsedName() throws Exception {
        // a name hand-edited into the data file with extra whitespace should still load
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(" Liam  O'Brien ", VALID_PHONE, VALID_EMAIL,
                    VALID_ADDRESS, VALID_NOTE, VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        assertEquals(new Name("Liam O'Brien"), person.toModelType().getName());
    }

    @Test
    public void toModelType_tooLongName_throwsIllegalValueException() {
        String tooLongName = "a".repeat(Name.MAX_LENGTH + 1);
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(tooLongName, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_NOTE,
                    VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PHONE, VALID_EMAIL,
            VALID_ADDRESS, VALID_NOTE, VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_NOTE,
                    VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_formattedPhone_returnsPersonWithStrippedPhone() throws Exception {
        // a phone hand-edited into the data file with formatting characters should still load
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, "(9876) 5432", VALID_EMAIL, VALID_ADDRESS, VALID_NOTE,
                    VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        assertEquals(new Phone("98765432"), person.toModelType().getPhone());
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL,
            VALID_ADDRESS, VALID_NOTE, VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS, VALID_NOTE,
                    VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null,
            VALID_ADDRESS, VALID_NOTE, VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS, VALID_NOTE,
                    VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_addressWithExtraWhitespace_returnsPersonWithCollapsedAddress() throws Exception {
        // an address hand-edited into the data file with extra whitespace should still load
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, " Blk 30  Geylang\nStreet 29 ",
                    VALID_NOTE, VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        assertEquals(new Address("Blk 30 Geylang Street 29"), person.toModelType().getAddress());
    }

    @Test
    public void toModelType_tooLongAddress_throwsIllegalValueException() {
        String tooLongAddress = "a".repeat(Address.MAX_LENGTH + 1);
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, tooLongAddress, VALID_NOTE,
                    VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
            null, VALID_NOTE, VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_personWithNote_returnsPersonWithNote() throws Exception {
        Person personWithNote = new PersonBuilder(BENSON).withNote("Weak at algebra").build();
        assertEquals(personWithNote, new JsonAdaptedPerson(personWithNote).toModelType());
    }

    @Test
    public void toModelType_nullNote_returnsPersonWithEmptyNote() throws Exception {
        // data files saved before notes existed have no note field, and must still load
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null,
                    VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        assertEquals(Note.EMPTY, person.toModelType().getNote());
    }

    @Test
    public void toModelType_noteWithExtraWhitespace_returnsPersonWithCollapsedNote() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                "  Weak  at\nalgebra ", VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        assertEquals(new Note("Weak at algebra"), person.toModelType().getNote());
    }

    @Test
    public void toModelType_tooLongNote_throwsIllegalValueException() {
        String tooLongNote = "a".repeat(Note.MAX_LENGTH + 1);
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, tooLongNote,
                    VALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        assertThrows(IllegalValueException.class, Note.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_personWithFeeAndDueDate_returnsPersonWithFeeAndDueDate() throws Exception {
        Person personWithBilling = new Person(BENSON.getName(), BENSON.getPhone(), BENSON.getEmail(),
            BENSON.getAddress(), BENSON.getNote(), new Fee("250"), new DueDate("01-11-2026"), BENSON.getTags());
        assertEquals(personWithBilling, new JsonAdaptedPerson(personWithBilling).toModelType());
    }

    @Test
    public void toModelType_nullFeeAndDueDate_returnsPersonWithEmptyFeeAndDueDate() throws Exception {
        // data files saved before billing existed have no fee or due date fields, and must still load
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
            VALID_NOTE, null, null, VALID_TAGS);
        Person modelPerson = person.toModelType();
        assertEquals(Fee.EMPTY, modelPerson.getFee());
        assertEquals(DueDate.EMPTY, modelPerson.getDueDate());
    }

    @Test
    public void toModelType_feeAndDueDateWithExtraWhitespace_returnsPersonWithTrimmedValues() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
            VALID_NOTE, " 250 ", " 01-11-2026 ", VALID_TAGS);
        Person modelPerson = person.toModelType();
        assertEquals(new Fee("250"), modelPerson.getFee());
        assertEquals(new DueDate("01-11-2026"), modelPerson.getDueDate());
    }

    @Test
    public void toModelType_invalidFee_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
            VALID_NOTE, INVALID_FEE, VALID_DUE_DATE, VALID_TAGS);
        assertThrows(IllegalValueException.class, Fee.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_invalidDueDate_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
            VALID_NOTE, VALID_FEE, INVALID_DUE_DATE, VALID_TAGS);
        assertThrows(IllegalValueException.class, DueDate.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_NOTE,
                    VALID_FEE, VALID_DUE_DATE, invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_nullTag_throwsIllegalValueException() {
        List<JsonAdaptedTag> tagsWithNull = new ArrayList<>(VALID_TAGS);
        tagsWithNull.add(null);
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_NOTE, VALID_FEE, VALID_DUE_DATE, tagsWithNull);

        assertThrows(IllegalValueException.class, JsonAdaptedPerson.MESSAGE_NULL_TAG, person::toModelType);
    }

    @Test
    public void toModelType_tagWithRepeatedWhitespace_returnsPersonWithNormalizedTag() throws Exception {
        List<JsonAdaptedTag> tagsWithRepeatedWhitespace = List.of(new JsonAdaptedTag(" Sec  4 "));
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_NOTE, VALID_FEE, VALID_DUE_DATE, tagsWithRepeatedWhitespace);

        assertEquals(Set.of(new Tag("Sec 4")), person.toModelType().getTags());
    }

    @Test
    public void getDisplayName_nameAtMaxDisplayLength_unchanged() {
        String name = "A".repeat(JsonAdaptedPerson.MAX_DISPLAY_NAME_LENGTH);
        assertEquals(name, createPersonNamed(name).getDisplayName(0));
    }

    @Test
    public void getDisplayName_longerName_shortenedWithEllipsis() {
        String name = "A".repeat(JsonAdaptedPerson.MAX_DISPLAY_NAME_LENGTH + 1);
        String expectedName = "A".repeat(JsonAdaptedPerson.MAX_DISPLAY_NAME_LENGTH) + "…";
        assertEquals(expectedName, createPersonNamed(name).getDisplayName(0));
    }

    @Test
    public void getDisplayName_blankName_returnsPositionInFile() {
        assertEquals("#3", createPersonNamed(" ").getDisplayName(2));
    }

    private JsonAdaptedPerson createPersonNamed(String name) {
        return new JsonAdaptedPerson(name, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_NOTE, VALID_FEE,
                VALID_DUE_DATE, VALID_TAGS);
    }
}

package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Client's %s field is missing!";
    public static final String MESSAGE_NULL_TAG = "Client's tag list contains null tag(s).";
    static final int MAX_DISPLAY_NAME_LENGTH = 30;

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String note;
    private final String fee;
    private final String dueDate;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("note") String note, @JsonProperty("fee") String fee,
            @JsonProperty("dueDate") String dueDate, @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.note = note;
        this.fee = fee;
        this.dueDate = dueDate;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        note = source.getNote().value;
        fee = source.getFee().value;
        dueDate = source.getDueDate().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            if (tag == null) {
                throw new IllegalValueException(MESSAGE_NULL_TAG);
            }
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        String normalizedName = Name.normalizeName(name);
        if (!Name.isValidName(normalizedName)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(normalizedName);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        String strippedPhone = Phone.stripPhone(phone);
        if (!Phone.isValidPhone(strippedPhone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(strippedPhone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        String normalizedAddress = Address.normalizeAddress(address);
        if (!Address.isValidAddress(normalizedAddress)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(normalizedAddress);

        // Data files saved before notes existed have no note, which is treated as an empty note
        String normalizedNote = Note.normalizeNote(note == null ? "" : note);
        if (!Note.isValidNote(normalizedNote)) {
            throw new IllegalValueException(Note.MESSAGE_CONSTRAINTS);
        }
        final Note modelNote = new Note(normalizedNote);

        String normalizedFee = Fee.normalizeFee(fee == null ? "" : fee);
        if (!Fee.isValidFee(normalizedFee)) {
            throw new IllegalValueException(Fee.MESSAGE_CONSTRAINTS);
        }
        final Fee modelFee = new Fee(normalizedFee);

        String normalizedDueDate = DueDate.normalizeDueDate(dueDate == null ? "" : dueDate);
        if (!DueDate.isValidDueDate(normalizedDueDate)) {
            throw new IllegalValueException(DueDate.MESSAGE_CONSTRAINTS);
        }
        final DueDate modelDueDate = new DueDate(normalizedDueDate);

        final Set<Tag> modelTags = new HashSet<>(personTags);
        return new Person(modelName, modelPhone, modelEmail, modelAddress, modelNote, modelFee, modelDueDate,
            modelTags);
    }

    String getDisplayName(int index) {
        if (name == null || name.isBlank()) {
            return "#" + (index + 1);
        }
        // A hand-edited name can be thousands of characters long, which would make the warning taller than the screen
        return name.length() > MAX_DISPLAY_NAME_LENGTH ? name.substring(0, MAX_DISPLAY_NAME_LENGTH) + "…" : name;
    }

}

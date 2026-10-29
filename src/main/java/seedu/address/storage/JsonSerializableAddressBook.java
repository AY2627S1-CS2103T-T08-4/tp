package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Client list contains duplicate client(s).";
    public static final String MESSAGE_NULL_PERSON = "Client list contains null client(s).";
    public static final String MESSAGE_NULL_PERSON_LIST = "Client list cannot be null.";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons)
            throws IllegalValueException {
        if (persons == null) {
            throw new IllegalValueException(MESSAGE_NULL_PERSON_LIST);
        }
        this.persons.addAll(persons);
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (int i = 0; i < persons.size(); i++) {
            JsonAdaptedPerson jsonAdaptedPerson = persons.get(i);
            if (jsonAdaptedPerson == null) {
                throw new IllegalValueException(MESSAGE_NULL_PERSON);
            }
            try {
                Person person = jsonAdaptedPerson.toModelType();
                if (addressBook.hasPerson(person)) {
                    throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
                }
                addressBook.addPerson(person);
            } catch (IllegalValueException e) {
                throw new IllegalValueException(
                        String.format("Client '%s': %s", jsonAdaptedPerson.getDisplayName(i), e.getMessage()), e);
            }
        }
        return addressBook;
    }

}

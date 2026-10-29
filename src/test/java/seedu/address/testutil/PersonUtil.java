package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DUE_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getAddCommand(Person person) {
        return AddCommand.COMMAND_WORD + " " + getPersonDetails(person);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + person.getName().fullName + " ");
        sb.append(PREFIX_PHONE + person.getPhone().value + " ");
        sb.append(PREFIX_EMAIL + person.getEmail().value + " ");
        sb.append(PREFIX_ADDRESS + person.getAddress().value + " ");
        if (!person
            .getFee()
            .isEmpty()) {
            sb.append(PREFIX_FEE + person.getFee().value + " ");
        }
        if (!person
            .getDueDate()
            .isEmpty()) {
            sb.append(PREFIX_DUE_DATE + person.getDueDate().value + " ");
        }
        person
            .getTags()
            .stream()
            .forEach(
                s -> sb.append(PREFIX_TAG + s.tagName + " ")
            );
        // nt/ must be the last parameter
        if (!person
            .getNote()
            .isEmpty()) {
            sb.append(PREFIX_NOTE + person.getNote().value + " ");
        }
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPersonDescriptor}'s details.
     */
    public static String getEditPersonDescriptorDetails(EditPersonDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor
            .getName()
            .ifPresent(name -> sb
                .append(PREFIX_NAME)
                .append(name.fullName)
                .append(" "));
        descriptor
            .getPhone()
            .ifPresent(phone -> sb
                .append(PREFIX_PHONE)
                .append(phone.value)
                .append(" "));
        descriptor
            .getEmail()
            .ifPresent(email -> sb
                .append(PREFIX_EMAIL)
                .append(email.value)
                .append(" "));
        descriptor
            .getAddress()
            .ifPresent(address -> sb
                .append(PREFIX_ADDRESS)
                .append(address.value)
                .append(" "));
        descriptor
            .getFee()
            .ifPresent(fee -> sb
                .append(PREFIX_FEE)
                .append(fee.value)
                .append(" "));
        descriptor
            .getDueDate()
            .ifPresent(dueDate -> sb
                .append(PREFIX_DUE_DATE)
                .append(dueDate.value)
                .append(" "));
        if (descriptor
            .getTags()
            .isPresent()) {
            Set<Tag> tags = descriptor
                .getTags()
                .get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG).append(" ");
            } else {
                tags.forEach(s -> sb
                    .append(PREFIX_TAG)
                    .append(s.tagName)
                    .append(" "));
            }
        }
        // nt/ must be the last parameter
        descriptor
            .getNote()
            .ifPresent(note -> sb
                .append(PREFIX_NOTE)
                .append(note.value)
                .append(" "));
        return sb.toString();
    }
}

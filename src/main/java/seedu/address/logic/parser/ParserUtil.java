package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_NOTE_NOT_LAST;
import static seedu.address.logic.parser.CliSyntax.PREFIXES_EXCEPT_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.DueDate;
import seedu.address.model.person.Email;
import seedu.address.model.person.Fee;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    /**
     * Checks that no other add or edit prefix follows {@code nt/} in {@code args}.
     * Everything after {@code nt/} belongs to the note, so a prefix there would cut the note short.
     *
     * @throws ParseException if a prefix in {@code PREFIXES_EXCEPT_NOTE} appears after {@code nt/}.
     */
    public static void verifyNoteIsLast(String args) throws ParseException {
        requireNonNull(args);
        if (ArgumentTokenizer.hasPrefixAfter(args, PREFIX_NOTE, PREFIXES_EXCEPT_NOTE)) {
            throw new ParseException(MESSAGE_NOTE_NOT_LAST);
        }
    }

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * A positive number too large for an {@code int} is parsed as {@code Integer.MAX_VALUE}, which is past the end
     * of any list, so that the command reports it as out of bounds.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            return Index.fromOneBased(Integer.parseInt(trimmedIndex));
        }
        // Only digits, not all zeros, yet not a valid int, so the number overflowed
        if (trimmedIndex.matches("\\d+") && !trimmedIndex.matches("0+")) {
            return Index.fromOneBased(Integer.MAX_VALUE);
        }
        throw new ParseException(MESSAGE_INVALID_INDEX);
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed, and consecutive whitespaces collapsed.
     * Apostrophe-like characters will be normalized, as in {@link Name#normalizeApostrophes(String)}.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String normalizedName = Name.normalizeName(name);
        if (!Name.isValidName(normalizedName)) {
            throw new ParseException(Name.getErrorMessage(normalizedName));
        }
        return new Name(normalizedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * All whitespace, hyphens and parentheses will be removed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String strippedPhone = Phone.stripPhone(phone);
        if (Phone.hasCountryCode(strippedPhone)) {
            throw new ParseException(Phone.MESSAGE_COUNTRY_CODE);
        }
        if (!Phone.isValidPhone(strippedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(strippedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     * Consecutive whitespaces will be collapsed into a single space.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String normalizedAddress = Address.normalizeAddress(address);
        if (!Address.isValidAddress(normalizedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(normalizedAddress);
    }

    /**
     * Parses a {@code String note} into a {@code Note}. An empty {@code note} gives an empty {@code Note}.
     * Leading and trailing whitespaces will be trimmed, and consecutive whitespaces collapsed.
     *
     * @throws ParseException if the given {@code note} is invalid.
     */
    public static Note parseNote(String note) throws ParseException {
        requireNonNull(note);
        String normalizedNote = Note.normalizeNote(note);
        if (!Note.isValidNote(normalizedNote)) {
            throw new ParseException(Note.getErrorMessage(normalizedNote));
        }
        return new Note(normalizedNote);
    }

    /**
     * Parses a {@code String fee} into a {@code Fee}. An empty {@code fee} gives an empty {@code Fee}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code fee} is invalid.
     */
    public static Fee parseFee(String fee) throws ParseException {
        requireNonNull(fee);
        String normalizedFee = Fee.normalizeFee(fee);
        if (!Fee.isValidFee(normalizedFee)) {
            throw new ParseException(Fee.MESSAGE_CONSTRAINTS);
        }
        return new Fee(normalizedFee);
    }

    /**
     * Parses a {@code String dueDate} into a {@code DueDate}. An empty {@code dueDate} gives an empty
     * {@code DueDate}. Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given dueDate is invalid or in the past.
     */
    public static DueDate parseDueDate(String dueDate) throws ParseException {
        requireNonNull(dueDate);
        String normalizedDueDate = DueDate.normalizeDueDate(dueDate);
        if (!DueDate.isValidDueDate(normalizedDueDate)) {
            throw new ParseException(DueDate.MESSAGE_CONSTRAINTS);
        }
        if (!DueDate.isTodayOrLater(normalizedDueDate)) {
            throw new ParseException(DueDate.MESSAGE_PAST_DATE);
        }
        return new DueDate(normalizedDueDate);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String normalizedTag = Tag.normalizeTagName(tag);
        if (!Tag.isValidTagName(normalizedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(normalizedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}

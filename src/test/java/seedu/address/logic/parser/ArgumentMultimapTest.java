package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;

public class ArgumentMultimapTest {

    private final ArgumentMultimap argMultimap = new ArgumentMultimap();

    @Test
    public void verifyNoUnknownPrefixesIn_noPrefixLikeWord_doesNotThrow() throws Exception {
        argMultimap.put(PREFIX_ADDRESS, "Blk 1 Ang Mo Kio"); // no slash
        argMultimap.put(PREFIX_ADDRESS, "Blk 12/3"); // slash after digits, a unit number
        argMultimap.put(PREFIX_ADDRESS, "Blk 10A/B"); // slash after a letter that is not the start of a word
        argMultimap.put(PREFIX_ADDRESS, "Blk 1 / 2"); // slash on its own
        argMultimap.verifyNoUnknownPrefixesIn(PREFIX_ADDRESS);
    }

    @Test
    public void verifyNoUnknownPrefixesIn_prefixNotGiven_doesNotThrow() throws Exception {
        argMultimap.verifyNoUnknownPrefixesIn(PREFIX_NAME, PREFIX_ADDRESS);
    }

    @Test
    public void verifyNoUnknownPrefixesIn_prefixNotChecked_doesNotThrow() throws Exception {
        // add and edit leave out the note, where "w/" (with) is normal
        argMultimap.put(PREFIX_NOTE, "Meet w/ parent");
        argMultimap.verifyNoUnknownPrefixesIn(PREFIX_ADDRESS);
    }

    @Test
    public void verifyNoUnknownPrefixesIn_prefixLikeWordAtStartOfValue_throwsParseException() {
        assertUnknownPrefix("z/", PREFIX_ADDRESS, "z/Blk 1");
    }

    @Test
    public void verifyNoUnknownPrefixesIn_prefixLikeWordAfterSpace_throwsParseException() {
        assertUnknownPrefix("z/", PREFIX_ADDRESS, "Blk 1 z/x");
    }

    @Test
    public void verifyNoUnknownPrefixesIn_severalLetters_throwsParseException() {
        // e.g. a mistyped nt/
        assertUnknownPrefix("nte/", PREFIX_ADDRESS, "Blk 1 nte/x");
    }

    @Test
    public void verifyNoUnknownPrefixesIn_capitalLetter_throwsParseException() {
        assertUnknownPrefix("Z/", PREFIX_ADDRESS, "Blk 1 Z/x");
    }

    @Test
    public void verifyNoUnknownPrefixesIn_inLaterValueOfPrefix_throwsParseException() {
        argMultimap.put(PREFIX_TAG, "math");
        assertUnknownPrefix("z/", PREFIX_TAG, "sec4 z/x");
    }

    @Test
    public void verifyNoUnknownPrefixesIn_inLaterCheckedPrefix_throwsParseException() {
        argMultimap.put(PREFIX_NAME, "Amy Bee");
        argMultimap.put(PREFIX_ADDRESS, "Blk 1 z/x");

        assertThrows(ParseException.class, Messages.getErrorMessageForUnknownPrefix("z/"), () ->
                argMultimap.verifyNoUnknownPrefixesIn(PREFIX_NAME, PREFIX_ADDRESS));
    }

    @Test
    public void verifyNoUnknownPrefixesIn_twoPrefixLikeWords_reportsFirst() {
        // Each word is rejected on its own above, so only the choice of word to report is tested here
        assertUnknownPrefix("x/", PREFIX_ADDRESS, "Blk 1 x/1 y/2");
    }

    /**
     * Gives {@code prefix} the value {@code value}, and asserts that checking {@code prefix} rejects
     * {@code unknownPrefix} as an unknown prefix.
     */
    private void assertUnknownPrefix(String unknownPrefix, Prefix prefix, String value) {
        argMultimap.put(prefix, value);
        assertThrows(ParseException.class, Messages.getErrorMessageForUnknownPrefix(unknownPrefix), () ->
                argMultimap.verifyNoUnknownPrefixesIn(prefix));
    }
}

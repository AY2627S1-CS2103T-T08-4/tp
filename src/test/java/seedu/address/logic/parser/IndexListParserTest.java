package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class IndexListParserTest {

    private static final String TEST_USAGE = "Test usage";

    @Test
    public void parse_singleIndex_returnsIndexList() throws Exception {
        assertEquals(
                List.of(INDEX_FIRST_PERSON),
                IndexListParser.parse("1", TEST_USAGE));
    }

    @Test
    public void parse_multipleIndexes_returnsIndexList() throws Exception {
        assertEquals(
                List.of(
                        INDEX_FIRST_PERSON,
                        INDEX_SECOND_PERSON),
                IndexListParser.parse("1, 2", TEST_USAGE));
    }

    @Test
    public void parse_duplicateIndexes_throwsParseException() {
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT,
                IndexListParser.MESSAGE_DUPLICATE_INDEX);

        assertThrows(ParseException.class, expectedMessage, () -> IndexListParser.parse("1, 1", TEST_USAGE));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT,
                TEST_USAGE);

        assertThrows(ParseException.class, expectedMessage, () -> IndexListParser.parse("1,,2", TEST_USAGE));
    }
}

package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class TagColorTest {

    private static final List<String> SAMPLE_TAG_NAMES =
            List.of("Math", "Sec4", "Urgent", "Physics", "English", "Sec2", "Chemistry", "JC1", "Paid");

    @Test
    public void getStyleClass_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> TagColor.getStyleClass(null));
    }

    @Test
    public void getStyleClass_sameName_sameColour() {
        assertEquals(TagColor.getStyleClass("Physics"), TagColor.getStyleClass("Physics"));
    }

    @Test
    public void getStyleClass_differentCase_sameColour() {
        assertEquals(TagColor.getStyleClass("Math"), TagColor.getStyleClass("math"));
        assertEquals(TagColor.getStyleClass("Sec4"), TagColor.getStyleClass("SEC4"));
    }

    @Test
    public void getStyleClass_anyName_returnsPaletteClass() {
        Set<String> paletteClasses = IntStream.range(0, TagColor.PALETTE_SIZE)
                .mapToObj(i -> "tag-color-" + i)
                .collect(Collectors.toSet());

        for (String tagName : SAMPLE_TAG_NAMES) {
            assertTrue(paletteClasses.contains(TagColor.getStyleClass(tagName)));
        }
        // "polygenelubricants" has a negative hash code, which % would turn into a negative index
        assertTrue(paletteClasses.contains(TagColor.getStyleClass("polygenelubricants")));
    }

    @Test
    public void getStyleClass_differentNames_useSeveralColours() {
        long coloursUsed = SAMPLE_TAG_NAMES.stream()
                .map(TagColor::getStyleClass)
                .distinct()
                .count();

        assertTrue(coloursUsed > 1);
    }
}

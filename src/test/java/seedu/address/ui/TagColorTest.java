package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class TagColorTest {

    private static final List<String> SAMPLE_TAG_NAMES =
            List.of("Math", "Sec4", "Urgent", "Physics", "English", "Sec2", "Chemistry", "JC1", "Paid");
    /** Every style class that {@code getStyleClass} can return, from tag-color-0 to tag-color-11. */
    private static final Set<String> PALETTE_CLASSES = IntStream.range(0, TagColor.PALETTE_SIZE)
            .mapToObj(i -> "tag-color-" + i)
            .collect(Collectors.toSet());
    private static final Pattern TAG_COLOR_CLASS = Pattern.compile("tag-color-\\d+");

    @Test
    public void getStyleClass_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> TagColor.getStyleClass(null));
    }

    @Test
    public void getStyleClass_sameName_sameColor() {
        assertEquals(TagColor.getStyleClass("Physics"), TagColor.getStyleClass("Physics"));
    }

    @Test
    public void getStyleClass_differentCase_sameColor() {
        assertEquals(TagColor.getStyleClass("Math"), TagColor.getStyleClass("math"));
        assertEquals(TagColor.getStyleClass("Sec4"), TagColor.getStyleClass("SEC4"));
    }

    @Test
    public void getStyleClass_anyName_returnsPaletteClass() {
        for (String tagName : SAMPLE_TAG_NAMES) {
            assertTrue(PALETTE_CLASSES.contains(TagColor.getStyleClass(tagName)));
        }
        // "polygenelubricants" has a negative hash code, which % would turn into a negative index
        assertTrue(PALETTE_CLASSES.contains(TagColor.getStyleClass("polygenelubricants")));
    }

    @Test
    public void getStyleClass_differentNames_useSeveralColors() {
        long colorsUsed = SAMPLE_TAG_NAMES.stream()
                .map(TagColor::getStyleClass)
                .distinct()
                .count();

        assertTrue(colorsUsed > 1);
    }

    @Test
    public void getStyleClass_eachTheme_hasRuleForEveryColor() throws IOException {
        for (String theme : List.of("/view/LightTheme.css", "/view/DarkTheme.css")) {
            Set<String> styledClasses = TAG_COLOR_CLASS.matcher(readStylesheet(theme)).results()
                    .map(MatchResult::group)
                    .collect(Collectors.toSet());

            // A missing rule would leave some tags uncolored, and a rule past the last color is never used
            assertEquals(PALETTE_CLASSES, styledClasses, theme);
        }
    }

    /**
     * Returns the text of the stylesheet at {@code path} on the classpath.
     */
    private static String readStylesheet(String path) throws IOException {
        try (InputStream stylesheet = TagColorTest.class.getResourceAsStream(path)) {
            return new String(stylesheet.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

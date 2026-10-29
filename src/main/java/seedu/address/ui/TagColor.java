package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Picks the color of a tag pill from a fixed palette, based on the tag's name.
 * The same tag name, in any letter case, always gets the same color.
 */
public class TagColor {

    /**
     * Number of colors in the palette.
     * LightTheme.css and DarkTheme.css each have one {@code .tag-color-N} rule per color.
     */
    public static final int PALETTE_SIZE = 12;

    private static final String STYLE_CLASS_PREFIX = "tag-color-";

    private TagColor() {}

    /**
     * Returns the style class that colors the tag called {@code tagName}, e.g. {@code tag-color-3}.
     */
    public static String getStyleClass(String tagName) {
        requireNonNull(tagName);
        // Tags are compared ignoring case, so Math and math must get the same color
        int hash = tagName.toLowerCase(Locale.ROOT).hashCode();
        // Unlike %, floorMod never returns a negative number when the hash is negative
        return STYLE_CLASS_PREFIX + Math.floorMod(hash, PALETTE_SIZE);
    }
}

package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Picks the colour of a tag pill from a fixed palette, based on the tag's name.
 * The same tag name, in any letter case, always gets the same colour.
 */
public class TagColor {

    /** Number of colours in the palette. LightTheme.css has one {@code .tag-color-N} rule for each. */
    public static final int PALETTE_SIZE = 12;

    private static final String STYLE_CLASS_PREFIX = "tag-color-";

    private TagColor() {}

    /**
     * Returns the style class that colours the tag called {@code tagName}, e.g. {@code tag-color-3}.
     */
    public static String getStyleClass(String tagName) {
        requireNonNull(tagName);
        // Tags are compared ignoring case, so Math and math must get the same colour
        int hash = tagName.toLowerCase(Locale.ROOT).hashCode();
        // Unlike %, floorMod never returns a negative number when the hash is negative
        return STYLE_CLASS_PREFIX + Math.floorMod(hash, PALETTE_SIZE);
    }
}

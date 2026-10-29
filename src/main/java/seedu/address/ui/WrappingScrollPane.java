package seedu.address.ui;

import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;

/**
 * A {@code ScrollPane} for wrapping text, which asks for enough height to show all of the wrapped lines.
 * Its maximum height still applies, after which the text scrolls.
 * Keep {@code fitToWidth} on, as {@link #getContentBias()} is only correct while the content fits the width.
 */
public class WrappingScrollPane extends ScrollPane {

    /**
     * Creates a {@code WrappingScrollPane} that fits its content to its width, so that the text wraps
     * instead of scrolling sideways.
     */
    public WrappingScrollPane() {
        // A horizontal content bias only holds when the content is as wide as the pane
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
    }

    /**
     * Tells the parent layout that the preferred height depends on the width, as it does for a wrapping
     * {@code Label}. Otherwise, the parent asks for the height before the text is wrapped,
     * and gives the text only as many lines as it has before wrapping.
     */
    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }
}

package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.VerticalDirection;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;

/**
 * A UI component that displays the result of a command execution.
 * A success message is shown with a tick, an error message with a cross,
 * and the message for a search that matched no clients with a gray "i".
 */
public class ResultDisplay extends UiPart<Region> {

    /** Matched by {@code .result-display:error} in the CSS while an error message is shown. */
    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    /** Matched by {@code .result-display:empty-search} in the CSS while an empty search message is shown. */
    private static final PseudoClass EMPTY_SEARCH_PSEUDO_CLASS = PseudoClass.getPseudoClass("empty-search");
    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private Region resultIcon;
    @FXML
    private ScrollPane resultScrollPane;
    @FXML
    private Label resultMessage;

    /**
     * Creates a {@code ResultDisplay} that stays hidden until the first command is entered.
     */
    public ResultDisplay() {
        super(FXML);
        setShown(getRoot(), false);
    }

    /**
     * Shows {@code feedbackToUser} as the result of a command that succeeded.
     */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        showMessage(feedbackToUser, false, false);
    }

    /**
     * Shows {@code message} as the result of a search that matched no clients.
     */
    public void setEmptySearchToUser(String message) {
        requireNonNull(message);
        showMessage(message, false, true);
    }

    /**
     * Shows {@code errorMessage} as the result of a command that failed.
     */
    public void setErrorToUser(String errorMessage) {
        requireNonNull(errorMessage);
        showMessage(errorMessage, true, false);
    }

    /**
     * Scrolls a message that is too long to show in full by one page in {@code direction}, stopping at its first or
     * last line. Does nothing if the whole message is already shown.
     */
    public void scrollPage(VerticalDirection direction) {
        requireNonNull(direction);
        double pageHeight = resultScrollPane.getViewportBounds().getHeight();
        double hiddenHeight = resultMessage.getHeight() - pageHeight;
        if (hiddenHeight <= 0) {
            return;
        }

        // The scroll position goes from vmin (first line at the top) to vmax (last line at the bottom),
        // which is a distance of hiddenHeight pixels
        double vmin = resultScrollPane.getVmin();
        double vmax = resultScrollPane.getVmax();
        double pageStep = pageHeight / hiddenHeight * (vmax - vmin);
        double step = direction == VerticalDirection.UP ? -pageStep : pageStep;
        resultScrollPane.setVvalue(Math.clamp(resultScrollPane.getVvalue() + step, vmin, vmax));
    }

    private void showMessage(String message, boolean isError, boolean isEmptySearch) {
        resultMessage.setText(message);
        resultScrollPane.setVvalue(0); // start each new message from its first line
        getRoot().pseudoClassStateChanged(ERROR_PSEUDO_CLASS, isError);
        getRoot().pseudoClassStateChanged(EMPTY_SEARCH_PSEUDO_CLASS, isEmptySearch);
        setShown(getRoot(), true);
    }
}

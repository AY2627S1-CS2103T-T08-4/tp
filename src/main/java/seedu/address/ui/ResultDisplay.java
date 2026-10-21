package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * A UI component that displays the result of a command execution.
 * A success message is shown with a tick, and an error message with a cross.
 */
public class ResultDisplay extends UiPart<Region> {

    /** Matched by {@code .result-display:error} in the CSS while an error message is shown. */
    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private Region resultIcon;
    @FXML
    private Label resultMessage;

    /**
     * Creates a {@code ResultDisplay} that stays hidden until the first command is entered.
     */
    public ResultDisplay() {
        super(FXML);
        getRoot().setVisible(false);
        getRoot().setManaged(false);
    }

    /**
     * Shows {@code feedbackToUser} as the result of a command that succeeded.
     */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        showMessage(feedbackToUser, false);
    }

    /**
     * Shows {@code errorMessage} as the result of a command that failed.
     */
    public void setErrorToUser(String errorMessage) {
        requireNonNull(errorMessage);
        showMessage(errorMessage, true);
    }

    private void showMessage(String message, boolean isError) {
        resultMessage.setText(message);
        getRoot().pseudoClassStateChanged(ERROR_PSEUDO_CLASS, isError);
        getRoot().setVisible(true);
        getRoot().setManaged(true);
    }
}

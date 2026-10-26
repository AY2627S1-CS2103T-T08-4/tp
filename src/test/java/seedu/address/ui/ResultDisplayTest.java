package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class ResultDisplayTest {

    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private static final PseudoClass EMPTY_SEARCH_PSEUDO_CLASS = PseudoClass.getPseudoClass("empty-search");
    private static final String SUCCESS_MESSAGE = "Listed all clients.";
    private static final String ERROR_MESSAGE = "Invalid command format!";
    private static final String EMPTY_SEARCH_MESSAGE = "No clients found.";

    private ResultDisplay resultDisplay;
    private Region root;
    private Label resultMessage;

    @Start
    public void start(Stage stage) {
        resultDisplay = new ResultDisplay();
        root = resultDisplay.getRoot();
        resultMessage = (Label) root.lookup("#resultMessage");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    public void constructor_hiddenUntilFirstMessage() {
        assertFalse(root.isVisible());
        assertFalse(root.isManaged());
    }

    @Test
    public void setFeedbackToUser_message_shownAsSuccess(FxRobot robot) {
        robot.interact(() -> resultDisplay.setFeedbackToUser(SUCCESS_MESSAGE));

        assertEquals(SUCCESS_MESSAGE, resultMessage.getText());
        assertTrue(root.isVisible());
        assertTrue(root.isManaged());
        assertFalse(isShownAsError());
    }

    @Test
    public void setErrorToUser_message_shownAsError(FxRobot robot) {
        robot.interact(() -> resultDisplay.setErrorToUser(ERROR_MESSAGE));

        assertEquals(ERROR_MESSAGE, resultMessage.getText());
        assertTrue(root.isVisible());
        assertTrue(isShownAsError());
    }

    @Test
    public void setFeedbackToUser_afterError_noLongerShownAsError(FxRobot robot) {
        robot.interact(() -> resultDisplay.setErrorToUser(ERROR_MESSAGE));
        robot.interact(() -> resultDisplay.setFeedbackToUser(SUCCESS_MESSAGE));

        assertEquals(SUCCESS_MESSAGE, resultMessage.getText());
        assertFalse(isShownAsError());
    }

    @Test
    public void setEmptySearchToUser_message_shownAsEmptySearch(FxRobot robot) {
        robot.interact(() -> resultDisplay.setErrorToUser(ERROR_MESSAGE));
        robot.interact(() -> resultDisplay.setEmptySearchToUser(EMPTY_SEARCH_MESSAGE));

        assertEquals(EMPTY_SEARCH_MESSAGE, resultMessage.getText());
        assertTrue(root.isVisible());
        assertTrue(isShownAsEmptySearch());
        assertFalse(isShownAsError());
    }

    @Test
    public void setFeedbackToUser_afterEmptySearch_noLongerShownAsEmptySearch(FxRobot robot) {
        robot.interact(() -> resultDisplay.setEmptySearchToUser(EMPTY_SEARCH_MESSAGE));
        robot.interact(() -> resultDisplay.setFeedbackToUser(SUCCESS_MESSAGE));

        assertFalse(isShownAsEmptySearch());
    }

    @Test
    public void setMessage_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> resultDisplay.setFeedbackToUser(null));
        assertThrows(NullPointerException.class, () -> resultDisplay.setEmptySearchToUser(null));
        assertThrows(NullPointerException.class, () -> resultDisplay.setErrorToUser(null));
    }

    private boolean isShownAsError() {
        return root.getPseudoClassStates().contains(ERROR_PSEUDO_CLASS);
    }

    private boolean isShownAsEmptySearch() {
        return root.getPseudoClassStates().contains(EMPTY_SEARCH_PSEUDO_CLASS);
    }
}

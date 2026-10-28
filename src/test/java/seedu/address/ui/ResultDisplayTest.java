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
import javafx.geometry.VerticalDirection;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class ResultDisplayTest {

    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private static final PseudoClass EMPTY_SEARCH_PSEUDO_CLASS = PseudoClass.getPseudoClass("empty-search");
    private static final String SUCCESS_MESSAGE = "Listed all clients.";
    private static final String ERROR_MESSAGE = "Invalid command format!";
    private static final String EMPTY_SEARCH_MESSAGE = "No clients found.";
    private static final String LONG_MESSAGE = "Deleted clients:" + "\nClient; Phone: 91234567".repeat(18);
    private static final double PIXEL_TOLERANCE = 0.5;
    /** More pages than {@code LONG_MESSAGE} has, so that a scrolling loop always ends. */
    private static final int MAX_PAGES = 20;

    private Stage stage;
    private ResultDisplay resultDisplay;
    private Region root;
    private ScrollPane resultScrollPane;
    private Label resultMessage;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        resultDisplay = new ResultDisplay();
        root = resultDisplay.getRoot();
        resultScrollPane = (ScrollPane) root.lookup("#resultScrollPane");
        resultMessage = (Label) resultScrollPane.getContent();
        Scene scene = new Scene(root);
        // The same font as in the main window, as the height of a line depends on it
        scene.getStylesheets().add("view/LightTheme.css");
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void constructor_noMessageYet_hidden() {
        assertFalse(root.isVisible());
        assertFalse(root.isManaged());
    }

    @Test
    public void setFeedbackToUser_message_shownAsSuccess(FxRobot robot) {
        robot.interact(() -> resultDisplay.setFeedbackToUser(SUCCESS_MESSAGE));

        assertShown(SUCCESS_MESSAGE, false, false);
    }

    @Test
    public void setErrorToUser_message_shownAsError(FxRobot robot) {
        robot.interact(() -> resultDisplay.setErrorToUser(ERROR_MESSAGE));

        assertShown(ERROR_MESSAGE, true, false);
    }

    @Test
    public void setFeedbackToUser_afterError_noLongerShownAsError(FxRobot robot) {
        robot.interact(() -> resultDisplay.setErrorToUser(ERROR_MESSAGE));
        robot.interact(() -> resultDisplay.setFeedbackToUser(SUCCESS_MESSAGE));

        assertShown(SUCCESS_MESSAGE, false, false);
    }

    @Test
    public void setEmptySearchToUser_afterError_shownAsEmptySearchOnly(FxRobot robot) {
        robot.interact(() -> resultDisplay.setErrorToUser(ERROR_MESSAGE));
        robot.interact(() -> resultDisplay.setEmptySearchToUser(EMPTY_SEARCH_MESSAGE));

        assertShown(EMPTY_SEARCH_MESSAGE, false, true);
    }

    @Test
    public void setFeedbackToUser_afterEmptySearch_noLongerShownAsEmptySearch(FxRobot robot) {
        robot.interact(() -> resultDisplay.setEmptySearchToUser(EMPTY_SEARCH_MESSAGE));
        robot.interact(() -> resultDisplay.setFeedbackToUser(SUCCESS_MESSAGE));

        assertShown(SUCCESS_MESSAGE, false, false);
    }

    @Test
    public void setErrorToUser_afterEmptySearch_shownAsErrorOnly(FxRobot robot) {
        robot.interact(() -> resultDisplay.setEmptySearchToUser(EMPTY_SEARCH_MESSAGE));
        robot.interact(() -> resultDisplay.setErrorToUser(ERROR_MESSAGE));

        assertShown(ERROR_MESSAGE, true, false);
    }

    @Test
    public void setFeedbackToUser_longMessage_scrollsWithinMaxHeight(FxRobot robot) {
        showAndLayOut(robot, LONG_MESSAGE);

        assertEquals(resultScrollPane.getMaxHeight(), resultScrollPane.getHeight());
        // Every line can be scrolled to, as the label is tall enough for the whole message
        assertEquals(LONG_MESSAGE, getShownText());
        assertTrue(resultMessage.getHeight() > resultScrollPane.getHeight());
    }

    @Test
    public void setFeedbackToUser_afterScrolling_startsAtFirstLine(FxRobot robot) {
        robot.interact(() -> resultDisplay.setFeedbackToUser(LONG_MESSAGE));
        robot.interact(() -> resultScrollPane.setVvalue(resultScrollPane.getVmax()));
        robot.interact(() -> resultDisplay.setFeedbackToUser(LONG_MESSAGE));

        assertEquals(resultScrollPane.getVmin(), resultScrollPane.getVvalue());
    }

    @Test
    public void maxHeight_lightTheme_wholeNumberOfLines(FxRobot robot) {
        showAndLayOut(robot, SUCCESS_MESSAGE);

        // Otherwise, the last line in view is cut in half
        double lineHeight = resultMessage.getHeight();
        assertEquals(0, resultScrollPane.getMaxHeight() % lineHeight);
    }

    @Test
    public void scrollPage_downThroughLongMessage_movesOnePageAtATimeUntilLastLine(FxRobot robot) {
        showAndLayOut(robot, LONG_MESSAGE);
        double pageHeight = resultScrollPane.getViewportBounds().getHeight();

        robot.interact(() -> resultDisplay.scrollPage(VerticalDirection.DOWN));
        assertEquals(pageHeight, getScrolledHeight(), PIXEL_TOLERANCE);

        for (int i = 0; i < MAX_PAGES && resultScrollPane.getVvalue() < resultScrollPane.getVmax(); i++) {
            robot.interact(() -> resultDisplay.scrollPage(VerticalDirection.DOWN));
        }
        assertEquals(resultScrollPane.getVmax(), resultScrollPane.getVvalue());

        // Stays at the last line
        robot.interact(() -> resultDisplay.scrollPage(VerticalDirection.DOWN));
        assertEquals(resultScrollPane.getVmax(), resultScrollPane.getVvalue());
    }

    @Test
    public void scrollPage_upFromLastLine_movesOnePageUpUntilFirstLine(FxRobot robot) {
        showAndLayOut(robot, LONG_MESSAGE);
        double pageHeight = resultScrollPane.getViewportBounds().getHeight();
        double hiddenHeight = resultMessage.getHeight() - pageHeight;
        robot.interact(() -> resultScrollPane.setVvalue(resultScrollPane.getVmax()));

        robot.interact(() -> resultDisplay.scrollPage(VerticalDirection.UP));
        assertEquals(hiddenHeight - pageHeight, getScrolledHeight(), PIXEL_TOLERANCE);

        for (int i = 0; i < MAX_PAGES && resultScrollPane.getVvalue() > resultScrollPane.getVmin(); i++) {
            robot.interact(() -> resultDisplay.scrollPage(VerticalDirection.UP));
        }
        assertEquals(resultScrollPane.getVmin(), resultScrollPane.getVvalue());

        // Stays at the first line
        robot.interact(() -> resultDisplay.scrollPage(VerticalDirection.UP));
        assertEquals(resultScrollPane.getVmin(), resultScrollPane.getVvalue());
    }

    @Test
    public void scrollPage_shortMessage_doesNotScroll(FxRobot robot) {
        showAndLayOut(robot, SUCCESS_MESSAGE);

        robot.interact(() -> resultDisplay.scrollPage(VerticalDirection.DOWN));
        assertEquals(resultScrollPane.getVmin(), resultScrollPane.getVvalue());
    }

    @Test
    public void scrollPage_beforeFirstMessage_doesNotScroll(FxRobot robot) {
        robot.interact(() -> resultDisplay.scrollPage(VerticalDirection.DOWN));

        assertEquals(resultScrollPane.getVmin(), resultScrollPane.getVvalue());
    }

    @Test
    public void setMessage_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> resultDisplay.setFeedbackToUser(null));
        assertThrows(NullPointerException.class, () -> resultDisplay.setEmptySearchToUser(null));
        assertThrows(NullPointerException.class, () -> resultDisplay.setErrorToUser(null));
        assertThrows(NullPointerException.class, () -> resultDisplay.scrollPage(null));
    }

    /**
     * Asserts that {@code message} is shown, and whether it is styled as an error and as an empty search.
     */
    private void assertShown(String message, boolean isError, boolean isEmptySearch) {
        assertEquals(message, resultMessage.getText());
        assertTrue(root.isVisible());
        assertTrue(root.isManaged());
        assertEquals(isError, isShownAsError());
        assertEquals(isEmptySearch, isShownAsEmptySearch());
    }

    /**
     * Shows {@code message} and lays it out in a window sized to fit it.
     * The window is reused from the previous test, which may have left it too small for even one line.
     */
    private void showAndLayOut(FxRobot robot, String message) {
        robot.interact(() -> resultDisplay.setFeedbackToUser(message));
        robot.interact(() -> {
            stage.sizeToScene();
            root.layout();
        });
    }

    /**
     * Returns how many pixels of the message are scrolled above the top of the scroll pane.
     */
    private double getScrolledHeight() {
        double hiddenHeight = resultMessage.getHeight() - resultScrollPane.getViewportBounds().getHeight();
        double vmin = resultScrollPane.getVmin();
        return (resultScrollPane.getVvalue() - vmin) / (resultScrollPane.getVmax() - vmin) * hiddenHeight;
    }

    /**
     * Returns the text the label shows, which ends in "..." if the label is too small for the whole message.
     */
    private String getShownText() {
        return ((Text) resultMessage.lookup(".text")).getText();
    }

    private boolean isShownAsError() {
        return root.getPseudoClassStates().contains(ERROR_PSEUDO_CLASS);
    }

    private boolean isShownAsEmptySearch() {
        return root.getPseudoClassStates().contains(EMPTY_SEARCH_PSEUDO_CLASS);
    }
}

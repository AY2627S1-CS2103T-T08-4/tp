package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DUE_DATE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FEE;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import seedu.address.testutil.PersonBuilder;

@ExtendWith(ApplicationExtension.class)
public class PersonCardTest {

    private static final String LONG_EMAIL = "a".repeat(110) + "@example.com";
    /** 50 characters, the longest name allowed. */
    private static final String LONG_NAME = "Muhammad Hafiz bin Abdul Rahman Ismail (NHHS Sec4)";
    /** 50 characters with no space, so the name can only wrap in the middle of the word. */
    private static final String LONG_SINGLE_WORD_NAME = "A" + "b".repeat(49);
    private static final double MIN_GAP_BEFORE_PHONE = 8;

    private Stage stage;
    private PersonCard taggedCard;
    /** No tags, fee, due date or note. */
    private PersonCard plainCard;
    private PersonCard longEmailCard;
    private PersonCard longNameCard;
    private PersonCard compactCard;
    private PersonCard longSingleWordNameCard;
    private PersonCard feeOnlyCard;
    private PersonCard dueDateOnlyCard;
    private PersonCard feeAndDueDateCard;
    private PersonCard noteCard;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        taggedCard = new PersonCard(new PersonBuilder().withTags("math", "Sec4", "english", "Zoology").build(), 1);
        plainCard = new PersonCard(new PersonBuilder().withTags().build(), 2);
        longEmailCard = new PersonCard(new PersonBuilder().withEmail(LONG_EMAIL).build(), 3);
        longNameCard = new PersonCard(new PersonBuilder().withName(LONG_NAME).build(), 4);
        compactCard = new PersonCard(new PersonBuilder().withNote("Prefers evenings")
                .withFee("250").withDueDate("01-11-2026").build(), 5, true);

        longSingleWordNameCard = new PersonCard(new PersonBuilder().withName(LONG_SINGLE_WORD_NAME).build(), 5);
        // Not shown in the window, as only which of their details are shown is checked
        feeOnlyCard = new PersonCard(new PersonBuilder().withFee(VALID_FEE).build(), 6);
        dueDateOnlyCard = new PersonCard(new PersonBuilder().withDueDate(VALID_DUE_DATE).build(), 7);
        feeAndDueDateCard = new PersonCard(
                new PersonBuilder().withFee(VALID_FEE).withDueDate(VALID_DUE_DATE).build(), 8);
        noteCard = new PersonCard(new PersonBuilder().withNote("Weak at algebra").build(), 9);

        Scene scene = new Scene(new VBox(taggedCard.getRoot(), plainCard.getRoot(), longEmailCard.getRoot(),
                longNameCard.getRoot(), longSingleWordNameCard.getRoot()));
        // The app's fonts, so that the text takes as much room as it does in the app
        scene.getStylesheets().add("view/LightTheme.css");
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void constructor_mixedCaseTags_sortedIgnoringCase() {
        List<String> shownTags = getTags(taggedCard).getChildren().stream()
                .map(node -> ((Label) node).getText())
                .toList();
        assertEquals(List.of("english", "math", "Sec4", "Zoology"), shownTags);
    }

    @Test
    public void constructor_withTags_showsTagRow() {
        assertShown(taggedCard, "#tags", true);
    }

    @Test
    public void constructor_noTags_hidesTagRow() {
        assertShown(plainCard, "#tags", false);
    }

    @Test
    public void constructor_withTags_eachTagColoredByName() {
        for (Node node : getTags(taggedCard).getChildren()) {
            Label tag = (Label) node;
            assertTrue(tag.getStyleClass().contains(TagColor.getStyleClass(tag.getText())), tag.getText());
        }
    }

    @Test
    public void constructor_compact_hidesDetailsButKeepsSummary() {
        assertTrue(compactCard.getRoot().lookup("#name").isVisible());
        assertTrue(compactCard.getRoot().lookup("#phone").isVisible());
        assertFalse(compactCard.getRoot().lookup("#email").isVisible());
        assertFalse(compactCard.getRoot().lookup("#address").isVisible());
        assertFalse(compactCard.getRoot().lookup("#billing").isVisible());
        assertFalse(compactCard.getRoot().lookup("#note").isVisible());
    }

    @Test
    public void constructor_feeOnly_showsFeeWithoutDueDate() {
        assertShown(feeOnlyCard, "#billing", true);
        assertShown(feeOnlyCard, "#fee", true);
        assertShown(feeOnlyCard, "#dueDate", false);
    }

    @Test
    public void constructor_dueDateOnly_showsDueDateWithoutFee() {
        assertShown(dueDateOnlyCard, "#billing", true);
        assertShown(dueDateOnlyCard, "#fee", false);
        assertShown(dueDateOnlyCard, "#dueDate", true);
    }

    @Test
    public void constructor_feeAndDueDate_showsBoth() {
        assertShown(feeAndDueDateCard, "#billing", true);
        assertShown(feeAndDueDateCard, "#fee", true);
        assertShown(feeAndDueDateCard, "#dueDate", true);
    }

    @Test
    public void constructor_noFeeOrDueDate_hidesBillingRow() {
        assertShown(plainCard, "#billing", false);
        assertShown(plainCard, "#fee", false);
        assertShown(plainCard, "#dueDate", false);
    }

    @Test
    public void constructor_withNote_showsNote() {
        assertShown(noteCard, "#note", true);
    }

    @Test
    public void constructor_noNote_hidesNote() {
        assertShown(plainCard, "#note", false);
    }

    @Test
    public void constructor_longEmail_phoneAndFullEmailShown(FxRobot robot) {
        assertShownInFullAtEachWidth(robot, longEmailCard, "#email", LONG_EMAIL);
    }

    @Test
    public void constructor_longName_phoneAndFullNameShown(FxRobot robot) {
        assertShownInFullAtEachWidth(robot, longNameCard, "#name", LONG_NAME);
    }

    @Test
    public void constructor_longSingleWordName_phoneAndFullNameShown(FxRobot robot) {
        assertShownInFullAtEachWidth(robot, longSingleWordNameCard, "#name", LONG_SINGLE_WORD_NAME);
    }

    private FlowPane getTags(PersonCard card) {
        return (FlowPane) card.getRoot().lookup("#tags");
    }

    /**
     * Asserts that the node {@code id} on {@code card} is shown if {@code isShown},
     * or is hidden and takes up no space otherwise.
     */
    private void assertShown(PersonCard card, String id, boolean isShown) {
        Node node = card.getRoot().lookup(id);
        assertEquals(isShown, node.isVisible(), id + " visible");
        assertEquals(isShown, node.isManaged(), id + " managed");
    }

    /**
     * Asserts that the label {@code labelId} on {@code card} shows all of {@code text} without covering the phone
     * number, and that the phone number is not cut off, at both the minimum and a wide window width.
     */
    private void assertShownInFullAtEachWidth(FxRobot robot, PersonCard card, String labelId, String text) {
        Label phone = (Label) card.getRoot().lookup("#phone");
        Label label = (Label) card.getRoot().lookup(labelId);
        for (double width : new double[] {600, 800}) {
            // Tall enough for all the cards, so that none is squeezed, as the client list would scroll instead
            robot.interact(() -> stage.setWidth(width));
            robot.interact(() -> stage.setHeight(1000));

            Bounds phoneBounds = phone.localToScene(phone.getBoundsInLocal());
            Bounds labelBounds = label.localToScene(label.getBoundsInLocal());
            assertTrue(phoneBounds.getMaxX() <= stage.getScene().getWidth(), "phone cut off at width " + width);
            // The name ends a gap before the phone on the same row, so they don't run together, and the email is on
            // a row below it
            boolean isBesidePhone = labelBounds.getMaxX() + MIN_GAP_BEFORE_PHONE <= phoneBounds.getMinX();
            boolean isBelowPhone = labelBounds.getMinY() >= phoneBounds.getMaxY();
            assertTrue(isBesidePhone || isBelowPhone, labelId + " too close to phone at width " + width);
            // A label that runs out of room shows a shortened copy of its text, ending in "..."
            assertEquals(text, ((Text) label.lookup(".text")).getText(), labelId + " shortened at width " + width);
        }
    }
}

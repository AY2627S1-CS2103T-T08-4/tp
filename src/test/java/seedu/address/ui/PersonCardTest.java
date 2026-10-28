package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

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

    private Stage stage;
    private PersonCard taggedCard;
    private PersonCard untaggedCard;
    private PersonCard longEmailCard;
    private PersonCard longNameCard;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        taggedCard = new PersonCard(new PersonBuilder().withTags("math", "Sec4", "english", "Zoology").build(), 1);
        untaggedCard = new PersonCard(new PersonBuilder().withTags().build(), 2);
        longEmailCard = new PersonCard(new PersonBuilder().withEmail(LONG_EMAIL).build(), 3);
        longNameCard = new PersonCard(new PersonBuilder().withName(LONG_NAME).build(), 4);
        Scene scene = new Scene(new VBox(taggedCard.getRoot(), untaggedCard.getRoot(), longEmailCard.getRoot(),
                longNameCard.getRoot()));
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
        FlowPane tags = getTags(taggedCard);
        assertTrue(tags.isVisible());
        assertTrue(tags.isManaged());
    }

    @Test
    public void constructor_noTags_hidesTagRow() {
        FlowPane tags = getTags(untaggedCard);
        assertFalse(tags.isVisible());
        assertFalse(tags.isManaged());
    }

    @Test
    public void constructor_longEmail_phoneAndFullEmailShown(FxRobot robot) {
        Label phone = (Label) longEmailCard.getRoot().lookup("#phone");
        Label email = (Label) longEmailCard.getRoot().lookup("#email");
        for (double width : new double[] {450, 800}) {
            // Tall enough for all the cards, so that none is squeezed, as the client list would scroll instead
            robot.interact(() -> stage.setWidth(width));
            robot.interact(() -> stage.setHeight(1000));

            double phoneRightEdge = phone.localToScene(phone.getBoundsInLocal()).getMaxX();
            assertTrue(phoneRightEdge <= stage.getScene().getWidth(), "phone cut off at width " + width);
            // A label that runs out of room shows a shortened copy of its text, ending in "..."
            assertEquals(LONG_EMAIL, ((Text) email.lookup(".text")).getText(), "email shortened at width " + width);
        }
    }

    @Test
    public void constructor_longName_phoneAndFullNameShown(FxRobot robot) {
        Label phone = (Label) longNameCard.getRoot().lookup("#phone");
        Label name = (Label) longNameCard.getRoot().lookup("#name");
        for (double width : new double[] {450, 800}) {
            robot.interact(() -> stage.setWidth(width));
            robot.interact(() -> stage.setHeight(1000));

            double phoneRightEdge = phone.localToScene(phone.getBoundsInLocal()).getMaxX();
            assertTrue(phoneRightEdge <= stage.getScene().getWidth(), "phone cut off at width " + width);
            double nameRightEdge = name.localToScene(name.getBoundsInLocal()).getMaxX();
            double phoneLeftEdge = phone.localToScene(phone.getBoundsInLocal()).getMinX();
            assertTrue(nameRightEdge <= phoneLeftEdge, "name overlaps phone at width " + width);
            assertEquals(LONG_NAME, ((Text) name.lookup(".text")).getText(), "name shortened at width " + width);
        }
    }

    private FlowPane getTags(PersonCard card) {
        return (FlowPane) card.getRoot().lookup("#tags");
    }
}

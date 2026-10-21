package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.testutil.PersonBuilder;

@ExtendWith(ApplicationExtension.class)
public class PersonCardTest {

    private PersonCard taggedCard;
    private PersonCard untaggedCard;

    @Start
    public void start(Stage stage) {
        taggedCard = new PersonCard(new PersonBuilder().withTags("math", "Sec4", "english", "Zoology").build(), 1);
        untaggedCard = new PersonCard(new PersonBuilder().withTags().build(), 2);
        stage.setScene(new Scene(new VBox(taggedCard.getRoot(), untaggedCard.getRoot())));
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

    private FlowPane getTags(PersonCard card) {
        return (FlowPane) card.getRoot().lookup("#tags");
    }
}

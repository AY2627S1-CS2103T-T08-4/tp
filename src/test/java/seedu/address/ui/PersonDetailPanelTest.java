package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

@ExtendWith(ApplicationExtension.class)
public class PersonDetailPanelTest {

    private final AtomicBoolean wasClosed = new AtomicBoolean();
    private PersonDetailPanel detailPanel;

    @Start
    public void start(Stage stage) {
        detailPanel = new PersonDetailPanel(() -> wasClosed.set(true));
        stage.setScene(new Scene(detailPanel.getRoot()));
        stage.show();
    }

    @Test
    public void setPerson_personWithAllDetails_displaysDetailsAndSortedTags(FxRobot robot) {
        Person person = new PersonBuilder().withName("Ravi Kumar").withPhone("87654321")
                .withEmail("ravi.k@edu.sg").withAddress("10 Queenstown Rd")
                .withNote("Prefers evening slots").withFee("250").withDueDate("01-11-2026")
                .withTags("physics", "Sec 4", "Thursday").build();

        robot.interact(() -> detailPanel.setPerson(person));

        assertEquals("Ravi Kumar", getLabel("#name").getText());
        assertEquals("87654321", getLabel("#phone").getText());
        assertEquals("ravi.k@edu.sg", getLabel("#email").getText());
        assertEquals("10 Queenstown Rd", getLabel("#address").getText());
        assertEquals("Prefers evening slots", getLabel("#note").getText());
        assertEquals("250.00", getLabel("#fee").getText());
        assertEquals("01-11-2026", getLabel("#dueDate").getText());
        FlowPane tags = (FlowPane) detailPanel.getRoot().lookup("#tags");
        assertEquals("physics", ((Label) tags.getChildren().get(0)).getText());
        assertEquals("Sec 4", ((Label) tags.getChildren().get(1)).getText());
        assertEquals("Thursday", ((Label) tags.getChildren().get(2)).getText());
    }

    @Test
    public void setPerson_personWithoutOptionalDetails_hidesOptionalRows(FxRobot robot) {
        robot.interact(() -> detailPanel.setPerson(new PersonBuilder().build()));

        assertFalse(detailPanel.getRoot().lookup("#noteRow").isManaged());
        assertFalse(detailPanel.getRoot().lookup("#feeRow").isManaged());
        assertFalse(detailPanel.getRoot().lookup("#dueDateRow").isManaged());
        assertFalse(detailPanel.getRoot().lookup("#tags").isManaged());
    }

    @Test
    public void closeButton_pressed_invokesCloseHandler(FxRobot robot) {
        Button closeButton = (Button) detailPanel.getRoot().lookup("#closeDetailButton");

        robot.interact(closeButton::fire);

        assertTrue(wasClosed.get());
    }

    private Label getLabel(String selector) {
        return (Label) detailPanel.getRoot().lookup(selector);
    }
}

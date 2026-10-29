package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import seedu.address.model.person.Person;

@ExtendWith(ApplicationExtension.class)
public class PersonListPanelTest {

    private final ObservableList<Person> persons = FXCollections.observableArrayList(ALICE, BENSON, CARL);
    // The app shows a FilteredList, which find and filter narrow down, so the tests use one too
    private final FilteredList<Person> displayedPersons = new FilteredList<>(persons);
    private Label clientCount;
    private PersonListPanel personListPanel;
    private ListView<Person> personListView;

    @Start
    @SuppressWarnings("unchecked")
    public void start(Stage stage) {
        personListPanel = new PersonListPanel(displayedPersons, persons);
        clientCount = (Label) personListPanel.getRoot().lookup("#clientCount");
        personListView = (ListView<Person>) personListPanel.getRoot().lookup("#personListView");
        stage.setScene(new Scene(personListPanel.getRoot()));
        stage.show();
    }

    @Test
    public void constructor_threeClients_showsCount() {
        assertEquals("3 clients", clientCount.getText());
    }

    @Test
    public void clientCount_clientsRemoved_updatesCount(FxRobot robot) {
        robot.interact(() -> persons.remove(ALICE));
        assertEquals("2 clients", clientCount.getText());

        robot.interact(() -> persons.remove(BENSON));
        assertEquals("1 client", clientCount.getText());

        robot.interact(persons::clear);
        assertEquals("0 clients", clientCount.getText());
    }

    @Test
    public void personListView_noClients_suggestsAddOnly(FxRobot robot) {
        robot.interact(persons::clear);
        assertPlaceholderShown(PersonListPanel.MESSAGE_NO_CLIENTS);
    }

    @Test
    public void personListView_allClientsHidden_suggestsList(FxRobot robot) {
        robot.interact(() -> displayedPersons.setPredicate(person -> false));
        assertPlaceholderShown(PersonListPanel.MESSAGE_ALL_CLIENTS_HIDDEN);
    }

    @Test
    public void personListView_lastHiddenClientDeleted_switchesToSuggestAdd(FxRobot robot) {
        robot.interact(() -> displayedPersons.setPredicate(person -> false));
        robot.interact(persons::clear);
        assertPlaceholderShown(PersonListPanel.MESSAGE_NO_CLIENTS);
    }

    @Test
    public void clientCount_listFiltered_countsDisplayedClientsOnly(FxRobot robot) {
        robot.interact(() -> displayedPersons.setPredicate(ALICE::equals));
        assertEquals("1 client", clientCount.getText());

        robot.interact(() -> displayedPersons.setPredicate(null));
        assertEquals("3 clients", clientCount.getText());
    }

    @Test
    public void selectedPerson_clientNoLongerListed_clearsSelection(FxRobot robot) {
        robot.interact(() -> personListView.getSelectionModel().select(CARL));
        assertEquals(CARL, personListPanel.selectedPersonProperty().get());

        // Daniel takes Carl's row, so a selection kept by row would show him
        robot.interact(() -> persons.setAll(ALICE, BENSON, DANIEL));

        assertTrue(personListView.getSelectionModel().isEmpty());
        assertNull(personListPanel.selectedPersonProperty().get());
    }

    @Test
    public void selectedPerson_clientMovesRow_staysSelected(FxRobot robot) {
        robot.interact(() -> personListView.getSelectionModel().select(CARL));

        // As after undo or find, which replace the whole list. Benson takes Carl's row
        robot.interact(() -> persons.setAll(CARL, ALICE, BENSON));

        assertEquals(CARL, personListView.getSelectionModel().getSelectedItem());
        assertEquals(CARL, personListPanel.selectedPersonProperty().get());
    }

    @Test
    public void setCompact_trueThenFalse_hidesThenShowsEmail(FxRobot robot) {
        robot.interact(() -> personListPanel.setCompact(true));
        // Setting the same value again keeps the cards compact
        robot.interact(() -> personListPanel.setCompact(true));
        assertEmailShownOnEveryCard(false);

        robot.interact(() -> personListPanel.setCompact(false));
        assertEmailShownOnEveryCard(true);
    }

    /**
     * Asserts that every client's card shows its email if {@code isShown}, or hides it and leaves no gap otherwise.
     */
    private void assertEmailShownOnEveryCard(boolean isShown) {
        // The ListView keeps spare cells for scrolling, which are hidden or empty
        List<Node> cards = personListView.lookupAll(".list-cell").stream()
                .map(cell -> (ListCell<?>) cell)
                .filter(cell -> cell.isVisible() && cell.getItem() != null)
                .map(ListCell::getGraphic)
                .toList();
        assertEquals(persons.size(), cards.size());
        for (Node card : cards) {
            Node email = card.lookup("#email");
            assertEquals(isShown, email.isVisible());
            assertEquals(isShown, email.isManaged());
        }
    }

    private void assertPlaceholderShown(String expectedText) {
        Label placeholder = (Label) personListView.getPlaceholder();
        assertTrue(placeholder.isVisible());
        assertEquals(expectedText, placeholder.getText());
    }
}

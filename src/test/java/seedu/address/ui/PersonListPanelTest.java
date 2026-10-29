package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
        PersonListPanel personListPanel = new PersonListPanel(displayedPersons, persons);
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
        assertPlaceholderShown(robot, PersonListPanel.MESSAGE_NO_CLIENTS);
    }

    @Test
    public void personListView_allClientsHidden_suggestsList(FxRobot robot) {
        robot.interact(() -> displayedPersons.setPredicate(person -> false));
        assertPlaceholderShown(robot, PersonListPanel.MESSAGE_ALL_CLIENTS_HIDDEN);
    }

    @Test
    public void personListView_lastHiddenClientDeleted_switchesToSuggestAdd(FxRobot robot) {
        robot.interact(() -> displayedPersons.setPredicate(person -> false));
        robot.interact(persons::clear);
        assertPlaceholderShown(robot, PersonListPanel.MESSAGE_NO_CLIENTS);
    }

    @Test
    public void clientCount_listFiltered_countsDisplayedClientsOnly(FxRobot robot) {
        robot.interact(() -> displayedPersons.setPredicate(ALICE::equals));
        assertEquals("1 client", clientCount.getText());

        robot.interact(() -> displayedPersons.setPredicate(null));
        assertEquals("3 clients", clientCount.getText());
    }

    @Test
    public void selectedPerson_replacedByShorterList_clearsSelection(FxRobot robot) {
        robot.interact(() -> personListView.getSelectionModel().select(CARL));
        assertEquals(CARL, personListPanel.selectedPersonProperty().get());

        robot.interact(() -> persons.setAll(ALICE));

        assertTrue(personListView.getSelectionModel().isEmpty());
        assertNull(personListPanel.selectedPersonProperty().get());
    }

    @Test
    public void setCompact_sameValueTwice_keepsDisplayedClients(FxRobot robot) {
        robot.interact(() -> {
            personListPanel.setCompact(true);
            personListPanel.setCompact(true);
        });

        assertEquals(3, personListView.getItems().size());
    }

    @Test
    public void assertPlaceholderShown(FxRobot robot, String expectedText) {
        ListView<?> personListView = robot.lookup("#personListView").queryAs(ListView.class);
        Label placeholder = (Label) personListView.getPlaceholder();
        assertTrue(placeholder.isVisible());
        assertEquals(expectedText, placeholder.getText());
    }
}

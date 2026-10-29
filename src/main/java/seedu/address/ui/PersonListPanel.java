package seedu.address.ui;

import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Messages;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    public static final String MESSAGE_NO_CLIENTS = "No clients yet. Use add to add one.";
    public static final String MESSAGE_ALL_CLIENTS_HIDDEN = "No clients to show. Use list to show all clients.";

    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private Label clientCount;
    @FXML
    private ListView<Person> personListView;
    @FXML
    private Label placeholder;

    /**
     * Creates a {@code PersonListPanel} that shows {@code personList}, the clients currently displayed.
     * {@code allPersons} is every client, which decides what the placeholder suggests when none are shown.
     */
    public PersonListPanel(ObservableList<Person> personList, ObservableList<Person> allPersons) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        // Recomputes the count whenever personList changes, e.g. after add, delete, find or filter
        clientCount.textProperty().bind(
                Bindings.createStringBinding(() -> Messages.formatClientCount(personList.size()), personList));
        // With no clients at all, list can't help, so only suggest add. Otherwise, a search or filter hides them all
        placeholder.textProperty().bind(Bindings.when(Bindings.isEmpty(allPersons))
                .then(MESSAGE_NO_CLIENTS)
                .otherwise(MESSAGE_ALL_CLIENTS_HIDDEN));
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        PersonListViewCell() {
            // Size each row to the list's width instead of its content, so long details are shortened or wrapped
            setPrefWidth(0);
        }

        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }

}

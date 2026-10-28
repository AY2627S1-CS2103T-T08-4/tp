package seedu.address.ui;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
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
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private Label clientCount;
    @FXML
    private ListView<Person> personListView;
    private final ReadOnlyObjectWrapper<Person> selectedPerson = new ReadOnlyObjectWrapper<>();
    private boolean isCompact;
    private boolean isRestoringSelection;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personList.addListener((ListChangeListener<Person>) this::restoreSelectionAfterReplacement);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        personListView.getSelectionModel().selectedItemProperty().addListener((observable, oldPerson, newPerson) -> {
            if (newPerson != null || !isRestoringSelection) {
                selectedPerson.set(newPerson);
            }
        });
        // Recomputes the count whenever personList changes, e.g. after add, delete, find or filter
        clientCount.textProperty().bind(
                Bindings.createStringBinding(() -> Messages.formatClientCount(personList.size()), personList));
    }

    /**
     * Returns the selected person property of the underlying list.
     */
    public ReadOnlyObjectProperty<Person> selectedPersonProperty() {
        return selectedPerson.getReadOnlyProperty();
    }

    private void restoreSelectionAfterReplacement(ListChangeListener.Change<? extends Person> change) {
        while (change.next()) {
            Person currentSelection = selectedPerson.get();
            int removedIndex = change.getRemoved().indexOf(currentSelection);
            if (change.wasReplaced() && removedIndex >= 0) {
                int replacementIndex = change.getFrom() + removedIndex;
                if (replacementIndex < change.getTo()) {
                    isRestoringSelection = true;
                    selectedPerson.set(change.getList().get(replacementIndex));
                    Platform.runLater(() -> {
                        personListView.getSelectionModel().select(replacementIndex);
                        isRestoringSelection = false;
                    });
                }
            }
        }
    }

    /**
     * Clears the selected person.
     */
    public void clearSelection() {
        personListView.getSelectionModel().clearSelection();
    }

    /**
     * Shows only the summary fields in each card while the detail panel is open.
     */
    public void setCompact(boolean isCompact) {
        if (this.isCompact == isCompact) {
            return;
        }
        this.isCompact = isCompact;
        personListView.refresh();
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
                setGraphic(new PersonCard(person, getIndex() + 1, isCompact).getRoot());
            }
        }
    }

}

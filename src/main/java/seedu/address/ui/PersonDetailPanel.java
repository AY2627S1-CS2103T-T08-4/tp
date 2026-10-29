package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * Panel containing all details of the selected person.
 */
public class PersonDetailPanel extends UiPart<Region> {
    private static final String FXML = "PersonDetailPanel.fxml";

    private final Runnable closeHandler;

    @FXML
    private Label name;
    @FXML
    private FlowPane tags;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label address;
    @FXML
    private Label note;
    @FXML
    private Label fee;
    @FXML
    private Label dueDate;
    @FXML
    private Region noteRow;
    @FXML
    private Region feeRow;
    @FXML
    private Region dueDateRow;

    /**
     * Creates a detail panel which invokes {@code closeHandler} when its close button is pressed.
     */
    public PersonDetailPanel(Runnable closeHandler) {
        super(FXML);
        this.closeHandler = closeHandler;
    }

    /**
     * Displays the details of {@code person}.
     */
    public void setPerson(Person person) {
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail().value);
        address.setText(person.getAddress().value);
        note.setText(person.getNote().value);
        fee.setText(person.getFee().value);
        dueDate.setText(person.getDueDate().value);

        tags.getChildren().clear();
        PersonCard.addTagLabels(tags, person.getTags());
        setShown(tags, !person.getTags().isEmpty());
        setShown(noteRow, !person.getNote().isEmpty());
        setShown(feeRow, !person.getFee().isEmpty());
        setShown(dueDateRow, !person.getDueDate().isEmpty());
    }

    @FXML
    private void handleClose() {
        closeHandler.run();
    }
}

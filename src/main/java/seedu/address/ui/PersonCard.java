package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private HBox billing;
    @FXML
    private Label fee;
    @FXML
    private Label dueDate;
    @FXML
    private Label note;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(String.valueOf(displayedIndex));
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);
        fee.setText("Fee: " + person.getFee().value);
        fee.setVisible(!person.getFee().isEmpty());
        fee.setManaged(!person.getFee().isEmpty());
        dueDate.setText("Due: " + person.getDueDate().value);
        dueDate.setVisible(!person.getDueDate().isEmpty());
        dueDate.setManaged(!person.getDueDate().isEmpty());
        boolean hasBilling = !person.getFee().isEmpty() || !person.getDueDate().isEmpty();
        billing.setVisible(hasBilling);
        billing.setManaged(hasBilling);
        note.setText(person.getNote().value);
        // An empty note takes up no space on the card
        note.setVisible(!person.getNote().isEmpty());
        note.setManaged(!person.getNote().isEmpty());
        // A client without tags gets no empty row of tags
        tags.setVisible(!person.getTags().isEmpty());
        tags.setManaged(!person.getTags().isEmpty());
        // Tags are case-insensitive, so they are sorted that way too (Math, physics, Sec4)
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName, String.CASE_INSENSITIVE_ORDER))
                .forEach(tag -> tags.getChildren().add(createTagLabel(tag.tagName)));
    }

    /**
     * Returns a pill-shaped label for the tag called {@code tagName}, coloured by {@link TagColor}.
     */
    private static Label createTagLabel(String tagName) {
        Label tagLabel = new Label(tagName);
        tagLabel.getStyleClass().add(TagColor.getStyleClass(tagName));
        return tagLabel;
    }
}

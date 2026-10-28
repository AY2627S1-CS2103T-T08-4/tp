package seedu.address.ui;

import java.util.Comparator;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

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
        dueDate.setText("Due: " + person.getDueDate().value);
        note.setText(person.getNote().value);
        addTags(person.getTags());

        // Empty optional details take up no space on the card, e.g. a client without tags gets no empty row
        boolean hasFee = !person.getFee().isEmpty();
        boolean hasDueDate = !person.getDueDate().isEmpty();
        setShown(fee, hasFee);
        setShown(dueDate, hasDueDate);
        setShown(billing, hasFee || hasDueDate);
        setShown(note, !person.getNote().isEmpty());
        setShown(tags, !person.getTags().isEmpty());
    }

    /**
     * Adds a pill for each tag in {@code personTags} to the row of tags.
     */
    private void addTags(Set<Tag> personTags) {
        // Tags are case-insensitive, so they are sorted that way too (Math, physics, Sec4)
        personTags.stream()
                .sorted(Comparator.comparing(tag -> tag.tagName, String.CASE_INSENSITIVE_ORDER))
                .forEach(tag -> tags.getChildren().add(createTagLabel(tag.tagName)));
    }

    /**
     * Returns a pill-shaped label for the tag called {@code tagName}, colored by {@link TagColor}.
     */
    private static Label createTagLabel(String tagName) {
        Label tagLabel = new Label(tagName);
        tagLabel.getStyleClass().add(TagColor.getStyleClass(tagName));
        return tagLabel;
    }
}

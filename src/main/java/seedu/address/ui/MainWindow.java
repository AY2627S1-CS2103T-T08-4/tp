package seedu.address.ui;

import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final String DARK_THEME_STYLESHEET = MainWindow.class
            .getResource("/view/DarkTheme.css").toExternalForm();
    private static final String DARK_THEME_BUTTON_TEXT = "Dark theme";
    private static final String LIGHT_THEME_BUTTON_TEXT = "Light theme";
    private static final Duration DETAIL_ANIMATION_DURATION = Duration.millis(200);
    private static final double DETAIL_WIDTH_RATIO = 0.55;
    private static final double MIN_DETAIL_WIDTH = 300;
    private static final double MAX_DETAIL_WIDTH = 520;

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private Path dataFilePath;
    private boolean isDarkTheme;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private PersonDetailPanel personDetailPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;
    private Timeline detailAnimation;
    private boolean isDetailOpen;

    @FXML
    private BorderPane clientContent;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane personDetailPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private Button themeToggleButton;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage}, {@code Logic},
     * and the data file path to show in the status bar.
     */
    public MainWindow(Stage primaryStage, Logic logic, Path dataFilePath) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.dataFilePath = dataFilePath;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in a later version of the SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, some
         * accelerators (e.g., F1) will not work when the focus is in it because the key
         * event is consumed by the TextInputControl.
         *
         * For now, we add the following event filter to capture such key events and open
         * the help window purposely so as to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        personDetailPanel = new PersonDetailPanel(personListPanel::clearSelection);
        personDetailPanelPlaceholder.getChildren().add(personDetailPanel.getRoot());
        clipDetailPanelDuringAnimation();
        personListPanel.selectedPersonProperty().addListener((observable, oldPerson, selectedPerson) -> {
            if (selectedPerson == null) {
                hidePersonDetail();
            } else {
                showPersonDetail(selectedPerson);
            }
        });

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(dataFilePath);
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand, resultDisplay::scrollPage);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (isDetailOpen && event.getCode() == KeyCode.ESCAPE) {
                personListPanel.clearSelection();
                event.consume();
            }
        });
    }

    private void clipDetailPanelDuringAnimation() {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(personDetailPanelPlaceholder.widthProperty());
        clip.heightProperty().bind(personDetailPanelPlaceholder.heightProperty());
        personDetailPanelPlaceholder.setClip(clip);
    }

    private void showPersonDetail(Person person) {
        personDetailPanel.setPerson(person);
        if (isDetailOpen) {
            return;
        }

        isDetailOpen = true;
        personListPanel.setCompact(true);
        personDetailPanelPlaceholder.setManaged(true);
        personDetailPanelPlaceholder.setVisible(true);

        double contentWidth = clientContent.getWidth() > 0 ? clientContent.getWidth() : primaryStage.getWidth();
        double targetWidth = Math.clamp(contentWidth * DETAIL_WIDTH_RATIO, MIN_DETAIL_WIDTH, MAX_DETAIL_WIDTH);
        animatePersonDetail(targetWidth, 1, null);
    }

    private void hidePersonDetail() {
        if (!isDetailOpen) {
            return;
        }

        isDetailOpen = false;
        animatePersonDetail(0, 0, () -> {
            personDetailPanelPlaceholder.setVisible(false);
            personDetailPanelPlaceholder.setManaged(false);
            personListPanel.setCompact(false);
        });
    }

    private void animatePersonDetail(double targetWidth, double targetOpacity, Runnable onFinished) {
        if (detailAnimation != null) {
            detailAnimation.stop();
        }
        detailAnimation = new Timeline(new KeyFrame(DETAIL_ANIMATION_DURATION,
                new KeyValue(personDetailPanelPlaceholder.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                new KeyValue(personDetailPanelPlaceholder.opacityProperty(), targetOpacity, Interpolator.EASE_BOTH)));
        if (onFinished != null) {
            detailAnimation.setOnFinished(event -> onFinished.run());
        }
        detailAnimation.play();
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    /**
     * Switches the application between its light and dark themes.
     */
    @FXML
    private void handleToggleTheme() {
        isDarkTheme = !isDarkTheme;
        if (isDarkTheme) {
            primaryStage.getScene().getStylesheets().add(DARK_THEME_STYLESHEET);
            themeToggleButton.setText(LIGHT_THEME_BUTTON_TEXT);
        } else {
            primaryStage.getScene().getStylesheets().remove(DARK_THEME_STYLESHEET);
            themeToggleButton.setText(DARK_THEME_BUTTON_TEXT);
        }
        helpWindow.setDarkTheme(isDarkTheme);
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            if (commandResult.isEmptySearch()) {
                resultDisplay.setEmptySearchToUser(commandResult.getFeedbackToUser());
            } else {
                resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());
            }

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setErrorToUser(e.getMessage());
            throw e;
        }
    }
}

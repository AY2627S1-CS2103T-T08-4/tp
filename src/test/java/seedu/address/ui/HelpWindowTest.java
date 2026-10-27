package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.ui.HelpWindow.HELP_MESSAGE;
import static seedu.address.ui.HelpWindow.HELP_URL_MESSAGE;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.control.Label;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class HelpWindowTest {

    private HelpWindow helpWindow;

    @Start
    public void start(Stage stage) {
        helpWindow = new HelpWindow(stage);
        helpWindow.show();
    }

    @Test
    public void constructor_initializesHelpLabels(FxRobot robot) {
        Label helpMessage = robot.lookup("#helpMessage").queryAs(Label.class);
        Label helpUrl = robot.lookup("#helpUrl").queryAs(Label.class);

        assertEquals(HELP_MESSAGE, helpMessage.getText());
        assertEquals(HELP_URL_MESSAGE, helpUrl.getText());
    }

    @Test
    public void focus_iconifiedWindow_restoresWindow() throws Exception {
        FxToolkit.setupFixture(() -> helpWindow.getRoot().setIconified(true));
        assertTrue(helpWindow.getRoot().isIconified());

        FxToolkit.setupFixture(helpWindow::focus);

        assertFalse(helpWindow.getRoot().isIconified());
    }

    @Test
    public void setDarkTheme_enableTwiceThenDisable_updatesStylesheetWithoutDuplicates() throws Exception {
        assertEquals(0, getDarkThemeStylesheetCount());

        FxToolkit.setupFixture(() -> helpWindow.setDarkTheme(true));
        assertEquals(1, getDarkThemeStylesheetCount());

        FxToolkit.setupFixture(() -> helpWindow.setDarkTheme(true));
        assertEquals(1, getDarkThemeStylesheetCount());

        FxToolkit.setupFixture(() -> helpWindow.setDarkTheme(false));
        assertEquals(0, getDarkThemeStylesheetCount());
    }

    private long getDarkThemeStylesheetCount() {
        return helpWindow.getRoot().getScene().getStylesheets().stream()
                .filter(stylesheet -> stylesheet.endsWith("DarkTheme.css"))
                .count();
    }
}

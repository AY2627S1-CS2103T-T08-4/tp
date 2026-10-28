package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Keeps track of the commands entered during the session, so that Up and Down can show them again.
 * Every command added is kept, whether it succeeded or failed, including consecutive repeats.
 * When the user goes back from a half-typed command, it is kept and shown again after the newest command.
 */
public class CommandHistory {

    private final List<String> commands = new ArrayList<>();
    /** Index of the command recalled with Up or Down, or {@code commands.size()} when past the newest one. */
    private int currentIndex = 0;
    /** The text that was in the command box when the user last went back from past the newest command. */
    private String draft = "";

    /**
     * Saves {@code command} as the newest command and moves back to the end of the history,
     * so that {@link #getPreviousCommand()} returns {@code command} next.
     */
    public void add(String command) {
        requireNonNull(command);
        commands.add(command);
        currentIndex = commands.size();
    }

    /**
     * Moves back one command and returns it.
     * When moving back from past the newest command, {@code currentText} is kept as the draft,
     * which {@link #getNextCommand()} returns after the newest command.
     * Returns an empty {@code Optional} if there is no earlier command to show,
     * either because nothing has been entered yet or because the oldest command is already shown.
     *
     * @param currentText The text in the command box.
     */
    public Optional<String> getPreviousCommand(String currentText) {
        requireNonNull(currentText);
        if (currentIndex <= 0) {
            return Optional.empty();
        }

        if (currentIndex == commands.size()) {
            draft = currentText;
        }
        currentIndex--;
        return Optional.of(commands.get(currentIndex));
    }

    /**
     * Moves forward one command and returns it.
     * Returns an empty {@code Optional} if there is no next command to show.
     * Returns the draft when moving past the newest command, so a half-typed command comes back.
     */
    public Optional<String> getNextCommand() {
        if (currentIndex >= commands.size()) {
            return Optional.empty();
        }

        currentIndex++;
        if (currentIndex == commands.size()) {
            return Optional.of(draft);
        }
        return Optional.of(commands.get(currentIndex));
    }
}

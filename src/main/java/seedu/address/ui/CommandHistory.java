package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Keeps track of all past commands the user has inputted during the session.
 * Every non-empty command entered is remembered, whether it succeeded or failed.
 * Consecutively repeated commands are also saved.
 */
public class CommandHistory {

    private final List<String> commands = new ArrayList<>();
    private int current = 0; // position of the command shown in the command box

    /**
     * Saves {@code command} into commands.
     */
    public void add(String command) {
        requireNonNull(command);
        commands.add(command);
        current = commands.size();
    }

    /**
     * Moves back one command and returns it.
     * Returns an empty {@code Optional} if there is no earlier command to show,
     * either because nothing has been entered yet or because the oldest command is already shown.
     */
    public Optional<String> getPreviousCommand() {
        if (current <= 0) {
            return Optional.empty();
        }

        current--;
        return Optional.of(commands.get(current));
    }

    /**
     * Moves forward one command and returns it.
     * Returns an empty {@code Optional} if there is no next command to show.
     * Returns an empty string when moving past the newest command, so the command box is cleared.
     */
    public Optional<String> getNextCommand() {
        if (current >= commands.size()) {
            return Optional.empty();
        }

        if (current == commands.size() - 1) {
            current++;
            return Optional.of("");
        }

        current++;
        return Optional.of(commands.get(current));
    }
}

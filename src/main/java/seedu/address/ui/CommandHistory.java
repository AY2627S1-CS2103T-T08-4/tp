package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Keeps track of the commands entered during the session, so that Up and Down can show them again.
 * Every command added is kept, whether it succeeded or failed, including consecutive repeats.
 */
public class CommandHistory {

    private final List<String> commands = new ArrayList<>();
    /** Index of the command recalled with Up or Down, or {@code commands.size()} when past the newest one. */
    private int current = 0;

    /**
     * Saves {@code command} as the newest command and moves back to the end of the history,
     * so that {@link #getPreviousCommand()} returns {@code command} next.
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

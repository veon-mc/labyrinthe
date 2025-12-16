package g64889.dev3.labyrinthe.model.command;

/**
 * Represents a command that can be executed and undone.
 * <p>
 * This interface follows the Command design pattern and is used to
 * encapsulate an action as an object, allowing it to be executed,
 * undone, and managed by a command history.
 * <p>
 * Typical implementations represent game actions such as tile insertion
 * or player movement.
 */
public interface Command {

    /**
     * Executes the command.
     */
    void execute();

    /**
     * Undoes the effects of the command.
     */
    void undo();
}
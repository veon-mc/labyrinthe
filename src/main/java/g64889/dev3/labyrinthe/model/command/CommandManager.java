package g64889.dev3.labyrinthe.model.command;

import java.util.Stack;

/**
 * Manages the execution history of {@link Command} objects.
 * <p>
 * This class implements the undo/redo mechanism using the Command
 * design pattern. Executed commands are stored in an undo stack,
 * while undone commands are stored in a redo stack.
 * <p>
 * The manager is responsible only for command history management;
 * commands themselves define how to execute and undo their actions.
 */
public class CommandManager {

    /** Stack containing executed commands that can be undone. */
    private final Stack<Command> undoStack;

    /** Stack containing undone commands that can be redone. */
    private final Stack<Command> redoStack;

    /**
     * Creates an empty command manager.
     */
    public CommandManager() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    /**
     * Executes a command and stores it in the undo history.
     * <p>
     * After execution, the redo history is cleared.
     *
     * @param command the command to execute
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * Undoes the most recently executed command, if any.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    /**
     * Redoes the most recently undone command, if any.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

    /**
     * Indicates whether an undo operation is possible.
     *
     * @return true if at least one command can be undone
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Indicates whether a redo operation is possible.
     *
     * @return true if at least one command can be redone
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Clears both undo and redo histories.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
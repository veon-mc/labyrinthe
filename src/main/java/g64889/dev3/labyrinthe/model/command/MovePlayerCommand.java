package g64889.dev3.labyrinthe.model.command;

import g64889.dev3.labyrinthe.model.LabyrinthGame;
import g64889.dev3.labyrinthe.model.Player;
import g64889.dev3.labyrinthe.model.Position;

import java.util.List;

/**
 * Command representing a player movement action.
 * <p>
 * This command follows the Command design pattern to allow a movement
 * to be executed and undone. It stores the previous game state required
 * to restore player positions, objective progress, turn order, and
 * end-of-game information.
 */
public class MovePlayerCommand implements Command {

    /** Reference to the game facade. */
    private final LabyrinthGame game;

    /** Target row of the movement. */
    private final int targetRow;

    /** Target column of the movement. */
    private final int targetCol;

    /** Players positions before execution (used for undo). */
    private List<Position> previousPlayerPositions;

    /** Objective indices of all players before execution (used for undo). */
    private List<Integer> previousObjectiveIndices;

    /** Current player index before execution (used for undo). */
    private int previousCurrentPlayerIndex;

    /** Tile insertion state before execution (used for undo). */
    private boolean previousTileInserted;

    /** Game over state before execution (used for undo). */
    private boolean previousGameOver;

    /** Winner before execution (used for undo). */
    private Player previousWinner;

    /**
     * Creates a command that moves the current player to the given position.
     *
     * @param game      the game model facade
     * @param targetRow the target row
     * @param targetCol the target column
     */
    public MovePlayerCommand(LabyrinthGame game, int targetRow, int targetCol) {
        this.game = game;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
    }

    /**
     * Executes the movement.
     * <p>
     * The previous game state is saved before performing the move,
     * so that the operation can later be undone.
     */
    @Override
    public void execute() {
        previousPlayerPositions = game.copyPlayerPositions();
        previousObjectiveIndices = game.copyObjectiveIndices();
        previousCurrentPlayerIndex = game.getCurrentPlayerIndex();
        previousTileInserted = game.isTileInserted();
        previousGameOver = game.isGameOver();
        previousWinner = game.getWinner();

        game.movePlayer(targetRow, targetCol);
    }

    /**
     * Undoes the movement by restoring the previous game state.
     *
     * @throws IllegalStateException if the command has not been executed yet
     */
    @Override
    public void undo() {
        if (previousPlayerPositions == null) {
            throw new IllegalStateException("Command was never executed");
        }

        game.restorePlayerPositions(previousPlayerPositions);
        game.restoreObjectiveIndices(previousObjectiveIndices);
        game.setCurrentPlayerIndex(previousCurrentPlayerIndex);
        game.setTileInserted(previousTileInserted);
        game.setGameOver(previousGameOver);
        game.setWinner(previousWinner);
    }
}
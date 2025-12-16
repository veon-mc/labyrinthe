package g64889.dev3.labyrinthe.model.command;

import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.LabyrinthGame;
import g64889.dev3.labyrinthe.model.Position;
import g64889.dev3.labyrinthe.model.Tile;

import java.util.List;

/**
 * Command representing the insertion of the spare tile into the board.
 * <p>
 * This command follows the Command design pattern and allows the insertion
 * action to be executed and undone. It saves the full game state required
 * to restore the board, spare tile, player positions, and insertion metadata.
 */
public class InsertTileCommand implements Command {

    /** Reference to the game facade. */
    private final LabyrinthGame game;

    /** Side from which the tile is inserted. */
    private final Direction side;

    /** Index at which the tile is inserted. */
    private final int index;

    /** Previous board grid (for undo). */
    private Tile[][] previousGrid;

    /** Previous spare tile (for undo). */
    private Tile previousSpareTile;

    /** Previous player positions (for undo). */
    private List<Position> previousPlayerPositions;

    /** Previous insertion side (for undo). */
    private Direction previousInsertionSide;

    /** Previous insertion index (for undo). */
    private int previousInsertionIndex;

    /** Previous tile insertion state (for undo). */
    private boolean previousTileInserted;

    /**
     * Creates a command that inserts the spare tile at a given location.
     *
     * @param game  the game model facade
     * @param side  the insertion side
     * @param index the insertion index
     */
    public InsertTileCommand(LabyrinthGame game, Direction side, int index) {
        this.game = game;
        this.side = side;
        this.index = index;
    }

    /**
     * Executes the tile insertion.
     * <p>
     * The current game state is saved before applying the insertion,
     * so that the operation can later be undone.
     */
    @Override
    public void execute() {
        previousGrid = game.copyGrid();
        previousSpareTile = game.copySpareTile();
        previousPlayerPositions = game.copyPlayerPositions();
        previousInsertionSide = game.getLastInsertionSide();
        previousInsertionIndex = game.getLastInsertionIndex();
        previousTileInserted = game.isTileInserted();

        game.insertTile(side, index);
    }

    /**
     * Undoes the tile insertion by restoring the previous game state.
     *
     * @throws IllegalStateException if the command has not been executed yet
     */
    @Override
    public void undo() {
        if (previousGrid == null) {
            throw new IllegalStateException("Command has not been executed yet");
        }

        game.restoreGrid(previousGrid);
        game.setSpareTile(previousSpareTile);
        game.restorePlayerPositions(previousPlayerPositions);
        game.setLastInsertion(previousInsertionSide, previousInsertionIndex);
        game.setTileInserted(previousTileInserted);
    }
}
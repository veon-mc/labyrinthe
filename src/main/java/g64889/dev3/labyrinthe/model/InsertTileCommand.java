package g64889.dev3.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;

public class InsertTileCommand implements Command {
    private final LabyrinthGame game;
    private final Direction side;
    private final int index;

    private Tile[][] previousGrid;
    private Tile previousSpareTile;
    private List<Position> previousPlayerPositions;
    private Direction previousInsertionSide;
    private int previousInsertionIndex;
    private boolean previousTileInserted;

    public InsertTileCommand(LabyrinthGame game, Direction side, int index) {
        this.game = game;
        this.side = side;
        this.index = index;
    }

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
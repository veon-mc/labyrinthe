package g64889.dev3.labyrinthe.model;

import java.util.List;

public class MovePlayerCommand implements Command {
    private final LabyrinthGame game;
    private final int targetRow;
    private final int targetCol;

    private List<Position> previousPlayerPositions;
    private List<Integer> previousObjectiveIndices;
    private int previousCurrentPlayerIndex;
    private boolean previousTileInserted;
    private boolean previousGameOver;
    private Player previousWinner;

    public MovePlayerCommand(LabyrinthGame game, int targetRow, int targetCol) {
        this.game = game;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
    }

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
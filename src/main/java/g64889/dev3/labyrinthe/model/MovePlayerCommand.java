package g64889.dev3.labyrinthe.model;

public class MovePlayerCommand implements Command {
    private final LabyrinthGame game;
    private final int targetRow;
    private final int targetCol;
    private Position previousPosition;
    private int previousPlayerIndex;
    private boolean previousTileInserted;
    private boolean objectiveWasCompleted;
    private int previousObjectiveIndex;

    public MovePlayerCommand(LabyrinthGame game, int targetRow, int targetCol) {
        this.game = game;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
    }

    @Override
    public void execute() {
        Player current = game.getCurrentPlayer();
        previousPosition = current.getCurrentPosition();
        previousPlayerIndex = game.getCurrentPlayerIndex();
        previousTileInserted = game.isTileInserted();
        previousObjectiveIndex = current.getCurrentObjectiveIndex();

        game.movePlayer(targetRow, targetCol);

        objectiveWasCompleted = (current.getCurrentObjectiveIndex() > previousObjectiveIndex);
    }

    @Override
    public void undo() {
        game.setCurrentPlayerIndex(previousPlayerIndex);
        game.setTileInserted(previousTileInserted);

        Player current = game.getCurrentPlayer();
        current.setCurrentPosition(previousPosition);

        if (objectiveWasCompleted) {
            current.setCurrentObjectiveIndex(previousObjectiveIndex);
        }
    }
}

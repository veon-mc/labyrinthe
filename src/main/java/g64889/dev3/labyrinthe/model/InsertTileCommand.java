package g64889.dev3.labyrinthe.model;

public class InsertTileCommand implements Command {
    private final LabyrinthGame game;
    private final Direction side;
    private final int index;
    private Tile previousSpareTile;
    private Direction previousInsertionSide;
    private int previousInsertionIndex;

    public InsertTileCommand(LabyrinthGame game, Direction side, int index) {
        this.game = game;
        this.side = side;
        this.index = index;
    }

    @Override
    public void execute() {
        previousSpareTile = game.getSpareTile();
        previousInsertionSide = game.getLastInsertionSide();
        previousInsertionIndex = game.getLastInsertionIndex();

        game.insertTile(side, index);
    }

    @Override
    public void undo() {
        Direction oppositeSide = getOppositeSide(side);
        game.insertTile(oppositeSide, index);
        game.setLastInsertion(previousInsertionSide, previousInsertionIndex);
    }

    private Direction getOppositeSide(Direction dir) {
        return switch (dir) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
        };
    }
}

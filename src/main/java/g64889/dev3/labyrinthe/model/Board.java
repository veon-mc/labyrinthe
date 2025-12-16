package g64889.dev3.labyrinthe.model;

import g64889.dev3.labyrinthe.model.enumeration.Direction;

/**
 * Represents the game board of the Labyrinth.
 * <p>
 * The board is a fixed-size square grid (7x7) of {@link Tile} objects
 * and contains one spare tile that can be inserted from the edges.
 * <p>
 * This class is responsible only for board structure and tile shifting;
 * it does not contain any game rules or player logic.
 */
public class Board {

    /** Size of the board (7x7). */
    private final int size = 7;

    /** Grid of tiles representing the board. */
    private Tile[][] grid;

    /** Spare tile that can be inserted into the board. */
    private Tile spareTile;

    /**
     * Creates a new board with an initial grid and a spare tile.
     *
     * @param initialGrid the initial grid of tiles
     * @param spareTile   the spare tile
     */
    public Board(Tile[][] initialGrid, Tile spareTile) {
        this.grid = initialGrid;
        this.spareTile = spareTile;
    }

    /**
     * Returns the size of the board.
     *
     * @return the board size (7)
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the tile at the given position.
     *
     * @param pos the position on the board
     * @return the tile at the specified position
     */
    public Tile getTileAt(Position pos) {
        return grid[pos.getRow()][pos.getCol()];
    }

    /**
     * Returns the current spare tile.
     *
     * @return the spare tile
     */
    public Tile getSpareTile() {
        return spareTile;
    }

    /**
     * Inserts the spare tile into the board from a given side and index.
     * <p>
     * Only odd indices are allowed, and the index must be within board bounds.
     * The tile ejected on the opposite side becomes the new spare tile.
     *
     * @param side  the side from which the tile is inserted
     * @param index the row or column index (must be odd)
     * @throws IllegalArgumentException if the index is invalid
     */
    public void insertSpareTile(Direction side, int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException(
                    "Index must be between 0 and " + (size - 1));
        }
        if (index % 2 == 0) {
            throw new IllegalArgumentException("Only odd indices are allowed.");
        }

        switch (side) {
            case WEST -> pushRowFromLeft(index);
            case EAST -> pushRowFromRight(index);
            case NORTH -> pushColumnFromTop(index);
            case SOUTH -> pushColumnFromBottom(index);
        }
    }

    /**
     * Pushes a row from the left side.
     *
     * @param row the row index
     */
    private void pushRowFromLeft(int row) {
        Tile ejected = grid[row][size - 1];
        for (int col = size - 1; col > 0; col--) {
            grid[row][col] = grid[row][col - 1];
        }
        grid[row][0] = spareTile;
        spareTile = ejected;
    }

    /**
     * Pushes a row from the right side.
     *
     * @param row the row index
     */
    private void pushRowFromRight(int row) {
        Tile ejected = grid[row][0];
        for (int col = 0; col < size - 1; col++) {
            grid[row][col] = grid[row][col + 1];
        }
        grid[row][size - 1] = spareTile;
        spareTile = ejected;
    }

    /**
     * Pushes a column from the top.
     *
     * @param col the column index
     */
    private void pushColumnFromTop(int col) {
        Tile ejected = grid[size - 1][col];
        for (int row = size - 1; row > 0; row--) {
            grid[row][col] = grid[row - 1][col];
        }
        grid[0][col] = spareTile;
        spareTile = ejected;
    }

    /**
     * Pushes a column from the bottom.
     *
     * @param col the column index
     */
    private void pushColumnFromBottom(int col) {
        Tile ejected = grid[0][col];
        for (int row = 0; row < size - 1; row++) {
            grid[row][col] = grid[row + 1][col];
        }
        grid[size - 1][col] = spareTile;
        spareTile = ejected;
    }

    /**
     * Replaces the entire grid.
     * <p>
     * This method is mainly used for state restoration (undo/redo).
     *
     * @param grid the new grid
     */
    public void setGrid(Tile[][] grid) {
        this.grid = grid;
    }

    /**
     * Sets the spare tile.
     * <p>
     * This method is mainly used for state restoration (undo/redo).
     *
     * @param tile the new spare tile
     */
    public void setSpareTile(Tile tile) {
        this.spareTile = tile;
    }
}
package g64889.dev3.labyrinthe.model;

/**
 * Represents a position on the game board.
 * <p>
 * A position is defined by a row and a column index.
 * This class is immutable and can safely be used
 * as a key in collections.
 */
public final class Position {

    /** Row index of the position. */
    private final int row;

    /** Column index of the position. */
    private final int col;

    /**
     * Creates a new position.
     *
     * @param row the row index
     * @param col the column index
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /** @return the row index. */
    public int getRow() { return row; }

    /** @return the column index. */
    public int getCol() { return col; }

    /**
     * Compares this position with another object.
     *
     * @param o the object to compare
     * @return true if both positions have the same row and column
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    /**
     * Computes the hash code of this position.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}
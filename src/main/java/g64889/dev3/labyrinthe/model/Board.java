package g64889.dev3.labyrinthe.model;

public class Board {

    private int size = 7;
    private Tile[][] grid;
    private Tile spareTile;

    public Board(Tile[][] initialGrid, Tile spareTile) {
        this.grid = initialGrid;
        this.spareTile = spareTile;
    }

    public int getSize() {
        return size;
    }

    public Tile getTileAt(Position pos) {
        return grid[pos.getRow()][pos.getCol()];
    }

    public Tile getSpareTile() {
        return spareTile;
    }

    public void insertSpareTile(Direction side, int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Index must be between 0 and " + (size));
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

    private void pushRowFromLeft(int row) {
        Tile ejected = grid[row][size - 1];
        for (int col = size - 1; col > 0; col--) {
            grid[row][col] = grid[row][col - 1];
        }
        grid[row][0] = spareTile;
        spareTile = ejected;
    }

    private void pushRowFromRight(int row) {
        Tile ejected = grid[row][0];
        for (int col = 0; col < size - 1; col++) {
            grid[row][col] = grid[row][col + 1];
        }
        grid[row][size - 1] = spareTile;
        spareTile = ejected;
    }

    private void pushColumnFromTop(int col) {
        Tile ejected = grid[size - 1][col];
        for (int row = size - 1; row > 0; row--) {
            grid[row][col] = grid[row - 1][col];
        }
        grid[0][col] = spareTile;
        spareTile = ejected;
    }

    private void pushColumnFromBottom(int col) {
        Tile ejected = grid[0][col];
        for (int row = 0; row < size - 1; row++) {
            grid[row][col] = grid[row + 1][col];
        }
        grid[size - 1][col] = spareTile;
        spareTile = ejected;
    }

    public void setGrid(Tile[][] grid) {
        this.grid = grid;
    }

    public void setSpareTile(Tile tile) {
        this.spareTile = tile;
    }
}

package g64889.dev3.labyrinthe.model;

import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.enumeration.TileType;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private static Tile tile(String id) {
        return new Tile(
                TileType.I,
                EnumSet.of(Direction.NORTH, Direction.SOUTH),
                false,
                new Objective(id) // sert juste à identifier la tuile
        );
    }

    private static Tile[][] grid7() {
        Tile[][] g = new Tile[7][7];
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                g[r][c] = tile("t_" + r + "_" + c);
            }
        }
        return g;
    }

    @Test
    void basicGetters_work() {
        Tile[][] g = grid7();
        Tile spare = tile("spare");
        Board board = new Board(g, spare);
        System.out.println(board);

        assertEquals(7, board.getSize());
        assertSame(g[2][4], board.getTileAt(new Position(2, 4)));
        assertSame(spare, board.getSpareTile());
    }

    @Test
    void insertSpareTile_throwsIfIndexOutOfRange() {
        Board board = new Board(grid7(), tile("spare"));

        assertThrows(IllegalArgumentException.class,
                () -> board.insertSpareTile(Direction.NORTH, -1));
        assertThrows(IllegalArgumentException.class,
                () -> board.insertSpareTile(Direction.NORTH, 7));
    }

    @Test
    void insertSpareTile_throwsIfIndexIsEven() {
        Board board = new Board(grid7(), tile("spare"));

        assertThrows(IllegalArgumentException.class,
                () -> board.insertSpareTile(Direction.WEST, 0));
        assertThrows(IllegalArgumentException.class,
                () -> board.insertSpareTile(Direction.WEST, 2));
        assertThrows(IllegalArgumentException.class,
                () -> board.insertSpareTile(Direction.WEST, 4));
        assertThrows(IllegalArgumentException.class,
                () -> board.insertSpareTile(Direction.WEST, 6));
    }

    @Test
    void insertFromWest_shiftsRowAndUpdatesSpareTile() {
        Tile[][] g = grid7();
        Tile spare = tile("spare");
        Board board = new Board(g, spare);

        int row = 1;
        Tile ejected = g[row][6];
        Tile old00 = g[row][0];

        board.insertSpareTile(Direction.WEST, row);

        // La spare rentre à gauche
        assertSame(spare, board.getTileAt(new Position(row, 0)));

        // Le reste est décalé d'une case (au moins on vérifie une)
        assertSame(old00, board.getTileAt(new Position(row, 1)));

        // La spare devient la tuile éjectée
        assertSame(ejected, board.getSpareTile());
    }
}
package g64889.dev3.labyrinthe.model;

import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.enumeration.TileType;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    void hasOpening_returnsTrueWhenDirectionIsPresent() {
        Tile t = new Tile(TileType.L, EnumSet.of(Direction.NORTH, Direction.EAST), false, null);

        assertTrue(t.hasOpening(Direction.NORTH));
        assertTrue(t.hasOpening(Direction.EAST));
        assertFalse(t.hasOpening(Direction.SOUTH));
        assertFalse(t.hasOpening(Direction.WEST));
    }

    @Test
    void rotate_rotatesOpeningsClockwise() {
        Tile t = new Tile(TileType.L, EnumSet.of(Direction.NORTH, Direction.EAST), false, null);

        t.rotate(); // N -> E, E -> S
        assertTrue(t.hasOpening(Direction.EAST));
        assertTrue(t.hasOpening(Direction.SOUTH));
        assertFalse(t.hasOpening(Direction.NORTH));
        assertFalse(t.hasOpening(Direction.WEST));

        t.rotate(); // E->S, S->W
        assertTrue(t.hasOpening(Direction.SOUTH));
        assertTrue(t.hasOpening(Direction.WEST));
        assertFalse(t.hasOpening(Direction.NORTH));
        assertFalse(t.hasOpening(Direction.EAST));

        t.rotate(); // S->W, W->N
        assertTrue(t.hasOpening(Direction.WEST));
        assertTrue(t.hasOpening(Direction.NORTH));

        t.rotate(); // back to original
        assertTrue(t.hasOpening(Direction.NORTH));
        assertTrue(t.hasOpening(Direction.EAST));
        assertFalse(t.hasOpening(Direction.SOUTH));
        assertFalse(t.hasOpening(Direction.WEST));
    }

    @Test
    void isFixed_returnsConstructorValue() {
        Tile fixed = new Tile(TileType.I, EnumSet.of(Direction.NORTH, Direction.SOUTH), true, null);
        Tile mobile = new Tile(TileType.I, EnumSet.of(Direction.NORTH, Direction.SOUTH), false, null);

        assertTrue(fixed.isFixed());
        assertFalse(mobile.isFixed());
    }

    @Test
    void hasObjective_trueOnlyWhenObjectiveIsNotNull() {
        Tile t1 = new Tile(TileType.I, EnumSet.of(Direction.NORTH, Direction.SOUTH), false, null);
        Tile t2 = new Tile(TileType.I, EnumSet.of(Direction.NORTH, Direction.SOUTH), false, new Objective("rat"));

        assertFalse(t1.hasObjective());
        assertTrue(t2.hasObjective());
    }

    @Test
    void copy_createsNewTileWithIndependentOpenings() {
        Tile original = new Tile(TileType.T, EnumSet.of(Direction.NORTH, Direction.EAST, Direction.WEST), false, new Objective("dragon"));
        Tile copy = original.copy();

        // Not the same instance
        assertNotSame(original, copy);

        // Same values
        assertEquals(original.getType(), copy.getType());
        assertEquals(original.isFixed(), copy.isFixed());
        assertSame(original.getObjective(), copy.getObjective()); // objective reference is shared (as in your code)
        assertEquals(original.getOpenings(), copy.getOpenings());

        // Rotate original, copy should stay the same because openings are copied
        EnumSet<Direction> beforeCopyOpenings = EnumSet.copyOf(copy.getOpenings());
        original.rotate();

        assertEquals(beforeCopyOpenings, copy.getOpenings());
        assertNotEquals(original.getOpenings(), copy.getOpenings());
    }
}
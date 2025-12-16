package g64889.dev3.labyrinthe.model;

import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.enumeration.TileType;

import java.util.EnumSet;

/**
 * Represents a tile of the Labyrinth board.
 * <p>
 * A tile has a type, a set of openings indicating allowed directions,
 * an optional objective, and can be fixed or movable.
 */
public class Tile {

    /** Type of the tile (L, T or I). */
    private TileType type;

    /** Open directions of the tile. */
    private EnumSet<Direction> openings;

    /** Indicates whether the tile is fixed on the board. */
    private boolean fixed;

    /** Optional objective placed on the tile. */
    private Objective objective;

    /**
     * Creates a new tile.
     *
     * @param type      the tile type
     * @param openings  the set of openings
     * @param fixed     true if the tile is fixed
     * @param objective the objective on the tile (or null if none)
     */
    public Tile(TileType type, EnumSet<Direction> openings, boolean fixed, Objective objective) {
        this.type = type;
        this.openings = openings;
        this.fixed = fixed;
        this.objective = objective;
    }

    /**
     * Checks whether the tile has an opening in the given direction.
     *
     * @param dir the direction to check
     * @return true if the tile has an opening in that direction
     */
    public boolean hasOpening(Direction dir) {
        return openings.contains(dir);
    }

    /**
     * Rotates the tile clockwise.
     * <p>
     * All openings are shifted accordingly.
     */
    public void rotate() {
        EnumSet<Direction> rotated = EnumSet.noneOf(Direction.class);

        for (Direction d : openings) {
            switch (d) {
                case NORTH -> rotated.add(Direction.EAST);
                case EAST  -> rotated.add(Direction.SOUTH);
                case SOUTH -> rotated.add(Direction.WEST);
                case WEST  -> rotated.add(Direction.NORTH);
            }
        }

        openings = rotated;
    }

    /**
     * Indicates whether the tile is fixed.
     *
     * @return true if the tile is fixed
     */
    public boolean isFixed() {
        return fixed;
    }

    /**
     * Returns the objective on this tile.
     *
     * @return the objective, or null if none
     */
    public Objective getObjective() {
        return objective;
    }

    /**
     * Indicates whether this tile has an objective.
     *
     * @return true if the tile contains an objective
     */
    public boolean hasObjective() {
        return objective != null;
    }

    /**
     * Creates a copy of this tile.
     *
     * @return a new tile with the same properties
     */
    public Tile copy() {
        return new Tile(
                this.type,
                EnumSet.copyOf(this.openings),
                this.isFixed(),
                this.getObjective()
        );
    }

    /**
     * Returns the tile type.
     *
     * @return the tile type
     */
    public TileType getType() {
        return type;
    }

    /**
     * Returns the openings of the tile.
     *
     * @return the set of openings
     */
    public EnumSet<Direction> getOpenings() {
        return openings;
    }
}
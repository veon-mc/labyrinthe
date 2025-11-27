package g64889.dev3.labyrinthe.model;

import java.util.EnumSet;
import java.util.Set;

public class Tile {

    private TileType type;
    private EnumSet<Direction> openings;
    private boolean fixed;
    private Objective objective;

    public Tile(TileType type, EnumSet<Direction> openings, boolean fixed, Objective objective) {
        this.type = type;
        this.openings = openings;
        this.fixed = fixed;
        this.objective = objective;
    }

    public boolean hasOpening(Direction dir) {
        return openings.contains(dir);
    }

    public void rotate() {
        EnumSet<Direction> rotated = EnumSet.noneOf(Direction.class);

        for (Direction d : openings) {
            switch (d) {
                case NORTH -> rotated.add(Direction.EAST);
                case EAST -> rotated.add(Direction.SOUTH);
                case SOUTH -> rotated.add(Direction.WEST);
                case WEST -> rotated.add(Direction.NORTH);
            }
        }

        openings = rotated;
    }

    public boolean isFixed() {
        return fixed;
    }

    public Objective getObjective() {
        return objective;
    }
}

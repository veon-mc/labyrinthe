package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.Tile;
import g64889.dev3.labyrinthe.model.enumeration.TileType;

/**
 * Utility class responsible for computing the spare tile rotation in the HUD.
 * <p>
 * This class contains the exact same rotation rules that were previously
 * inside {@link HudView#setSpareTile(Tile)}.
 */
public final class HudTileRotation {

    private HudTileRotation() {}

    /**
     * Computes the rotation of the spare tile image for the HUD.
     * <p>
     * <ul>
     *   <li>rotation from openings (I/L/T)</li>
     *   <li>general patch: L tile + objective</li>
     *   <li>extra patch depending on objective name (rat / scarabée / hibou)</li>
     * </ul>
     *
     * @param tile the spare tile
     * @return rotation angle in degrees (0..359)
     */
    public static double spareRotation(Tile tile) {
        if (tile == null) return 0;

        double rot = rotationFromOpenings(tile);

        // Patch general L + objective
        if (tile.getType() == TileType.L && tile.getObjective() != null) {
            rot = (rot + 90) % 360;
        }

        // Patch per objective
        if (tile.getObjective() != null) {
            String key = tile.getObjective().getName();

            switch (key) {
                case "rat" -> rot = (rot + 90) % 360;
                case "scarabée", "hibou" -> rot = (rot + 180) % 360;
                default -> { /* nothing */ }
            }
        }

        return rot;
    }

    /**
     * Computes the base rotation from a tile openings set.
     * @param tile the tile
     * @return base rotation in degrees
     */
    private static double rotationFromOpenings(Tile tile) {
        var o = tile.getOpenings();

        return switch (tile.getType()) {
            case I -> o.contains(Direction.EAST) ? 90 : 0;

            case L -> {
                if (o.contains(Direction.NORTH) && o.contains(Direction.EAST)) yield 0;
                if (o.contains(Direction.EAST) && o.contains(Direction.SOUTH)) yield 90;
                if (o.contains(Direction.SOUTH) && o.contains(Direction.WEST)) yield 180;
                yield 270;
            }

            case T -> {
                // 0° : T closed SOUTH (open N,E,W)
                if (!o.contains(Direction.SOUTH)) yield 0;
                if (!o.contains(Direction.WEST))  yield 90;
                if (!o.contains(Direction.NORTH)) yield 180;
                yield 270; // closed EAST
            }
        };
    }
}
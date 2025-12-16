package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;
import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.enumeration.TileType;

import java.util.EnumSet;

/**
 * Utility class that computes the rotation of tile images.
 * This class contains only view-related rules (image orientation).
 */
public final class TileRotation {

    private TileRotation() {}

    /**
     * Computes the final rotation for a tile displayed on the board.
     * This method contains the exact same rules that were previously in TileView.
     *
     * @param tile the tile model
     * @param pos  the tile position on the board
     * @return rotation in degrees (0..359)
     */
    public static double boardRotation(Tile tile, Position pos) {
        double rot;

        if (tile.isFixed()) {
            rot = fixedRotation(pos);
        } else {
            EnumSet<Direction> o = tile.getOpenings();

            rot = switch (tile.getType()) {
                case I -> o.contains(Direction.EAST) ? 90 : 0;

                case L -> {
                    if (o.contains(Direction.NORTH) && o.contains(Direction.EAST)) yield 0;
                    if (o.contains(Direction.EAST) && o.contains(Direction.SOUTH)) yield 90;
                    if (o.contains(Direction.SOUTH) && o.contains(Direction.WEST)) yield 180;
                    yield 270;
                }

                case T -> {
                    if (!o.contains(Direction.SOUTH)) yield 0;
                    if (!o.contains(Direction.WEST))  yield 90;
                    if (!o.contains(Direction.NORTH)) yield 180;
                    yield 270;
                }
            };
        }

        // objective-specific correction (same as before)
        if (tile.getObjective() != null) {
            rot = (rot + Images.correctionDegrees(tile.getObjective())) % 360;
        }

        // extra offset for non-fixed T tiles with goal (same as before)
        if (!tile.isFixed() && tile.getType() == TileType.T && tile.getObjective() != null) {
            rot = (rot + Images.tGoalOffsetDegrees(tile.getObjective())) % 360;
        }

        return rot;
    }

    /**
     * Rotation for fixed tiles depending on their position (same as before).
     */
    private static double fixedRotation(Position pos) {
        int r = pos.getRow();
        int c = pos.getCol();

        if ((r == 0 && c == 0) || (r == 0 && c == 6) || (r == 6 && c == 0) || (r == 6 && c == 6)) {
            return 0;
        }

        if (r == 0 && (c == 2 || c == 4)) return 0;
        if (c == 0 && (r == 2 || r == 4)) return 90;
        if (r == 6 && (c == 2 || c == 4)) return 180;
        if (c == 6 && (r == 2 || r == 4)) return 270;

        if (r == 2 && c == 2) return 90;
        if (r == 2 && c == 4) return 0;
        if (r == 4 && c == 4) return 270;
        if (r == 4 && c == 2) return 180;

        return 0;
    }
}
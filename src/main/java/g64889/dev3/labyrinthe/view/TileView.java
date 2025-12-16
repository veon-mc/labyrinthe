package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;

import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.enumeration.TileType;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.EnumSet;
import java.util.List;

/**
 * JavaFX view used to display a single tile on the board.
 * <p>
 * This component renders:
 * <ul>
 *   <li>the base tile image (corners, fixed tiles, mobile tiles)</li>
 *   <li>the correct rotation depending on openings and fixed position</li>
 *   <li>one or more player markers on top of the tile</li>
 * </ul>
 * <p>
 * Rotation rules:
 * <ul>
 *   <li>fixed tiles use a predefined orientation based on their position</li>
 *   <li>mobile tiles compute rotation from their openings</li>
 *   <li>objective images may require an extra correction angle</li>
 *   <li>some mobile T tiles with a goal apply an additional offset</li>
 * </ul>
 */
public class TileView extends StackPane {

    /** Default size of a tile cell (in pixels). */
    private static final double TILE_SIZE = 80;

    /**
     * Creates a tile view for the given tile and position.
     * <p>
     * The view shows the tile image with the computed rotation and overlays
     * player markers if some players are currently located on this tile.
     *
     * @param tile    the tile model to display
     * @param pos     the position of the tile on the board
     * @param players the list of players used to display player markers
     */
    public TileView(Tile tile, Position pos, List<Player> players) {
        setPrefSize(TILE_SIZE, TILE_SIZE);
        setMinSize(TILE_SIZE, TILE_SIZE);
        setMaxSize(TILE_SIZE, TILE_SIZE);
        setAlignment(Pos.CENTER);

        ImageView base = new ImageView(Images.baseTile(tile, pos));
        base.setFitWidth(TILE_SIZE);
        base.setFitHeight(TILE_SIZE);
        base.setPreserveRatio(false);

        double rot = rotationDegrees(tile, pos);

        // Extra offset for non-fixed T tiles that have a goal image
        if (!tile.isFixed() && tile.getType() == TileType.T && tile.getObjective() != null) {
            rot = (rot + Images.tGoalOffsetDegrees(tile.getObjective())) % 360;
        }

        base.setRotate(rot);

        getChildren().add(base);

        // Add player markers on top of the tile
        for (Player p : players) {
            if (p.getCurrentPosition().equals(pos)) {
                getChildren().add(new PlayerView(p));
            }
        }
    }

    /**
     * Computes the rotation angle for the tile image.
     * <p>
     * Fixed tiles use predefined rotations. Mobile tiles compute the rotation
     * from their openings. If the tile has an objective, an additional
     * correction angle is applied.
     *
     * @param tile the tile model
     * @param pos  the tile position
     * @return the rotation angle in degrees (0..359)
     */
    private double rotationDegrees(Tile tile, Position pos) {

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

        // Objective-specific correction
        if (tile.getObjective() != null) {
            rot = (rot + Images.correctionDegrees(tile.getObjective())) % 360;
        }

        return rot;
    }

    /**
     * Returns the predefined rotation for fixed tiles depending on their position.
     * <p>
     * This method encodes the expected orientation of the fixed labyrinth tiles
     * (corners, borders and central fixed tiles).
     *
     * @param pos the fixed tile position
     * @return the rotation angle in degrees
     */
    private double fixedRotation(Position pos) {
        int r = pos.getRow();
        int c = pos.getCol();

        // corners are already oriented by their image
        if ((r == 0 && c == 0) || (r == 0 && c == 6) || (r == 6 && c == 0) || (r == 6 && c == 6)) {
            return 0;
        }

        // border fixed T tiles
        if (r == 0 && (c == 2 || c == 4)) return 0;
        if (c == 0 && (r == 2 || r == 4)) return 90;
        if (r == 6 && (c == 2 || c == 4)) return 180;
        if (c == 6 && (r == 2 || r == 4)) return 270;

        // central fixed T tiles
        if (r == 2 && c == 2) return 90;
        if (r == 2 && c == 4) return 0;
        if (r == 4 && c == 4) return 270;
        if (r == 4 && c == 2) return 180;

        return 0;
    }
}
package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.EnumSet;
import java.util.List;

public class TileView extends StackPane {

    private static final double TILE_SIZE = 80;

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
        if (tile.getObjective() != null) {
            rot = (rot + Images.fixedGoalOffsetDegrees(tile.getObjective())) % 360;
        }
        base.setRotate(rot);
        getChildren().add(base);


        for (Player p : players) {
            if (p.getCurrentPosition().equals(pos)) {
                getChildren().add(new PlayerView(p));
            }
        }
    }

    private double rotationDegrees(Tile tile, Position pos) {

        if (tile.isFixed()) {
            return fixedRotation(pos);
        }

        EnumSet<Direction> o = tile.getOpenings();

        return switch (tile.getType()) {
            case I -> o.contains(Direction.EAST) ? 90 : 0;

            case L -> {
                if (o.contains(Direction.NORTH) && o.contains(Direction.EAST)) yield 0;
                if (o.contains(Direction.EAST) && o.contains(Direction.SOUTH)) yield 90;
                if (o.contains(Direction.SOUTH) && o.contains(Direction.WEST)) yield 180;
                yield 270;
            }

            case T -> {
                if (!o.contains(Direction.NORTH)) yield 0;
                if (!o.contains(Direction.WEST)) yield 90;
                if (!o.contains(Direction.SOUTH)) yield 180;
                yield 270;
            }
        };
    }

    private double fixedRotation(Position pos) {
        int r = pos.getRow();
        int c = pos.getCol();

        if ((r == 0 && c == 0) || (r == 0 && c == 6) || (r == 6 && c == 0) || (r == 6 && c == 6)) {
            return 0;
        }

        if (r == 0 && (c == 2 || c == 4)) return 0;    // manque NORTH
        if (c == 0 && (r == 2 || r == 4)) return 90;   // manque WEST
        if (r == 6 && (c == 2 || c == 4)) return 180;  // manque SOUTH
        if (c == 6 && (r == 2 || r == 4)) return 270;  // manque EAST

        if (r == 2 && c == 2) return 90;    // manque WEST
        if (r == 2 && c == 4) return 0;     // manque NORTH
        if (r == 4 && c == 4) return 270;   // manque EAST
        if (r == 4 && c == 2) return 180;   // manque SOUTH

        return 0;
    }
}
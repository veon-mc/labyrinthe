package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.List;

/**
 * JavaFX view used to display a single tile on the board.
 * <p>
 * This class displays the tile image with the correct rotation
 * and overlays player markers when players are on the same position.
 */
public class TileView extends StackPane {

    private static final double TILE_SIZE = 80;

    /**
     * Creates a tile view for the given tile and position.
     *
     * @param tile    the tile to display
     * @param pos     the tile position on the board
     * @param players the list of players (used to display player markers)
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

        // 🔁 EXACT same rotation logic as before, just delegated
        double rot = TileRotation.boardRotation(tile, pos);
        base.setRotate(rot);

        getChildren().add(base);

        // Player markers
        for (Player p : players) {
            if (p.getCurrentPosition().equals(pos)) {
                getChildren().add(new PlayerView(p));
            }
        }
    }
}
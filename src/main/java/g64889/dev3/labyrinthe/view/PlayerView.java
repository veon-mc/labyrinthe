package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.Player;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Visual representation of a player on the board.
 * <p>
 * This view displays a small colored circle corresponding
 * to the player's color. The color is derived directly
 * from the {@link Player} model.
 */
public class PlayerView extends Circle {

    /**
     * Creates a player view for the given player.
     *
     * @param player the player to represent
     */
    public PlayerView(Player player) {
        super(8);
        setFill(player.getColor().toJavaFXColor());
        setStroke(Color.BLACK);
    }
}
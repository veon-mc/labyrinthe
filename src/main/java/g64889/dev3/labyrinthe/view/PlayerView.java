package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.Player;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PlayerView extends Circle {

    public PlayerView(Player player) {
        super(8);

        setFill(switch (player.getColor()) {
            case RED -> Color.RED;
            case BLUE -> Color.DODGERBLUE;
            case GREEN -> Color.LIMEGREEN;
            case YELLOW -> Color.GOLD;
        });

        setStroke(Color.BLACK);
    }
}

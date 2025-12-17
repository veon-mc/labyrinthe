package g64889.dev3.labyrinthe.model.enumeration;

import javafx.scene.paint.Color;

/**
 * Represents the available colors for players.
 * <p>
 * Each player is assigned a unique color to distinguish them
 * on the board and in the user interface.
 */
public enum PlayerColor {

    /** Blue player color. */
    BLUE,

    /** Red player color. */
    RED,

    /** Yellow player color. */
    YELLOW,

    /** Green player color. */
    GREEN;

    public Color toJavaFXColor() {
        return switch (this) {
            case RED -> Color.RED;
            case BLUE -> Color.DODGERBLUE;
            case GREEN -> Color.LIMEGREEN;
            case YELLOW -> Color.GOLD;
        };
    }
}
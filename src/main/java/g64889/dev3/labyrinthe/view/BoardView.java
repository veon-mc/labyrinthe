package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.function.BiConsumer;

public class BoardView extends GridPane {

    private BiConsumer<Integer, Integer> onCellClicked;

    public BoardView() {
        setHgap(2);
        setVgap(2);
    }

    public void setOnCellClicked(BiConsumer<Integer, Integer> handler) {
        this.onCellClicked = handler;
    }

    public void update(Board board, List<Player> players) {
        getChildren().clear();

        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                Position pos = new Position(r, c);
                Tile tile = board.getTileAt(pos);

                TileView cell = new TileView(tile, pos, players);

                final int row = r;
                final int col = c;
                cell.setOnMouseClicked(e -> {
                    if (onCellClicked != null) onCellClicked.accept(row, col);
                });

                add(cell, c, r);
            }
        }
    }
}
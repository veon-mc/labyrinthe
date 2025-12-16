package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * JavaFX view responsible for displaying the game board.
 * <p>
 * This view renders a grid of {@link TileView} objects corresponding
 * to the current state of the {@link Board}. It also handles mouse
 * interactions on cells and forwards them to the controller.
 */
public class BoardView extends GridPane {

    /**
     * Handler called when a cell is clicked.
     * <p>
     * The two integers correspond to the row and column of the clicked cell.
     */
    private BiConsumer<Integer, Integer> onCellClicked;

    /**
     * Creates a new board view.
     * <p>
     * Sets spacing between cells.
     */
    public BoardView() {
        setHgap(2);
        setVgap(2);
    }

    /**
     * Sets the handler to be called when a cell is clicked.
     *
     * @param handler a consumer receiving the row and column of the clicked cell
     */
    public void setOnCellClicked(BiConsumer<Integer, Integer> handler) {
        this.onCellClicked = handler;
    }

    /**
     * Updates the board view to reflect the current game state.
     * <p>
     * All existing cells are cleared and recreated based on the
     * provided board and player positions.
     *
     * @param board   the game board
     * @param players the list of players
     */
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
                    if (onCellClicked != null) {
                        onCellClicked.accept(row, col);
                    }
                });

                add(cell, c, r);
            }
        }
    }
}
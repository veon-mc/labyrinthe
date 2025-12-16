package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;
import g64889.dev3.labyrinthe.model.Observer;
import g64889.dev3.labyrinthe.model.enumeration.Direction;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Main JavaFX view of the Labyrinth game.
 * <p>
 * This view is responsible for assembling the UI layout:
 * <ul>
 *   <li>the board grid in the center</li>
 *   <li>insertion arrows around the board</li>
 *   <li>the HUD on the right side</li>
 * </ul>
 * <p>
 * The view observes the {@link LabyrinthGame} model and refreshes the UI
 * whenever the model changes.
 */
public class MainView extends BorderPane implements Observer {

    /** The game model observed by this view. */
    private LabyrinthGame game;

    /** View used to render the board tiles. */
    private final BoardView boardView;

    /** HUD view (spare tile, objective, buttons, players status). */
    private final HudView hudView;

    /** Manages insertion arrow nodes and insertion events. */
    private final InsertionPadView insertPad = new InsertionPadView();

    /** Keeps a reference to arrow buttons (useful if needed for future UI changes). */
    private final List<Button> arrowButtons = new ArrayList<>();

    /**
     * Creates the main view and builds the initial layout.
     * <p>
     * The board is placed inside a {@link GridPane} with one extra row/column
     * around it to display insertion arrows.
     */
    public MainView() {
        boardView = new BoardView();
        hudView = new HudView();

        GridPane boardGrid = new GridPane();

        final double TILE = 80;

        boardGrid.getColumnConstraints().add(new ColumnConstraints(TILE)); // left margin
        for (int i = 0; i < 7; i++) {
            boardGrid.getColumnConstraints().add(new ColumnConstraints(TILE));
        }
        boardGrid.getColumnConstraints().add(new ColumnConstraints(TILE)); // right margin

        boardGrid.getRowConstraints().add(new RowConstraints(TILE)); // top margin
        for (int i = 0; i < 7; i++) {
            boardGrid.getRowConstraints().add(new RowConstraints(TILE));
        }
        boardGrid.getRowConstraints().add(new RowConstraints(TILE)); // bottom margin

        boardGrid.add(boardView, 1, 1, 7, 7);

        insertArrows(boardGrid);

        setCenter(boardGrid);
        setRight(hudView);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #1e1e1e;");
    }

    /**
     * Binds this view to the given game model and registers as an observer.
     *
     * @param game the game model
     */
    public void bindGame(LabyrinthGame game) {
        this.game = game;
        game.addObserver(this);
    }

    /**
     * Inserts the arrow buttons around the board.
     * <p>
     * Valid insertion indices are 1, 3 and 5 (for both rows and columns),
     * and the arrows are placed at the outer border of the grid.
     *
     * @param g the grid pane hosting the board and arrows
     */
    private void insertArrows(GridPane g) {

        // NORTH
        for (int c : new int[]{1, 3, 5}) {
            g.add(insertPadButton(Direction.NORTH, c), c + 1, 0);
        }

        // SOUTH
        for (int c : new int[]{1, 3, 5}) {
            g.add(insertPadButton(Direction.SOUTH, c), c + 1, 8);
        }

        // WEST
        for (int r : new int[]{1, 3, 5}) {
            g.add(insertPadButton(Direction.WEST, r), 0, r + 1);
        }

        // EAST
        for (int r : new int[]{1, 3, 5}) {
            g.add(insertPadButton(Direction.EAST, r), 8, r + 1);
        }
    }

    /**
     * Creates a button representing an insertion arrow.
     * <p>
     * Clicking the button triggers an insertion request through
     * {@link InsertionPadView#fireInsert(Direction, int)}.
     * The created button is also registered inside {@link InsertionPadView}
     * so it can be enabled/disabled depending on the game state.
     *
     * @param side  insertion side
     * @param index insertion index (1, 3 or 5)
     * @return the created arrow button
     */
    private Button insertPadButton(Direction side, int index) {
        Button b = new Button(
                switch (side) {
                    case NORTH -> "↓";
                    case SOUTH -> "↑";
                    case WEST  -> "→";
                    case EAST  -> "←";
                }
        );

        b.setPrefSize(80, 80);
        b.setStyle("""
    -fx-background-color: #2a2a2a;
    -fx-text-fill: white;
    -fx-font-size: 18px;
""");

        addArrowHoverEffect(b);

        // click -> insertion request
        b.setOnAction(e -> insertPad.fireInsert(side, index));

        // register arrow in InsertionPadView
        insertPad.registerArrow(side, index, b);

        arrowButtons.add(b);
        return b;
    }

    /**
     * Refreshes the UI when the model changes.
     * <p>
     * Updates the board tiles, spare tile image, current objective,
     * player info, phase label, and enables/disables controls.
     */
    @Override
    public void update() {
        if (game == null || game.getBoard() == null) return;

        // board
        boardView.update(game.getBoard(), game.getPlayers());

        // spare tile
        hudView.setSpareTile(game.getSpareTile());

        // current player + objective + players panel
        Player current = game.getCurrentPlayer();
        if (current != null) {
            ObjectiveCard card = current.getCurrentObjective();
            hudView.setObjective(card == null ? null : card.getObjective());
            hudView.setTurnAndPlayers(current, game.getPlayers());
        }

        // phase
        boolean inserted = game.isTileInserted();
        hudView.setPhase(inserted);

        // controls
        insertPad.setDisabledAll(inserted);

        hudView.setUndoDisabled(!game.canUndo());
        hudView.setRedoDisabled(!game.canRedo());
        hudView.setRotateDisabled(inserted);
    }

    /**
     * Returns the board view component.
     *
     * @return the board view
     */
    public BoardView getBoardView() {
        return boardView;
    }

    /**
     * Returns the HUD view component.
     *
     * @return the HUD view
     */
    public HudView getHudView() {
        return hudView;
    }

    /**
     * Returns the insertion pad used to manage arrow nodes.
     *
     * @return the insertion pad view
     */
    public InsertionPadView getInsertionPad() {
        return insertPad;
    }

    /**
     * Adds a simple hover effect to an arrow button.
     *
     * @param b the arrow button
     */
    private void addArrowHoverEffect(Button b) {
        String normal = """
        -fx-background-color: #2a2a2a;
        -fx-text-fill: white;
        -fx-font-size: 18px;
    """;

        String hover = """
        -fx-background-color: #3a3a3a;
        -fx-text-fill: white;
        -fx-font-size: 18px;
        -fx-cursor: hand;
    """;

        b.setStyle(normal);

        b.setOnMouseEntered(e -> {
            if (!b.isDisabled()) b.setStyle(hover);
        });

        b.setOnMouseExited(e -> b.setStyle(normal));
    }
}
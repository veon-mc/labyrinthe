package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;
import g64889.dev3.labyrinthe.model.Observer;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class MainView extends BorderPane implements Observer {

    private LabyrinthGame game;

    private final BoardView boardView;
    private final HudView hudView;
    private final InsertionPadView insertPad = new InsertionPadView();

    public MainView() {
        boardView = new BoardView();
        hudView = new HudView();

        GridPane boardGrid = new GridPane();

        final double TILE = 80;

        boardGrid.getColumnConstraints().add(new ColumnConstraints(TILE)); // gauche
        for (int i = 0; i < 7; i++)
            boardGrid.getColumnConstraints().add(new ColumnConstraints(TILE));
        boardGrid.getColumnConstraints().add(new ColumnConstraints(TILE)); // droite

        boardGrid.getRowConstraints().add(new RowConstraints(TILE)); // haut
        for (int i = 0; i < 7; i++)
            boardGrid.getRowConstraints().add(new RowConstraints(TILE));
        boardGrid.getRowConstraints().add(new RowConstraints(TILE)); // bas

        boardGrid.add(boardView, 1, 1, 7, 7);

        insertArrows(boardGrid);

        setCenter(boardGrid);
        setRight(hudView);
    }

    public void bindGame(LabyrinthGame game) {
        this.game = game;
        game.addObserver(this);
    }

    private void insertArrows(GridPane g) {

        // NORD
        for (int c : new int[]{1, 3, 5}) {
            g.add(insertPadButton(Direction.NORTH, c), c + 1, 0);
        }

        // SUD
        for (int c : new int[]{1, 3, 5}) {
            g.add(insertPadButton(Direction.SOUTH, c), c + 1, 8);
        }

        // OUEST
        for (int r : new int[]{1, 3, 5}) {
            g.add(insertPadButton(Direction.WEST, r), 0, r + 1);
        }

        // EST
        for (int r : new int[]{1, 3, 5}) {
            g.add(insertPadButton(Direction.EAST, r), 8, r + 1);
        }
    }

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
        b.setOnAction(e -> insertPad.fireInsert(side, index));
        return b;
    }

    @Override
    public void update() {
        if (game == null || game.getBoard() == null) return;

        boardView.update(game.getBoard(), game.getPlayers());
        hudView.setSpareTile(game.getSpareTile());

        Player current = game.getCurrentPlayer();
        if (current != null && !current.hasCompletedAllObjectives()) {
            ObjectiveCard card = current.getCurrentObjective();
            if (card != null) {
                hudView.setObjective(card.getObjective());
                hudView.setTurnAndPlayers(game.getCurrentPlayer(), game.getPlayers());
                hudView.setUndoDisabled(!game.canUndo());
                hudView.setRedoDisabled(!game.canRedo());
            }
        }
        insertPad.setDisabledAll(game.isTileInserted());
    }

    public BoardView getBoardView() {
        return boardView;
    }

    public HudView getHudView() {
        return hudView;
    }

    public InsertionPadView getInsertionPad() {
        return insertPad;
    }
}
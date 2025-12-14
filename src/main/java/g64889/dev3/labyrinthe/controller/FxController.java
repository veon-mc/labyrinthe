package g64889.dev3.labyrinthe.controller;

import g64889.dev3.labyrinthe.model.*;
import g64889.dev3.labyrinthe.view.MainView;

public class FxController {

    private final LabyrinthGame game;
    private final MainView view;

    public FxController(LabyrinthGame game, MainView view) {
        this.game = game;
        this.view = view;

        view.bindGame(game);
        bindHandlers();
    }

    private void bindHandlers() {

        view.getHudView().onUndo(() -> {
            if (game.canUndo()) game.undo();
        });

        view.getHudView().onRedo(() -> {
            if (game.canRedo()) game.redo();
        });

        view.getHudView().onRotate(() -> System.out.println("ROTATE CLICK"));

        view.getHudView().onRotate(() -> {
            game.rotateSpareTile();
        });

        view.getInsertionPad().setOnInsert((side, index) -> {
            System.out.println("CTRL INSERT " + side + " " + index);

            if (game.isTileInserted()) return;

            try {
                game.doInsertTile(side, index);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        view.getBoardView().setOnCellClicked((row, col) -> {
            if (!game.isTileInserted()) return;

            try {
                game.doMovePlayer(row, col);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void startGame(int players) {
        game.startGame(players);
    }
}
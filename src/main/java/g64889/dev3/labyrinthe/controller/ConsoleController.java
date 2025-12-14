package g64889.dev3.labyrinthe.controller;

import java.util.ArrayList;
import java.util.List;
import g64889.dev3.labyrinthe.controller.strategy.PlayerStrategy;
import g64889.dev3.labyrinthe.controller.strategy.RandomStrategy;
import g64889.dev3.labyrinthe.model.*;
import g64889.dev3.labyrinthe.console.ConsoleView;


public class ConsoleController {

    private final LabyrinthGame game;
    private final ConsoleView view;
    private List<PlayerStrategy> strategies;

    public ConsoleController(LabyrinthGame game, ConsoleView view) {
        this.game = game;
        this.view = view;
    }

    public void start() {
        view.showWelcome();

        int nbPlayers = view.askNumberOfPlayers();
        game.startGame(nbPlayers);
        strategies = new ArrayList<>();

        strategies.add(null);

        for (int i = 1; i < nbPlayers; i++) {
            strategies.add(new RandomStrategy());
        }

        view.showGameStart();

        while (!game.isGameOver()) {
            playTurn();
        }

        view.displayWinner(game.getWinner());
    }

    private void playTurn() {
        Player current = game.getCurrentPlayer();
        PlayerStrategy strategy = strategies.get(current.getId() - 1);

        if (strategy != null) {
            playAutoTurn(current, strategy);
            return;
        }

        while (!game.isGameOver() && game.getCurrentPlayer() == current) {

            view.displayTurnHeader(current);
            view.displayBoard(game.getBoard(), game.getPlayers());
            view.displayCurrentObjective(current);

            // Phase insertion (si pas encore insérée)
            if (!game.isTileInserted()) {
                insertPhase();

                view.showBoardAfterInsertion();
                view.displayBoard(game.getBoard(), game.getPlayers());
            }

            // Phase move
            movePhase();

            // Si un undo/redo a annulé l'insertion pendant movePhase(),
            // on reboucle et on repasse par insertPhase().
            // Sinon, movePhase() a normalement fait doMovePlayer() => nextPlayer() => le tour est fini.
        }
    }

    private void playAutoTurn(Player player, PlayerStrategy strategy) {
        view.displayTurnHeader(player);
        view.displayBoard(game.getBoard(), game.getPlayers());
        view.displayCurrentObjective(player);

        // Rotation aléatoire
        int rotations = strategy.chooseRotations(game);
        for (int i = 0; i < rotations; i++) {
            game.rotateSpareTile();
        }

        // Insertion valide aléatoire
        Direction side;
        int index;
        do {
            side = strategy.chooseInsertionSide(game);
            index = strategy.chooseInsertionIndex(game);
        } while (!game.canInsertTile(side, index));

        game.doInsertTile(side, index);

        view.showBoardAfterInsertion();
        view.displayBoard(game.getBoard(), game.getPlayers());

        // Déplacement aléatoire
        Position from = player.getCurrentPosition();
        var reachable = game.getPathFinder()
                .getReachablePositions(game.getBoard(), from);

        Position target = strategy.chooseMove(game, reachable);
        game.doMovePlayer(target.getRow(), target.getCol());
    }

    private void insertPhase() {
        view.showInsertPhase();
        view.displaySpareTile(game.getSpareTile());

        while (!game.isTileInserted()) {
            String choice = view.askInsertCommand();

            switch (choice) {
                case "r" -> {
                    game.rotateSpareTile();
                    view.showSpareRotated();
                    view.displaySpareTile(game.getSpareTile());
                }
                case "i" -> {
                    Direction side = view.askSide();
                    int index = view.askIndex();
                    try {
                        game.doInsertTile(side, index); // IMPORTANT
                        view.showTileInserted(side, index);
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        view.showInsertError(e.getMessage());
                    }
                }
                case "u" -> {
                    if (game.canUndo()) {
                        game.undo();
                        view.showUndo();
                    } else view.showCannotUndo();
                }
                case "d" -> {
                    if (game.canRedo()) {
                        game.redo();
                        view.showRedo();
                    } else view.showCannotRedo();
                }
                default -> view.showInvalidCommand();
            }
        }
    }

    private void movePhase() {
        view.showMovePhase(game.getCurrentPlayer());

        boolean moved = false;
        while (!moved && !game.isGameOver()) {
            String cmd = view.askMoveCommand();

            switch (cmd) {
                case "u" -> {
                    if (game.canUndo()) {
                        game.undo();

                        // Si l'undo annule l'insertion → retour insertion
                        if (!game.isTileInserted()) {
                            return;
                        }

                        view.showUndo();
                        view.displayBoard(game.getBoard(), game.getPlayers());
                        view.displayCurrentObjective(game.getCurrentPlayer());
                    } else {
                        view.showCannotUndo();
                    }
                }
                case "d" -> {
                    if (game.canRedo()) {
                        game.redo();

                        // Si le redo annule l'insertion → retour insertion
                        if (!game.isTileInserted()) {
                            return;
                        }

                        view.showRedo();
                        view.displayBoard(game.getBoard(), game.getPlayers());
                        view.displayCurrentObjective(game.getCurrentPlayer());
                    } else {
                        view.showCannotRedo();
                    }
                }
                case "stay" -> {
                    Position p = game.getCurrentPlayer().getCurrentPosition();
                    try {
                        game.doMovePlayer(p.getRow(), p.getCol());
                        view.showStayed();
                        moved = true;
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        view.showMoveError(e.getMessage());
                    }
                }
                case "m" -> {
                    int r = view.askRow();
                    int c = view.askCol();
                    try {
                        game.doMovePlayer(r, c);
                        view.showMoved(r, c);
                        moved = true;
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        view.showMoveError(e.getMessage());
                    }
                }
                default -> view.showInvalidCommand();
            }
        }
    }
}
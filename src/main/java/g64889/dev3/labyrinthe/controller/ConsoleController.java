package g64889.dev3.labyrinthe.controller;

import g64889.dev3.labyrinthe.console.ConsoleView;
import g64889.dev3.labyrinthe.controller.strategy.PlayerStrategy;
import g64889.dev3.labyrinthe.controller.strategy.RandomStrategy;
import g64889.dev3.labyrinthe.model.*;
import g64889.dev3.labyrinthe.model.enumeration.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * ConsoleController manages the flow of the game in console mode.
 * <p>
 * It connects the {@link LabyrinthGame} model with the {@link ConsoleView}
 * and coordinates user actions, AI strategies and game phases.
 * <p>
 * This class does not contain any game logic itself; all rules are enforced
 * by the model. The controller only calls model methods and updates the view.
 */
public class ConsoleController {

    /** The game model (facade) */
    private final LabyrinthGame game;

    /** The console view used for input and output */
    private final ConsoleView view;

    /** List of strategies, one per player (null = human player) */
    private List<PlayerStrategy> strategies;

    /**
     * Creates a new console controller.
     *
     * @param game the game model
     * @param view the console view
     */
    public ConsoleController(LabyrinthGame game, ConsoleView view) {
        this.game = game;
        this.view = view;
    }

    /**
     * Starts the game loop.
     * <p>
     * This method initializes the game, assigns strategies to players
     * (human or AI), and runs the main game loop until the game is over.
     */
    public void start() {
        view.showWelcome();

        int nbPlayers = view.askNumberOfPlayers();
        game.startGame(nbPlayers);

        strategies = new ArrayList<>();

        // Player 1 is human (no strategy)
        strategies.add(null);

        // Other players use a random AI strategy
        for (int i = 1; i < nbPlayers; i++) {
            strategies.add(new RandomStrategy());
        }

        view.showGameStart();

        while (!game.isGameOver()) {
            playTurn();
        }

        view.displayWinner(game.getWinner());
    }

    /**
     * Plays a full turn for the current player.
     * <p>
     * If the player has an associated {@link PlayerStrategy},
     * the turn is played automatically. Otherwise, the player
     * interacts through the console.
     */
    private void playTurn() {
        Player current = game.getCurrentPlayer();
        PlayerStrategy strategy = strategies.get(current.getId() - 1);

        if (strategy != null) {
            playAutoTurn(current, strategy);
            return;
        }

        // Human player turn
        while (!game.isGameOver() && game.getCurrentPlayer() == current) {

            view.displayTurnHeader(current);
            view.displayBoard(game.getBoard(), game.getPlayers());
            view.displayCurrentObjective(current);

            // Phase 1: tile insertion
            if (!game.isTileInserted()) {
                insertPhase();

                view.showBoardAfterInsertion();
                view.displayBoard(game.getBoard(), game.getPlayers());
            }

            // Phase 2: movement
            movePhase();
        }
    }

    /**
     * Plays a complete turn automatically using a strategy.
     *
     * @param player   the player controlled by the AI
     * @param strategy the strategy used to make decisions
     */
    private void playAutoTurn(Player player, PlayerStrategy strategy) {
        view.displayTurnHeader(player);
        view.displayBoard(game.getBoard(), game.getPlayers());
        view.displayCurrentObjective(player);

        // Random rotations of the spare tile
        int rotations = strategy.chooseRotations(game);
        for (int i = 0; i < rotations; i++) {
            game.rotateSpareTile();
        }

        // Random valid insertion
        Direction side;
        int index;
        do {
            side = strategy.chooseInsertionSide(game);
            index = strategy.chooseInsertionIndex(game);
        } while (!game.canInsertTile(side, index));

        game.doInsertTile(side, index);

        view.showBoardAfterInsertion();
        view.displayBoard(game.getBoard(), game.getPlayers());

        // Random movement among reachable positions
        Position from = player.getCurrentPosition();
        var reachable = game.getPathFinder()
                .getReachablePositions(game.getBoard(), from);

        Position target = strategy.chooseMove(game, reachable);
        game.doMovePlayer(target.getRow(), target.getCol());
    }

    /**
     * Handles the tile insertion phase for a human player.
     * <p>
     * The player can rotate the spare tile, insert it,
     * or use undo/redo commands until a valid insertion is performed.
     */
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
                        game.doInsertTile(side, index);
                        view.showTileInserted(side, index);
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        view.showInsertError(e.getMessage());
                    }
                }
                case "u" -> {
                    if (game.canUndo()) {
                        game.undo();
                        view.showUndo();
                    } else {
                        view.showCannotUndo();
                    }
                }
                case "d" -> {
                    if (game.canRedo()) {
                        game.redo();
                        view.showRedo();
                    } else {
                        view.showCannotRedo();
                    }
                }
                default -> view.showInvalidCommand();
            }
        }
    }

    /**
     * Handles the movement phase for a human player.
     * <p>
     * The player may move to a reachable position, stay on the same cell,
     * or use undo/redo commands. The phase ends when a valid move is performed.
     */
    private void movePhase() {
        view.showMovePhase(game.getCurrentPlayer());

        boolean moved = false;
        while (!moved && !game.isGameOver()) {
            String cmd = view.askMoveCommand();

            switch (cmd) {
                case "u" -> {
                    if (game.canUndo()) {
                        game.undo();

                        // If undo cancels the insertion, return to insertion phase
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

                        // If redo cancels the insertion, return to insertion phase
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
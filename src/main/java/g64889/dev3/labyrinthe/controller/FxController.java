package g64889.dev3.labyrinthe.controller;

import g64889.dev3.labyrinthe.controller.strategy.PlayerStrategy;
import g64889.dev3.labyrinthe.controller.strategy.RandomStrategy;
import g64889.dev3.labyrinthe.model.*;
import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.enumeration.GameMode;
import g64889.dev3.labyrinthe.view.MainView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.*;

/**
 * JavaFX controller responsible for connecting the {@link LabyrinthGame} model
 * with the {@link MainView} user interface.
 * <p>
 * This controller handles user interactions, AI turns, undo/redo actions,
 * and game flow management. All game rules are enforced by the model;
 * this class only orchestrates calls and updates the UI state.
 */
public class FxController {

    /** The game model facade. */
    private final LabyrinthGame game;

    /** The main JavaFX view. */
    private final MainView view;

    /** Callback invoked when a restart is requested. */
    private Runnable onRestartRequested;

    /**
     * Mapping between players and their strategies.
     * <p>
     * Convention: the first player is human, the others are AI-controlled.
     */
    private final Map<Player, PlayerStrategy> strategies = new HashMap<>();

    /** Indicates whether AI turns are currently being executed. */
    private boolean aiPlaying = false;

    /** Suspends AI autoplay when the user navigates history (undo/redo). */
    private boolean suspendAutoPlay = false;

    /** Random generator reused for AI insertion attempts. */
    private final Random rnd = new Random();

    /**
     * Creates a new JavaFX controller and binds the view to the model.
     *
     * @param game the game model
     * @param view the JavaFX main view
     */
    public FxController(LabyrinthGame game, MainView view) {
        this.game = game;
        this.view = view;

        view.bindGame(game);
        bindHandlers();
    }

    /**
     * Starts a new game with the given number of players and game mode.
     *
     * @param players number of players
     * @param mode    selected game mode
     */
    public void startGame(int players, GameMode mode) {
        aiPlaying = false;
        suspendAutoPlay = false;

        game.setMode(mode);
        game.startGame(players);

        strategies.clear();
        List<Player> ps = game.getPlayers();
        if (ps.size() >= 2) {
            for (int i = 1; i < ps.size(); i++) {
                strategies.put(ps.get(i), new RandomStrategy());
            }
        }

        refreshInsertionArrows();
        maybePlayAiTurns();
    }

    /**
     * Refreshes insertion arrows according to the current game state.
     */
    private void refreshInsertionArrows() {
        // We do method reference to validate the BiPredicate in update... method //
        view.getInsertionPad().updateInsertionArrows(game::canInsertTile);
    }


    /**
     * Checks whether it is currently the human player's turn.
     *
     * @return true if the current player is human
     */
    private boolean isHumanTurn() {
        List<Player> ps = game.getPlayers();
        if (ps.isEmpty()) return true;
        return game.getCurrentPlayer() == ps.getFirst();
    }

    /**
     * Enables or disables all interactive UI elements.
     *
     * @param disabled true to disable the UI, false otherwise
     */
    private void setUiDisabled(boolean disabled) {
        view.getInsertionPad().setDisabledAll(disabled);
        view.getHudView().setRotateDisabled(disabled);
        view.getBoardView().setDisable(disabled);

        // When the interface is reactivated, the insertion arrows are only reactivated
        // if we are still in the tile insertion phase.
        if (!disabled && !game.isTileInserted()) {
            refreshInsertionArrows();
        }

        refreshUndoRedo();
    }

    /**
     * Common guard used to block invalid human actions.
     *
     * @param needsInsertedTile true if the action requires a tile to be inserted first (movement phase)
     * @return true if the action must be blocked
     */
    private boolean blockIfNotHumanAction(boolean needsInsertedTile) {
        if (game.isGameOver()) {
            maybeShowGameOverDialog();
            return true;
        }
        if (aiPlaying || !isHumanTurn()) return true;
        // This instruction allows auto-play to be reactivated as soon
        // as a human action is performed,
        // so that automatic turns can resume normally after the player's intervention.
        if (suspendAutoPlay) suspendAutoPlay = false;
        return needsInsertedTile && !game.isTileInserted();
    }

    /**
     * Handles undo/redo actions with a common behavior.
     *
     * It temporarily suspends auto-play, executes the requested action,
     * and then updates the interface to ensure state consistency after an undo or redo.
     * @param action the history action to execute
     */
    private void handleHistoryAction(Runnable action) {
        suspendAutoPlay = true;

        action.run();
        refreshUndoRedo();
        refreshInsertionArrows();

        setUiDisabled(!isHumanTurn());
    }

    /**
     * Binds all UI events to controller actions.
     */
    private void bindHandlers() {

        view.getHudView().onRotate(() -> {
            if (blockIfNotHumanAction(false)) return;
            game.rotateSpareTile();
            refreshInsertionArrows();
        });

        view.getInsertionPad().setOnInsert((side, index) -> {
            if (blockIfNotHumanAction(false)) return;
            try {
                game.doInsertTile(side, index);
                refreshInsertionArrows();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        view.getBoardView().setOnCellClicked((row, col) -> {
            if (blockIfNotHumanAction(true)) return;
            try {
                game.doMovePlayer(row, col);
                refreshInsertionArrows();

                if (game.isGameOver()) {
                    maybeShowGameOverDialog();
                    return;
                }

                maybePlayAiTurns();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        view.getHudView().onUndo(() -> handleHistoryAction(game::undo));
        view.getHudView().onRedo(() -> handleHistoryAction(game::redo));

        view.getHudView().onAbandon(() -> {
            aiPlaying = false;
            suspendAutoPlay = true;
            if (onRestartRequested != null) onRestartRequested.run();
        });
    }

    /**
     * Triggers AI turns until the human player regains control.
     */
    private void maybePlayAiTurns() {
        if (aiPlaying) return;

        if (suspendAutoPlay) {
            setUiDisabled(!isHumanTurn());
            return;
        }

        if (isHumanTurn()) {
            setUiDisabled(false);
            return;
        }

        aiPlaying = true;
        setUiDisabled(true);
        Platform.runLater(this::playAiChain);
    }

    /**
     * Executes consecutive AI turns.
     */
    private void playAiChain() {
        try {
            while (!isHumanTurn()) {
                Player current = game.getCurrentPlayer();
                PlayerStrategy ai = strategies.get(current);

                int r = ai.chooseRotations(game);
                for (int i = 0; i < r; i++) game.rotateSpareTile();

                aiDoValidInsert();
                refreshInsertionArrows();

                Set<Position> reachable = game.getReachableForCurrentPlayer();
                if (!reachable.isEmpty()) {
                    Position p = ai.chooseMove(game, reachable);
                    game.doMovePlayer(p.getRow(), p.getCol());
                } else {
                    Position cur = game.getCurrentPlayer().getCurrentPosition();
                    game.doMovePlayer(cur.getRow(), cur.getCol());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            aiPlaying = false;
            setUiDisabled(false);
            if (game.isGameOver()) {
                Platform.runLater(this::maybeShowGameOverDialog);
            }
        }
    }

    /**
     * Performs a valid tile insertion for an AI player.
     */
    private void aiDoValidInsert() {
        Direction[] sides = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        int[] idx = {1, 3, 5};

        for (int tries = 0; tries < 50; tries++) {
            Direction side = sides[rnd.nextInt(sides.length)];
            int index = idx[rnd.nextInt(idx.length)];

            if (game.canInsertTile(side, index)) {
                game.doInsertTile(side, index);
                return;
            }
        }
    }

    /**
     * Updates the enabled state of undo and redo buttons.
     */
    private void refreshUndoRedo() {
        view.getHudView().setUndoDisabled(!game.canUndo());
        view.getHudView().setRedoDisabled(!game.canRedo());
    }

    /**
     * Registers a callback invoked when a restart is requested.
     *
     * @param r the callback to execute
     */
    public void setOnRestartRequested(Runnable r) {
        this.onRestartRequested = r;
    }

    /**
     * Displays the end-of-game dialog and handles restart or exit.
     */
    private void maybeShowGameOverDialog() {
        if (!game.isGameOver()) return;

        Player w = game.getWinner();
        String winnerName = (w == null) ? "Personne" : w.getName();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Partie terminée");
        alert.setHeaderText("Victoire !");
        alert.setContentText("Gagnant : " + winnerName + "\n\nRelancer une partie ?");

        ButtonType relancer = new ButtonType("Relancer");
        ButtonType quitter = new ButtonType("Quitter");
        alert.getButtonTypes().setAll(relancer, quitter);

        var res = alert.showAndWait();
        if (res.isPresent() && res.get() == relancer) {
            if (onRestartRequested != null) onRestartRequested.run();
        } else {
            Platform.exit();
        }
    }
}
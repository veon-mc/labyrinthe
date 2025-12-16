package g64889.dev3.labyrinthe.model;

import g64889.dev3.labyrinthe.model.enumeration.Direction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class LabyrinthGameTest {

    private LabyrinthGame game;

    @BeforeEach
    void setup() {
        game = new LabyrinthGame();
    }

    // -----------------------------
    // startGame
    // -----------------------------

    @Test
    void startGame_initializesCoreState() {
        game.startGame(2);

        assertNotNull(game.getBoard());
        assertNotNull(game.getSpareTile());
        assertEquals(2, game.getPlayers().size());
        assertEquals(0, game.getCurrentPlayerIndex());
        assertFalse(game.isGameOver());
        assertNull(game.getWinner());
        assertFalse(game.isTileInserted());
    }

    // -----------------------------
    // canInsertTile
    // -----------------------------

    @Test
    void canInsertTile_rejectsEvenIndex() {
        game.startGame(2);
        assertFalse(game.canInsertTile(Direction.WEST, 0));
        assertFalse(game.canInsertTile(Direction.WEST, 2));
        assertFalse(game.canInsertTile(Direction.WEST, 4));
        assertFalse(game.canInsertTile(Direction.WEST, 6));
    }

    @Test
    void canInsertTile_rejectsOutOfRangeIndex() {
        game.startGame(2);
        assertFalse(game.canInsertTile(Direction.WEST, -1));
        assertFalse(game.canInsertTile(Direction.WEST, 7));
        assertFalse(game.canInsertTile(Direction.WEST, 999));
    }

    @Test
    void canInsertTile_acceptsOddIndexInRange() {
        game.startGame(2);
        assertTrue(game.canInsertTile(Direction.WEST, 1));
        assertTrue(game.canInsertTile(Direction.WEST, 3));
        assertTrue(game.canInsertTile(Direction.WEST, 5));
    }

    @Test
    void canInsertTile_rejectsOppositeOfLastInsertionSameIndex() {
        game.startGame(2);

        // première insertion OK
        game.insertTile(Direction.WEST, 1);
        assertTrue(game.isTileInserted());

        // l'opposé exact doit être refusé (WEST <-> EAST)
        assertFalse(game.canInsertTile(Direction.EAST, 1));
    }

    // -----------------------------
    // insertTile
    // -----------------------------

    @Test
    void insertTile_setsLastInsertionAndTileInserted() {
        game.startGame(2);

        game.insertTile(Direction.WEST, 1);

        assertTrue(game.isTileInserted());
        assertEquals(Direction.WEST, game.getLastInsertionSide());
        assertEquals(1, game.getLastInsertionIndex());
        assertNotNull(game.getSpareTile());
    }

    @Test
    void insertTile_throwsIfAlreadyInsertedThisTurn() {
        game.startGame(2);

        game.insertTile(Direction.WEST, 1);
        assertThrows(IllegalStateException.class, () -> game.insertTile(Direction.WEST, 3));
    }

    @Test
    void insertTile_throwsIfInvalidPosition() {
        game.startGame(2);

        // index pair interdit
        assertThrows(IllegalArgumentException.class, () -> game.insertTile(Direction.WEST, 2));
        // index hors limites
        assertThrows(IllegalArgumentException.class, () -> game.insertTile(Direction.WEST, 7));
    }

    @Test
    void insertTile_throwsIfGameOver() {
        game.startGame(2);
        game.setGameOver(true);

        assertThrows(IllegalStateException.class, () -> game.insertTile(Direction.WEST, 1));
    }

    // -----------------------------
    // movePlayer (règles + tour)
    // -----------------------------

    @Test
    void movePlayer_throwsIfGameOver() {
        game.startGame(2);
        game.setGameOver(true);

        assertThrows(IllegalStateException.class, () -> game.movePlayer(0, 0));
    }

    @Test
    void movePlayer_throwsIfTileNotInsertedYet() {
        game.startGame(2);
        assertFalse(game.isTileInserted());

        assertThrows(IllegalStateException.class, () -> game.movePlayer(0, 0));
    }

    @Test
    void movePlayer_onSameCellIsAlwaysAllowed_andAdvancesTurn() {
        game.startGame(2);

        // insertion obligatoire avant de bouger
        game.insertTile(Direction.WEST, 1);
        assertTrue(game.isTileInserted());

        Player p0 = game.getCurrentPlayer();
        Position pos = p0.getCurrentPosition();

        // bouger sur sa propre case = autorisé par ton code (pas de check path)
        game.movePlayer(pos.getRow(), pos.getCol());

        assertEquals(1, game.getCurrentPlayerIndex(), "Le tour doit passer au joueur suivant");
        assertFalse(game.isTileInserted(), "Après le move, tileInserted doit redevenir false");
    }

    @Test
    void afterMove_nextPlayerCannotInsertOppositeOfLastInsertionSameIndex() {
        game.startGame(2);

        // joueur 1 insère WEST 1
        game.insertTile(Direction.WEST, 1);

        // joueur 1 termine son tour en restant sur place
        Player p0 = game.getCurrentPlayer();
        Position pos = p0.getCurrentPosition();
        game.movePlayer(pos.getRow(), pos.getCol());

        // joueur 2 : opposé exact (EAST 1) interdit
        assertFalse(game.canInsertTile(Direction.EAST, 1));
    }

    // -----------------------------
    // rotateSpareTile + getters simples
    // -----------------------------

    @Test
    void rotateSpareTile_doesNotThrow() {
        game.startGame(2);
        assertDoesNotThrow(() -> game.rotateSpareTile());
    }

    @Test
    void getPlayers_returnsUnmodifiableList() {
        game.startGame(2);
        assertThrows(UnsupportedOperationException.class, () -> game.getPlayers().add(null));
    }

    // -----------------------------
    // Commandes undo/redo (si ton CommandManager est complet)
    // -----------------------------

    @Test
    void undoRedo_initiallyNotAvailable() {
        game.startGame(2);
        assertFalse(game.canUndo());
        assertFalse(game.canRedo());
    }

    @Test
    void doInsertTile_thenUndo_shouldBePossible() {
        game.startGame(2);

        game.doInsertTile(Direction.WEST, 1);
        assertTrue(game.canUndo());

        game.undo();
        assertTrue(game.canRedo());
    }
}

package g64889.dev3.labyrinthe.controller.strategy;

import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.LabyrinthGame;
import g64889.dev3.labyrinthe.model.Position;

import java.util.Set;

/**
 * Defines a strategy used by a player to make decisions during a turn.
 * <p>
 * Implementations of this interface determine how a player rotates
 * the spare tile, chooses where to insert it, and selects a movement.
 * <p>
 * This interface represents a decision-making contract and does not
 * contain any game logic.
 */
public interface PlayerStrategy {

    /**
     * Chooses the number of clockwise rotations to apply to the spare tile.
     *
     * @param game the current game state
     * @return the number of rotations (between 0 and 3 inclusive)
     */
    int chooseRotations(LabyrinthGame game);

    /**
     * Chooses the side from which the spare tile will be inserted.
     *
     * @param game the current game state
     * @return a valid insertion side (NORTH, SOUTH, EAST or WEST)
     */
    Direction chooseInsertionSide(LabyrinthGame game);

    /**
     * Chooses the index at which the spare tile will be inserted.
     *
     * @param game the current game state
     * @return a valid insertion index (1, 3 or 5)
     */
    int chooseInsertionIndex(LabyrinthGame game);

    /**
     * Chooses the target position for the player's movement.
     *
     * @param game      the current game state
     * @param reachable the set of positions reachable from the current position
     * @return the chosen target position
     */
    Position chooseMove(LabyrinthGame game, Set<Position> reachable);
}
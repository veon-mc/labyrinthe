package g64889.dev3.labyrinthe.model;

import g64889.dev3.labyrinthe.model.enumeration.Direction;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Computes reachable positions on the board using tile openings.
 * <p>
 * This class performs a breadth-first search (BFS) starting from a given
 * position. Two adjacent cells are considered connected only if:
 * <ul>
 *   <li>the current tile has an opening in the movement direction</li>
 *   <li>the neighbor tile has an opening in the opposite direction</li>
 * </ul>
 */
public class PathFinder {

    /**
     * Returns the set of all positions reachable from a start position.
     * <p>
     * The start position is always included in the returned set.
     *
     * @param board the game board
     * @param start the starting position
     * @return a set of reachable positions
     */
    public Set<Position> getReachablePositions(Board board, Position start) {
        Set<Position> visited = new HashSet<>();
        Queue<Position> queue = new ArrayDeque<>();

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            Position current = queue.remove();

            for (Direction dir : Direction.values()) {
                Position next = neighbor(current, dir);

                if (!isInsideBoard(board, next)) continue;
                if (!areConnected(board, current, next, dir)) continue;

                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }

        return visited;
    }

    /**
     * Computes the adjacent position in a given direction.
     *
     * @param pos the current position
     * @param dir the movement direction
     * @return the neighboring position
     */
    private Position neighbor(Position pos, Direction dir) {
        return switch (dir) {
            case NORTH -> new Position(pos.getRow() - 1, pos.getCol());
            case SOUTH -> new Position(pos.getRow() + 1, pos.getCol());
            case WEST  -> new Position(pos.getRow(), pos.getCol() - 1);
            case EAST  -> new Position(pos.getRow(), pos.getCol() + 1);
        };
    }

    /**
     * Checks whether a position is inside the board boundaries.
     *
     * @param board the board
     * @param pos   the position to check
     * @return true if the position is valid
     */
    private boolean isInsideBoard(Board board, Position pos) {
        int size = board.getSize();
        return pos.getRow() >= 0 && pos.getRow() < size &&
                pos.getCol() >= 0 && pos.getCol() < size;
    }

    /**
     * Checks if two adjacent positions are connected according to tile openings.
     *
     * @param board the board
     * @param a     the source position
     * @param b     the adjacent position
     * @param dir   the direction from a to b
     * @return true if movement from a to b is possible
     */
    private boolean areConnected(Board board, Position a, Position b, Direction dir) {
        Tile tileA = board.getTileAt(a);
        Tile tileB = board.getTileAt(b);

        if (!tileA.hasOpening(dir)) return false;

        Direction opposite = switch (dir) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST  -> Direction.WEST;
            case WEST  -> Direction.EAST;
        };

        return tileB.hasOpening(opposite);
    }
}
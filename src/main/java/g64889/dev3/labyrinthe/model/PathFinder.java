package g64889.dev3.labyrinthe.model;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class PathFinder {

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

    private Position neighbor(Position pos, Direction dir) {
        return switch (dir) {
            case NORTH -> new Position(pos.getRow() - 1, pos.getCol());
            case SOUTH -> new Position(pos.getRow() + 1, pos.getCol());
            case WEST  -> new Position(pos.getRow(), pos.getCol() - 1);
            case EAST  -> new Position(pos.getRow(), pos.getCol() + 1);
        };
    }

    private boolean isInsideBoard(Board board, Position pos) {
        int size = board.getSize();
        return pos.getRow() >= 0 && pos.getRow() < size &&
                pos.getCol() >= 0 && pos.getCol() < size;
    }

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

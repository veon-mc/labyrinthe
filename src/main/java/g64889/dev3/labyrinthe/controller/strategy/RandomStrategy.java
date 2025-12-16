package g64889.dev3.labyrinthe.controller.strategy;

import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.LabyrinthGame;
import g64889.dev3.labyrinthe.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * A {@link PlayerStrategy} implementation that makes random decisions.
 * <p>
 * This strategy selects random rotations, insertion sides and indices,
 * and randomly chooses a reachable position for movement.
 * <p>
 * All decisions respect the constraints defined by the game rules,
 * as the controller ensures validity before applying them.
 */
public class RandomStrategy implements PlayerStrategy {

    /** Random generator used to make decisions */
    private final Random random = new Random();

    /**
     * {@inheritDoc}
     */
    @Override
    public Direction chooseInsertionSide(LabyrinthGame game) {
        Direction[] dirs = Direction.values();
        return dirs[random.nextInt(dirs.length)];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int chooseInsertionIndex(LabyrinthGame game) {
        int[] indices = {1, 3, 5};
        return indices[random.nextInt(indices.length)];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int chooseRotations(LabyrinthGame game) {
        return random.nextInt(4);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Position chooseMove(LabyrinthGame game, Set<Position> reachable) {
        List<Position> list = new ArrayList<>(reachable);
        return list.get(random.nextInt(list.size()));
    }
}
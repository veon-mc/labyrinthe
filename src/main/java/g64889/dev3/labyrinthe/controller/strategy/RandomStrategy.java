package g64889.dev3.labyrinthe.controller.strategy;

import g64889.dev3.labyrinthe.model.Direction;
import g64889.dev3.labyrinthe.model.LabyrinthGame;
import g64889.dev3.labyrinthe.model.Position;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomStrategy implements PlayerStrategy {

    private final Random random = new Random();

    @Override
    public Direction chooseInsertionSide(LabyrinthGame game) {
        Direction[] dirs = Direction.values();
        return dirs[random.nextInt(dirs.length)];
    }

    @Override
    public int chooseInsertionIndex(LabyrinthGame game) {
        int[] indices = {1, 3, 5};
        return indices[random.nextInt(indices.length)];
    }

    @Override
    public int chooseRotations(LabyrinthGame game) {
        return random.nextInt(4);
    }

    @Override
    public Position chooseMove(LabyrinthGame game, Set<Position> reachable) {
        List<Position> list = new ArrayList<>(reachable);
        return list.get(random.nextInt(list.size()));
    }
}

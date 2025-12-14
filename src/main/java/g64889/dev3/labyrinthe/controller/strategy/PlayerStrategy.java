package g64889.dev3.labyrinthe.controller.strategy;


import g64889.dev3.labyrinthe.model.Direction;
import g64889.dev3.labyrinthe.model.LabyrinthGame;
import g64889.dev3.labyrinthe.model.Position;


import java.util.Set;

public interface PlayerStrategy {
    int chooseRotations(LabyrinthGame game);                    // 0..3
    Direction chooseInsertionSide(LabyrinthGame game);           // NORTH/SOUTH/EAST/WEST
    int chooseInsertionIndex(LabyrinthGame game);               // 1/3/5
    Position chooseMove(LabyrinthGame game, Set<Position> reachable);
}

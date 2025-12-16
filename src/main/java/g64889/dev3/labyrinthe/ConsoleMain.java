package g64889.dev3.labyrinthe;

import g64889.dev3.labyrinthe.console.ConsoleView;
import g64889.dev3.labyrinthe.controller.ConsoleController;
import g64889.dev3.labyrinthe.model.LabyrinthGame;

/**
 * Entry point of the console version of the Labyrinth game.
 * <p>
 * This class initializes the game model, the console view,
 * and the console controller, then starts the game loop.
 */
public class ConsoleMain {

    /**
     * Launches the console-based Labyrinth game.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        LabyrinthGame game = new LabyrinthGame();
        ConsoleView view = new ConsoleView();
        ConsoleController controller = new ConsoleController(game, view);
        controller.start();
    }
}
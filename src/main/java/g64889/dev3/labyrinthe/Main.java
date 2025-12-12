package g64889.dev3.labyrinthe;


import g64889.dev3.labyrinthe.console.ConsoleView;
import g64889.dev3.labyrinthe.controller.ConsoleController;
import g64889.dev3.labyrinthe.model.LabyrinthGame;

public class Main {
    public static void main(String[] args) {
        LabyrinthGame game = new LabyrinthGame();
        ConsoleView view = new ConsoleView();
        ConsoleController controller = new ConsoleController(game, view);
        controller.start();
    }

}

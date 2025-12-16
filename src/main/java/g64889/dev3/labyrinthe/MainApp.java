package g64889.dev3.labyrinthe;

import g64889.dev3.labyrinthe.model.enumeration.GameMode;
import g64889.dev3.labyrinthe.model.LabyrinthGame;
import g64889.dev3.labyrinthe.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;
import g64889.dev3.labyrinthe.controller.FxController;

import java.util.List;

/**
 * JavaFX entry point of the Labyrinth game.
 * <p>
 * This class initializes the JavaFX application, creates the game model,
 * main view and controller, and handles game start and restart logic.
 * <p>
 * The user is asked to choose the game mode and the number of players
 * before starting a new game.
 */
public class MainApp extends Application {

    /**
     * Starts the JavaFX application.
     * <p>
     * This method:
     * <ul>
     *   <li>creates the game model</li>
     *   <li>creates the main view</li>
     *   <li>creates the JavaFX controller</li>
     *   <li>initializes the primary stage</li>
     *   <li>asks the user for game settings</li>
     * </ul>
     *
     * @param stage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        LabyrinthGame game = new LabyrinthGame();
        MainView view = new MainView();
        FxController controller = new FxController(game, view);

        stage.setTitle("Labyrinth Game");
        Scene scene = new Scene(view, 1500, 900);
        stage.setScene(scene);
        stage.show();

        // Restart callback (used after game over)
        controller.setOnRestartRequested(() -> {
            GameMode mode = askMode();
            int players = askPlayers();
            controller.startGame(players, mode);
        });

        // Initial game start
        GameMode mode = askMode();
        int players = askPlayers();
        controller.startGame(players, mode);
    }

    /**
     * Displays a dialog asking the user for the number of players.
     *
     * @return the selected number of players (default: 2)
     */
    private int askPlayers() {
        ChoiceDialog<Integer> dialog =
                new ChoiceDialog<>(2, List.of(2, 3, 4));

        dialog.setTitle("Labyrinthe");
        dialog.setHeaderText("Choisir le nombre de joueurs");
        dialog.setContentText("Joueurs :");

        return dialog.showAndWait().orElse(2);
    }

    /**
     * Displays a dialog asking the user to choose the game mode.
     *
     * @return the selected game mode (default: STANDARD)
     */
    private GameMode askMode() {
        ChoiceDialog<GameMode> dialog =
                new ChoiceDialog<>(GameMode.STANDARD, GameMode.values());

        dialog.setTitle("Labyrinthe");
        dialog.setHeaderText("Choisir le mode de jeu");
        dialog.setContentText("Mode :");

        return dialog.showAndWait().orElse(GameMode.STANDARD);
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
package g64889.dev3.labyrinthe;

import g64889.dev3.labyrinthe.model.LabyrinthGame;
import g64889.dev3.labyrinthe.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;
import g64889.dev3.labyrinthe.controller.FxController;

import java.util.List;
import java.util.Optional;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        LabyrinthGame game = new LabyrinthGame();
        MainView view = new MainView();
        FxController controller = new FxController(game, view);

        Scene scene = new Scene(view, 1100, 700);
        stage.setScene(scene);
        stage.show();

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(2, List.of(2, 3, 4));
        dialog.setTitle("Labyrinthe");
        dialog.setHeaderText("Choisir le nombre de joueurs");
        dialog.setContentText("Joueurs :");

        Optional<Integer> res = dialog.showAndWait();
        int players = res.orElse(2);

        controller.startGame(players);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

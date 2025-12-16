package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

/**
 * Heads-up display (HUD) of the game.
 * <p>
 * This view displays:
 * <ul>
 *   <li>the spare tile to insert</li>
 *   <li>the current objective image</li>
 *   <li>current turn information and players progress</li>
 *   <li>action buttons (undo, redo, rotate, abandon)</li>
 * </ul>
 * <p>
 * This class only handles UI rendering and user interactions.
 */
public class HudView extends VBox {

    private final ImageView objectiveImg;
    private final ImageView spareImg;
    private final Label turnLabel;
    private final VBox playersBox;
    private final Label phaseLabel;

    private final Button undoBtn;
    private final Button redoBtn;
    private final Button rotateBtn;
    private final Button abandonBtn;

    /**
     * Creates the HUD view and initializes all UI components.
     */
    public HudView() {
        setStyle("""
    -fx-background-color: #2a2a2a;
    -fx-background-radius: 16;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 6);
""");

        Label spareLabel = new Label("Tuile à insérer");
        spareLabel.setStyle("-fx-text-fill: #eeeeee; -fx-font-size: 13px; -fx-font-weight: bold;");

        spareImg = new ImageView();
        spareImg.setFitWidth(140);
        spareImg.setFitHeight(140);
        spareImg.setPreserveRatio(false);

        getChildren().addAll(spareLabel, spareImg);

        setSpacing(10);
        setPadding(new Insets(12));
        setAlignment(Pos.TOP_CENTER);

        objectiveImg = new ImageView();
        objectiveImg.setFitWidth(140);
        objectiveImg.setFitHeight(140);
        objectiveImg.setPreserveRatio(true);

        Label objectiveLabel = new Label("Objectif");
        objectiveLabel.setStyle("-fx-text-fill: #eeeeee; -fx-font-size: 13px; -fx-font-weight: bold;");

        turnLabel = new Label("Tour : ?");
        turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        phaseLabel = new Label("Phase : -");
        phaseLabel.setStyle("-fx-text-fill: #cccccc;");

        VBox turnBox = new VBox(2, turnLabel, phaseLabel);
        turnBox.setAlignment(Pos.CENTER);

        playersBox = new VBox(6);
        playersBox.setFillWidth(true);

        getChildren().addAll(turnBox, playersBox);

        undoBtn = new Button("Undo");
        redoBtn = new Button("Redo");
        rotateBtn = new Button("Rotate");
        abandonBtn = new Button("Abandonner");

        String btnStyle = """
    -fx-background-radius: 10;
    -fx-font-weight: bold;
""";

        undoBtn.setStyle(btnStyle);
        redoBtn.setStyle(btnStyle);
        rotateBtn.setStyle(btnStyle);
        abandonBtn.setStyle(btnStyle);

        addHoverEffect(undoBtn);
        addHoverEffect(redoBtn);
        addHoverEffect(rotateBtn);
        addHoverEffect(abandonBtn);

        undoBtn.setMaxWidth(Double.MAX_VALUE);
        redoBtn.setMaxWidth(Double.MAX_VALUE);
        rotateBtn.setMaxWidth(Double.MAX_VALUE);
        abandonBtn.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(objectiveLabel, objectiveImg,
                undoBtn, redoBtn, rotateBtn, abandonBtn);
    }

    /**
     * Sets the objective image displayed in the HUD.
     *
     * @param obj the objective to display (may be null)
     */
    public void setObjective(Objective obj) {
        objectiveImg.setImage(obj == null ? null : Images.hudObjective(obj));
    }

    /**
     * Updates the current player label and the players status list.
     *
     * @param current the current player
     * @param players the list of all players
     */
    public void setTurnAndPlayers(Player current, java.util.List<Player> players) {
        turnLabel.setText("Tour : " + current.getName());

        playersBox.getChildren().clear();

        for (Player p : players) {
            int total = p.getObjectives().size();
            int done = Math.min(p.getCurrentObjectiveIndex(), total);

            Label name = new Label(p.getName() + " (" + p.getColor() + ")");
            name.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

            Label prog = new Label("Objectifs : " + done + " / " + total);
            prog.setStyle("-fx-text-fill: #dddddd;");

            String cur = (p.hasCompletedAllObjectives() || p.getCurrentObjective() == null)
                    ? "Terminé"
                    : p.getCurrentObjective().getObjective().getName();
            Label curObj = new Label("Cible : " + cur);
            curObj.setStyle("-fx-text-fill: #bbbbbb;");

            VBox card = new VBox(2, name, prog, curObj);
            card.setPadding(new Insets(8));
            card.setStyle("-fx-border-color: rgba(255,255,255,0.25); -fx-border-radius: 10; -fx-background-radius: 10;");

            if (p.getId() == current.getId()) {
                card.setStyle(card.getStyle() + "-fx-border-color: gold; -fx-border-width: 2;");
            }

            playersBox.getChildren().add(card);
        }
    }

    /**
     * Sets the spare tile image displayed in the HUD and applies the correct rotation.
     * <p>
     * The rotation logic is delegated to {@link HudTileRotation} to keep this class focused on UI rendering.
     *
     * @param tile the spare tile (may be null)
     */
    public void setSpareTile(Tile tile) {
        if (tile == null) {
            spareImg.setImage(null);
            spareImg.setRotate(0);
            return;
        }

        spareImg.setImage(Images.spareTileImage(tile));

        // 🔁 EXACT same rotation logic as before, just delegated
        double rot = HudTileRotation.spareRotation(tile);

        spareImg.setRotate(rot);
    }

    /**
     * Updates the displayed phase (insertion or movement).
     *
     * @param tileInserted true if the spare tile was inserted this turn
     */
    public void setPhase(boolean tileInserted) {
        if (tileInserted) {
            phaseLabel.setText("Phase : Mouvement");
        } else {
            phaseLabel.setText("Phase : Insertion");
        }
    }

    /**
     * Enables or disables the rotate button.
     *
     * @param v true to disable
     */
    public void setRotateDisabled(boolean v) {
        rotateBtn.setDisable(v);
    }

    /**
     * Adds a hover style effect to a button.
     *
     * @param b the button to style
     */
    private void addHoverEffect(Button b) {
        String normal = """
        -fx-background-radius: 10;
        -fx-font-weight: bold;
    """;

        String hover = """
        -fx-background-radius: 10;
        -fx-font-weight: bold;
        -fx-background-color: #4a4a4a;
        -fx-text-fill: white;
        -fx-cursor: hand;
    """;

        b.setStyle(normal);

        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e -> b.setStyle(normal));
    }

    // --- handlers (controller only) ---
    public void onUndo(Runnable r) { undoBtn.setOnAction(e -> r.run()); }
    public void onRedo(Runnable r) { redoBtn.setOnAction(e -> r.run()); }
    public void onRotate(Runnable r) { rotateBtn.setOnAction(e -> r.run()); }
    public void onAbandon(Runnable r) { abandonBtn.setOnAction(e -> r.run()); }

    /**
     * Enables or disables the undo button.
     *
     * @param v true to disable
     */
    public void setUndoDisabled(boolean v) { undoBtn.setDisable(v); }

    /**
     * Enables or disables the redo button.
     *
     * @param v true to disable
     */
    public void setRedoDisabled(boolean v) { redoBtn.setDisable(v); }
}
package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.*;
import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.enumeration.TileType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

/**
 * JavaFX HUD (Heads-Up Display) for the Labyrinth game.
 * <p>
 * This view shows:
 * <ul>
 *   <li>the spare tile to insert</li>
 *   <li>the current objective (image)</li>
 *   <li>the current turn and phase</li>
 *   <li>a small panel with players progress</li>
 *   <li>buttons for undo/redo/rotate/abandon</li>
 * </ul>
 * The HUD does not contain game logic: it only displays data and forwards
 * user actions through handlers provided by the controller.
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
     * Creates a new HUD view and initializes all UI elements.
     * <p>
     * The visual style is defined directly via JavaFX CSS strings.
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
     * Updates the displayed objective image.
     *
     * @param obj the objective to show (null clears the image)
     */
    public void setObjective(Objective obj) {
        objectiveImg.setImage(obj == null ? null : Images.hudObjective(obj));
    }

    /**
     * Updates the current turn label and rebuilds the player progress panel.
     * <p>
     * For each player, it displays name, color, progress, and current target.
     *
     * @param current the current player
     * @param players all players
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
     * Updates the displayed spare tile image and rotation.
     * <p>
     * Rotation is computed from openings and can include additional
     * adjustments for specific tiles/objectives.
     *
     * @param tile the spare tile to display (null clears the image)
     */
    public void setSpareTile(Tile tile) {
        if (tile == null) {
            spareImg.setImage(null);
            spareImg.setRotate(0);
            return;
        }

        spareImg.setImage(Images.spareTileImage(tile));

        double rot = rotationFromOpenings(tile);

        if (tile.getType() == TileType.L && tile.getObjective() != null) {
            rot = (rot + 90) % 360;
        }

        if (tile.getObjective() != null) {
            String key = tile.getObjective().getName();

            switch (key) {
                case "rat" -> rot = (rot + 90) % 360;
                case "scarabée", "hibou" -> rot = (rot + 180) % 360;
                default -> { /* no extra rotation */ }
            }
        }

        spareImg.setRotate(rot);
    }

    /**
     * Computes the visual rotation angle based on the tile openings.
     *
     * @param tile the tile to analyze
     * @return rotation angle in degrees (0, 90, 180, 270)
     */
    private double rotationFromOpenings(Tile tile) {
        var o = tile.getOpenings();

        return switch (tile.getType()) {
            case I -> o.contains(Direction.EAST) ? 90 : 0;

            case L -> {
                if (o.contains(Direction.NORTH) && o.contains(Direction.EAST)) yield 0;
                if (o.contains(Direction.EAST) && o.contains(Direction.SOUTH)) yield 90;
                if (o.contains(Direction.SOUTH) && o.contains(Direction.WEST)) yield 180;
                yield 270;
            }

            case T -> {
                if (!o.contains(Direction.SOUTH)) yield 0;
                if (!o.contains(Direction.WEST))  yield 90;
                if (!o.contains(Direction.NORTH)) yield 180;
                yield 270;
            }
        };
    }

    /**
     * Updates the phase label based on whether the tile has been inserted.
     *
     * @param tileInserted true if the current player already inserted the tile
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
     * @param v true to disable the button
     */
    public void setRotateDisabled(boolean v) {
        rotateBtn.setDisable(v);
    }

    /**
     * Adds a simple hover effect to a button by changing its style.
     *
     * @param b the button to configure
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

    /**
     * Registers a handler for the undo button.
     *
     * @param r action to execute when the button is pressed
     */
    public void onUndo(Runnable r) { undoBtn.setOnAction(e -> r.run()); }

    /**
     * Registers a handler for the redo button.
     *
     * @param r action to execute when the button is pressed
     */
    public void onRedo(Runnable r) { redoBtn.setOnAction(e -> r.run()); }

    /**
     * Registers a handler for the rotate button.
     *
     * @param r action to execute when the button is pressed
     */
    public void onRotate(Runnable r) { rotateBtn.setOnAction(e -> r.run()); }

    /**
     * Registers a handler for the abandon button.
     *
     * @param r action to execute when the button is pressed
     */
    public void onAbandon(Runnable r) { abandonBtn.setOnAction(e -> r.run()); }

    /**
     * Enables or disables the undo button.
     *
     * @param v true to disable the button
     */
    public void setUndoDisabled(boolean v) { undoBtn.setDisable(v); }

    /**
     * Enables or disables the redo button.
     *
     * @param v true to disable the button
     */
    public void setRedoDisabled(boolean v) { redoBtn.setDisable(v); }
}
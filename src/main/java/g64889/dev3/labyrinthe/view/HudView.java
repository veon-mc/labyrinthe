package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.Direction;
import g64889.dev3.labyrinthe.model.Objective;
import g64889.dev3.labyrinthe.model.Player;
import g64889.dev3.labyrinthe.model.Tile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class HudView extends VBox {

    private final ImageView objectiveImg;
    private final ImageView spareImg;
    private final Label turnLabel;
    private final VBox playersBox;

    private final Button undoBtn;
    private final Button redoBtn;
    private final Button rotateBtn;

    public HudView() {
        spareImg = new ImageView();
        spareImg.setFitWidth(140);
        spareImg.setFitHeight(140);
        spareImg.setPreserveRatio(false);

        getChildren().add(0, spareImg);
        setSpacing(10);
        setPadding(new Insets(12));
        setAlignment(Pos.TOP_CENTER);

        objectiveImg = new ImageView();
        objectiveImg.setFitWidth(140);
        objectiveImg.setFitHeight(140);
        objectiveImg.setPreserveRatio(true);

        turnLabel = new Label("Tour : ?");
        turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        playersBox = new VBox(6);
        playersBox.setFillWidth(true);

        getChildren().addAll(turnLabel, playersBox);

        undoBtn = new Button("Undo");
        redoBtn = new Button("Redo");
        rotateBtn = new Button("Rotate");

        undoBtn.setMaxWidth(Double.MAX_VALUE);
        redoBtn.setMaxWidth(Double.MAX_VALUE);
        rotateBtn.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(objectiveImg, undoBtn, redoBtn, rotateBtn);
    }

    public void setObjective(Objective obj) {
        objectiveImg.setImage(obj == null ? null : Images.hudObjective(obj));
    }

    public void setTurnAndPlayers(Player current, java.util.List<Player> players) {
        if (current == null) {
            turnLabel.setText("Tour : -");
        } else {
            turnLabel.setText("Tour : " + current.getName());
        }

        playersBox.getChildren().clear();

        for (Player p : players) {
            int total = p.getObjectives().size();
            int done = Math.min(p.getCurrentObjectiveIndex(), total);

            Label name = new Label(p.getName() + " (" + p.getColor() + ")");
            name.setStyle("-fx-font-weight: bold;");

            Label prog = new Label("Objectifs : " + done + " / " + total);

            // objectif courant (texte)
            String cur = (p.hasCompletedAllObjectives() || p.getCurrentObjective() == null)
                    ? "Terminé"
                    : p.getCurrentObjective().getObjective().getName();
            Label curObj = new Label("Cible : " + cur);

            VBox card = new VBox(2, name, prog, curObj);
            card.setPadding(new Insets(8));
            card.setStyle("-fx-border-color: rgba(255,255,255,0.25); -fx-border-radius: 10; -fx-background-radius: 10;");

            if (current != null && p.getId() == current.getId()) {
                card.setStyle(card.getStyle() + "-fx-border-color: gold; -fx-border-width: 2;");
            }

            playersBox.getChildren().add(card);
        }
    }
    public void setSpareTile(Tile tile) {
        if (tile == null) {
            spareImg.setImage(null);
            spareImg.setRotate(0);
            return;
        }
        // image de base (sans position -> on prend une image vide selon le type)
        spareImg.setImage(Images.spareTileImage(tile));
        spareImg.setRotate(rotationDegrees(tile));
    }

    private double rotationDegrees(Tile tile) {
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
                if (!o.contains(Direction.NORTH)) yield 0;
                if (!o.contains(Direction.WEST)) yield 90;
                if (!o.contains(Direction.SOUTH)) yield 180;
                yield 270;
            }
        };
    }

    // --- handlers (controller only) ---
    public void onUndo(Runnable r) { undoBtn.setOnAction(e -> r.run()); }
    public void onRedo(Runnable r) { redoBtn.setOnAction(e -> r.run()); }
    public void onRotate(Runnable r) { rotateBtn.setOnAction(e -> r.run()); }
    
    public void setUndoDisabled(boolean v) { undoBtn.setDisable(v); }
    public void setRedoDisabled(boolean v) { redoBtn.setDisable(v); }
}
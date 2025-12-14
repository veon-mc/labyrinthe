package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.Direction;

import java.util.function.BiConsumer;

public class InsertionPadView {

    private BiConsumer<Direction, Integer> onInsert;
    private boolean disabled;

    public void setOnInsert(BiConsumer<Direction, Integer> handler) {
        this.onInsert = handler;
    }

    public void setDisabledAll(boolean disabled) {
        this.disabled = disabled;
    }

    public void fireInsert(Direction side, int index) {
        System.out.println("FIRE INSERT " + side + " " + index);

        if (disabled) return;
        if (onInsert != null) onInsert.accept(side, index);
    }
}
package g64889.dev3.labyrinthe.view;

import g64889.dev3.labyrinthe.model.enumeration.Direction;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * Manages the insertion arrows displayed around the board.
 * <p>
 * This class stores references to arrow nodes and controls whether each
 * arrow is enabled/disabled depending on the game rules.
 * The controller can register a callback to be notified when an insertion
 * is requested by the user.
 * <p>
 * This class does not create the arrow nodes itself: it only registers and
 * updates them.
 */
public class InsertionPadView {

    /**
     * Handler called when the user requests an insertion.
     * The two arguments are the insertion side and the insertion index.
     */
    private BiConsumer<Direction, Integer> onInsert;

    /** Global disable flag for all insertion actions. */
    private boolean disabled = false;

    /**
     * Stores arrow nodes by side and index.
     * side -> (index -> arrow node)
     */
    private final Map<Direction, Map<Integer, Node>> arrows = new HashMap<>();

    /* ===================== EVENTS ===================== */

    /**
     * Sets the handler called when an insertion is requested.
     *
     * @param handler a consumer receiving (side, index)
     */
    public void setOnInsert(BiConsumer<Direction, Integer> handler) {
        this.onInsert = handler;
    }

    /**
     * Triggers an insertion request programmatically.
     * <p>
     * This method checks the global disabled state and the arrow node state
     * before calling the registered handler.
     *
     * @param side  the insertion side
     * @param index the insertion index
     */
    public void fireInsert(Direction side, int index) {
        if (disabled) return;

        Node arrow = getArrow(side, index);
        if (arrow != null && arrow.isDisable()) return;

        if (onInsert != null) {
            onInsert.accept(side, index);
        }
    }

    /**
     * Enables or disables all arrow nodes at once.
     * <p>
     * When disabled, arrows become semi-transparent.
     *
     * @param disabled true to disable all arrows
     */
    public void setDisabledAll(boolean disabled) {
        this.disabled = disabled;

        for (Map<Integer, Node> m : arrows.values()) {
            for (Node n : m.values()) {
                n.setDisable(disabled);
                n.setOpacity(disabled ? 0.35 : 1.0);
            }
        }
    }

    /**
     * Registers an arrow node for a given side and index.
     * <p>
     * This method should be called once when creating the arrow nodes.
     *
     * @param side  the insertion side
     * @param index the insertion index
     * @param node  the JavaFX node representing the arrow
     */
    public void registerArrow(Direction side, int index, Node node) {
        arrows.computeIfAbsent(side, k -> new HashMap<>()).put(index, node);
    }

    /**
     * Returns the registered arrow node for the given side and index.
     *
     * @param side  the insertion side
     * @param index the insertion index
     * @return the arrow node, or null if not registered
     */
    private Node getArrow(Direction side, int index) {
        Map<Integer, Node> m = arrows.get(side);
        return (m == null) ? null : m.get(index);
    }

    /**
     * Enables or disables a specific arrow, unless the whole pad is globally disabled.
     * <p>
     * When disabled, the arrow becomes semi-transparent.
     *
     * @param side    the insertion side
     * @param index   the insertion index
     * @param enabled true to enable the arrow
     */
    public void setArrowEnabled(Direction side, int index, boolean enabled) {
        Node n = getArrow(side, index);
        if (n == null) return;

        // If globally disabled, keep current state unchanged.
        if (disabled) return;

        n.setDisable(!enabled);
        n.setOpacity(enabled ? 1.0 : 0.35);
    }

    /**
     * Enables/disables all insertion arrows using the given decision callback.
     * The callback receives (side, index) and must return true if the arrow is allowed.
     *
     * @param canInsert function that returns whether an insertion is allowed
     */
    public void updateInsertionArrows(BiPredicate<Direction, Integer> canInsert) {
        int[] idx = {1, 3, 5};
        Direction[] sides = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for (Direction side : sides) {
            for (int i : idx) {
                setArrowEnabled(side, i, canInsert.test(side, i));
            }
        }
    }
    }
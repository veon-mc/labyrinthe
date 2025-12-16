package g64889.dev3.labyrinthe.model;

/**
 * Represents an observer in the Observer design pattern.
 * <p>
 * An observer is notified by an {@link Observable} whenever
 * the observed object's state changes.
 */
public interface Observer {

    /**
     * Called when the observed object changes its state.
     */
    void update();
}
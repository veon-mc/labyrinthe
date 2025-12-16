package g64889.dev3.labyrinthe.model;

/**
 * Represents an observable object in the Observer design pattern.
 * <p>
 * An observable object allows {@link Observer} instances to register
 * and be notified when its state changes.
 */
public interface Observable {

    /**
     * Registers an observer to be notified of state changes.
     *
     * @param observer the observer to add
     */
    void addObserver(Observer observer);

    /**
     * Removes a previously registered observer.
     *
     * @param observer the observer to remove
     */
    void removeObserver(Observer observer);

    /**
     * Notifies all registered observers of a state change.
     */
    void notifyObservers();
}
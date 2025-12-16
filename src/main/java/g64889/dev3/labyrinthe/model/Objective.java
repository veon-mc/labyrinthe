package g64889.dev3.labyrinthe.model;

/**
 * Represents an objective in the Labyrinth game.
 * <p>
 * An objective is identified by a name and is placed on a tile.
 * Players must reach tiles containing their current objective
 * to progress in the game.
 */
public class Objective {

    /** Name of the objective. */
    private String name;

    /**
     * Creates a new objective with the given name.
     *
     * @param name the name of the objective
     * @throws IllegalArgumentException if the name is null or empty
     */
    public Objective(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    /**
     * Returns the name of the objective.
     *
     * @return the objective name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of the objective.
     *
     * @return a string describing the objective
     */
    @Override
    public String toString() {
        return "Objective{" + name + '}';
    }
}
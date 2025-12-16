package g64889.dev3.labyrinthe.model;

/**
 * Represents a card containing a single objective.
 * <p>
 * Objective cards are assigned to players and define the sequence
 * of objectives they must complete during the game.
 */
public class ObjectiveCard {

    /** Objective associated with this card. */
    private Objective objective;

    /**
     * Creates a new objective card.
     *
     * @param objective the objective carried by the card
     * @throws IllegalArgumentException if the objective is null
     */
    public ObjectiveCard(Objective objective) {
        if (objective == null) {
            throw new IllegalArgumentException("Objective cannot be null");
        }
        this.objective = objective;
    }

    /**
     * Returns the objective associated with this card.
     *
     * @return the objective
     */
    public Objective getObjective() {
        return objective;
    }

    /**
     * Returns a string representation of the objective card.
     *
     * @return a string describing the objective card
     */
    @Override
    public String toString() {
        return "ObjectiveCard{" + objective + '}';
    }
}
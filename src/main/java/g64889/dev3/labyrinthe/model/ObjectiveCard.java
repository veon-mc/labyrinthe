package g64889.dev3.labyrinthe.model;

public class ObjectiveCard {
    private Objective objective;

    public ObjectiveCard(Objective objective) {
        if (objective == null) {
            throw new IllegalArgumentException("Objective cannot be null");
        }
        this.objective = objective;
    }

    public Objective getObjective() {
        return objective;
    }

    @Override
    public String toString() {
        return "ObjectiveCard{" + objective + '}';
    }
}

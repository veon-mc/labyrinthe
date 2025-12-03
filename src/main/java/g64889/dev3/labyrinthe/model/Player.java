package g64889.dev3.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final int id;
    private final String name;
    private final PlayerColor color;
    private final Position startPosition;
    private Position currentPosition;
    private List<ObjectiveCard> objectives;
    private int currentObjectiveIndex;

    public Player(int id, String name, PlayerColor color, Position startPosition, Position currentPosition, List<ObjectiveCard> objectives, int currentObjectiveIndex) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
        this.objectives = new ArrayList<>();
        this.currentObjectiveIndex = 0;
    }

    public ObjectiveCard getCurrentObjective() {
        return objectives.get(currentObjectiveIndex);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position position) {
        this.currentPosition = position;
    }

    public void setObjectives(List<ObjectiveCard> objectives) {
        this.objectives = new ArrayList<>(objectives);
        this.currentObjectiveIndex = 0;
    }

    public List<ObjectiveCard> getObjectives() {
        return new ArrayList<>(objectives);
    }

    public boolean hasCompletedAllObjectives() {
        return currentObjectiveIndex >= objectives.size();
    }

    public void validateCurrentObjective() {
        if (!hasCompletedAllObjectives()) {
            currentObjectiveIndex++;
        }
    }
}

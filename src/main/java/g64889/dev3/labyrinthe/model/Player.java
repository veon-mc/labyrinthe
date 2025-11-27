package g64889.dev3.labyrinthe.model;

import java.util.List;

public class Player {
    private int id;
    private String name;
    private PlayerColor color;
    private Position startPosition;
    private Position currentPosition;
    private List<ObjectiveCard> objectives;
    private int currentObjectiveIndex;

    public Player(int id, String name, PlayerColor color, Position startPosition, Position currentPosition, List<ObjectiveCard> objectives, int currentObjectiveIndex) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.startPosition = startPosition;
        this.currentPosition = currentPosition;
        this.objectives = objectives;
        this.currentObjectiveIndex = currentObjectiveIndex;
    }

    public ObjectiveCard getCurrentObjective() {
        return objectives.get(currentObjectiveIndex);
    }
}

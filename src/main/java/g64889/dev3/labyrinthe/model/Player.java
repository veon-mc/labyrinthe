package g64889.dev3.labyrinthe.model;

import g64889.dev3.labyrinthe.model.enumeration.PlayerColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Labyrinth game.
 * <p>
 * A player has an identifier, a name, a color, a start position,
 * a current position, and a list of objective cards to complete.
 * The player progresses through objectives in order.
 */
public class Player {

    /** Unique identifier of the player. */
    private final int id;

    /** Display name of the player. */
    private final String name;

    /** Color associated with the player. */
    private final PlayerColor color;

    /** Starting position of the player. */
    private final Position startPosition;

    /** Current position of the player on the board. */
    private Position currentPosition;

    /** List of objectives assigned to the player. */
    private List<ObjectiveCard> objectives;

    /** Index of the current objective to complete. */
    private int currentObjectiveIndex;

    /**
     * Creates a new player with the given parameters.
     *
     * @param id                   the player identifier
     * @param name                 the player name
     * @param color                the player color
     * @param startPosition        the starting position
     * @param currentPosition      the initial current position (or null to use start position)
     * @param objectives            the list of objectives (copied internally)
     * @param currentObjectiveIndex the starting objective index
     */
    public Player(int id, String name, PlayerColor color,
                  Position startPosition,
                  Position currentPosition,
                  List<ObjectiveCard> objectives,
                  int currentObjectiveIndex) {

        this.id = id;
        this.name = name;
        this.color = color;
        this.startPosition = startPosition;

        this.currentPosition = (currentPosition != null)
                ? currentPosition
                : startPosition;

        this.objectives = (objectives != null)
                ? new ArrayList<>(objectives)
                : new ArrayList<>();

        this.currentObjectiveIndex = Math.max(0, currentObjectiveIndex);
        if (this.currentObjectiveIndex > this.objectives.size()) {
            this.currentObjectiveIndex = this.objectives.size();
        }
    }

    /**
     * Returns the current objective card of the player.
     *
     * @return the current objective card, or null if none
     */
    public ObjectiveCard getCurrentObjective() {
        if (objectives == null || objectives.isEmpty()) return null;
        if (currentObjectiveIndex < 0 || currentObjectiveIndex >= objectives.size()) return null;
        return objectives.get(currentObjectiveIndex);
    }

    /** @return the player identifier. */
    public int getId() {
        return id;
    }

    /** @return the player name. */
    public String getName() {
        return name;
    }

    /** @return the player's starting position. */
    public Position getStartPosition() {
        return startPosition;
    }

    /** @return the player's current position. */
    public Position getCurrentPosition() {
        return currentPosition;
    }

    /** @return the index of the current objective. */
    public int getCurrentObjectiveIndex() {
        return currentObjectiveIndex;
    }

    /**
     * Sets the current position of the player.
     *
     * @param position the new position
     */
    public void setCurrentPosition(Position position) {
        this.currentPosition = position;
    }

    /**
     * Assigns a new list of objectives to the player.
     * <p>
     * The objective index is reset to the first objective.
     *
     * @param objectives the objectives to assign
     */
    public void setObjectives(List<ObjectiveCard> objectives) {
        this.objectives = new ArrayList<>(objectives);
        this.currentObjectiveIndex = 0;
    }

    /**
     * Returns a copy of the player's objectives.
     *
     * @return a list of objective cards
     */
    public List<ObjectiveCard> getObjectives() {
        return new ArrayList<>(objectives);
    }

    /**
     * Indicates whether the player has completed all objectives.
     *
     * @return true if all objectives are completed
     */
    public boolean hasCompletedAllObjectives() {
        return currentObjectiveIndex >= objectives.size();
    }

    /**
     * Validates the current objective and moves to the next one.
     */
    public void validateCurrentObjective() {
        if (!hasCompletedAllObjectives()) {
            currentObjectiveIndex++;
        }
    }

    /**
     * Sets the current objective index.
     *
     * @param index the objective index
     */
    public void setCurrentObjectiveIndex(int index) {
        this.currentObjectiveIndex = index;
    }

    /** @return the player's color. */
    public PlayerColor getColor() {
        return color;
    }
}
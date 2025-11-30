package g64889.dev3.labyrinthe.model;

import java.util.List;

public class LabyrinthGame {
    private Board board;
    private List<Player> players;
    private int currentPlayerIndex;
    private Tile spareTitle;
    private boolean gameOver;
    private Player winner;

    public void startGame(int nbPLayers, List<AiLevel> aiLevels) {

    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Board getBoard() {
        return board;
    }

    public Player getWinner() {
        return winner;
    }
}

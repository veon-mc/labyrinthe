package g64889.dev3.labyrinthe.model;

import g64889.dev3.labyrinthe.model.command.CommandManager;
import g64889.dev3.labyrinthe.model.command.InsertTileCommand;
import g64889.dev3.labyrinthe.model.command.MovePlayerCommand;
import g64889.dev3.labyrinthe.model.enumeration.Direction;
import g64889.dev3.labyrinthe.model.enumeration.GameMode;
import g64889.dev3.labyrinthe.model.enumeration.PlayerColor;
import g64889.dev3.labyrinthe.model.enumeration.TileType;

import java.util.*;

/**
 * Facade of the Labyrinth game model.
 * <p>
 * This class coordinates the main game state: board, players, objectives,
 * turn management, tile insertion, movement, undo/redo (Command pattern),
 * and observer notifications.
 * <p>
 * It exposes high-level methods used by controllers (console/JavaFX) and
 * keeps the game rules enforced inside the model (MVC friendly).
 */
public class LabyrinthGame implements Observable {

    private Board board;
    private final List<Player> players;
    private int currentPlayerIndex;
    private Tile spareTile;
    private boolean gameOver;
    private Player winner;

    private final List<Objective> objectives;
    private final PathFinder pathFinder;

    private Direction lastInsertionSide;
    private int lastInsertionIndex;
    private boolean tileInserted;

    private final CommandManager commandManager;
    private final List<Observer> observers = new ArrayList<>();

    private GameMode mode = GameMode.STANDARD;

    /**
     * Names allowed for mobile tiles with objectives (T tiles).
     */
    private static final Set<String> ALLOWED_T = Set.of(
            "fantôme", "gnome", "dragon", "fée", "chauve-souris", "génie"
    );

    /**
     * Names allowed for mobile tiles with objectives (L tiles).
     */
    private static final Set<String> ALLOWED_L = Set.of(
            "hibou", "scarabée", "rat", "araignée", "papillon", "lézard"
    );

    /**
     * Creates a new game facade with an empty initial state.
     * <p>
     * The game is fully initialized by calling {@link #startGame(int)}.
     */
    public LabyrinthGame() {
        this.commandManager = new CommandManager();
        this.board = null;
        this.objectives = new ArrayList<>();
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.spareTile = null;
        this.gameOver = false;
        this.winner = null;
        this.pathFinder = new PathFinder();
        this.tileInserted = false;
    }

    /**
     * Initializes and starts a new game.
     * <p>
     * This resets the model state, creates the board and players,
     * distributes objective cards, and notifies observers.
     *
     * @param nbPlayers number of players (typically 2 to 4)
     */
    public void startGame(int nbPlayers) {
        initObjectives();
        initPlayers(nbPlayers);
        initBoard();
        distributeCards(nbPlayers);

        currentPlayerIndex = 0;
        gameOver = false;
        winner = null;
        tileInserted = false;

        notifyObservers();
    }

    /**
     * Initializes the list of objectives used in the game.
     *
     * @throws IllegalStateException if the objective list does not contain exactly 24 elements
     */
    private void initObjectives() {
        objectives.clear();

        String[] names = {
                "fantôme","gnome","dragon","fée","chauve-souris","génie",
                "hibou","scarabée","rat","araignée","papillon","lézard",
                "grimoire","bourse d'or","carte","couronne","clefs","ossements",
                "bague","coffre au trésor","émeraude","épée","chandelier","heaume"
        };

        for (String n : names) objectives.add(new Objective(n));

        if (objectives.size() != 24)
            throw new IllegalStateException("Il faut 24 objectifs");
    }

    /**
     * Returns the fixed objective that must appear at a given fixed board location.
     *
     * @param row row index
     * @param col column index
     * @return the objective for this fixed cell, or null if none
     */
    private Objective fixedObjectiveAt(int row, int col) {
        if (row == 0 && col == 2) return new Objective("carte");
        if (row == 0 && col == 4) return new Objective("couronne");

        if (row == 2 && col == 0) return new Objective("clefs");
        if (row == 4 && col == 0) return new Objective("chandelier");

        if (row == 6 && col == 2) return new Objective("heaume");
        if (row == 6 && col == 4) return new Objective("grimoire");

        if (row == 2 && col == 6) return new Objective("bague");
        if (row == 4 && col == 6) return new Objective("ossements");

        if (row == 2 && col == 2) return new Objective("coffre au trésor");
        if (row == 2 && col == 4) return new Objective("émeraude");
        if (row == 4 && col == 2) return new Objective("bourse d'or");
        if (row == 4 && col == 4) return new Objective("épée");

        return null;
    }

    /**
     * Initializes the board: fixed tiles + mobile tiles + spare tile.
     * <p>
     * Fixed positions follow the classic 7x7 Labyrinth layout.
     */
    private void initBoard() {
        int size = 7;
        Tile[][] grid = new Tile[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (isFixedPosition(row, col)) {
                    TileType type = determineTileType(row, col);
                    EnumSet<Direction> openings = getOpeningsForType(type, row, col);

                    Objective obj = isCorner(row, col) ? null : fixedObjectiveAt(row, col);
                    grid[row][col] = new Tile(type, openings, true, obj);
                }
            }
        }

        Set<String> usedFixed = new HashSet<>();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (isFixedPosition(r, c) && !isCorner(r, c)) {
                    usedFixed.add(fixedObjectiveAt(r, c).getName());
                }
            }
        }

        List<Objective> remaining = new ArrayList<>();
        for (Objective o : objectives) {
            if (!usedFixed.contains(o.getName())) remaining.add(o);
        }

        if (remaining.size() != 12) {
            throw new IllegalStateException("Il doit rester 12 objectifs mobiles, trouvé: " + remaining.size());
        }

        Collections.shuffle(remaining);

        List<Objective> poolT = new ArrayList<>();
        List<Objective> poolL = new ArrayList<>();

        for (Objective o : objectives) {
            String n = o.getName();
            if (ALLOWED_T.contains(n)) poolT.add(o);
            else if (ALLOWED_L.contains(n)) poolL.add(o);
        }

        Collections.shuffle(poolT);
        Collections.shuffle(poolL);

        List<Tile> mobileTiles = createMobileTiles(poolT, poolL);
        Collections.shuffle(mobileTiles);

        int m = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (!isFixedPosition(row, col)) {
                    grid[row][col] = mobileTiles.get(m++);
                }
            }
        }

        spareTile = mobileTiles.get(m);
        board = new Board(grid, spareTile);
    }

    /**
     * Checks whether a position is fixed (not movable).
     *
     * @param row row index
     * @param col column index
     * @return true if the position is fixed
     */
    private boolean isFixedPosition(int row, int col) {
        return (row % 2 == 0 && col % 2 == 0);
    }

    /**
     * Checks whether a position is a corner.
     *
     * @param row row index
     * @param col column index
     * @return true if the position is a corner
     */
    private boolean isCorner(int row, int col) {
        return (row == 0 || row == 6) && (col == 0 || col == 6);
    }

    /**
     * Determines the type of fixed tile at a given position.
     *
     * @param row row index
     * @param col column index
     * @return tile type (L for corners, otherwise T)
     */
    private TileType determineTileType(int row, int col) {
        if (isCorner(row, col)) return TileType.L;
        return TileType.T;
    }

    /**
     * Computes the openings of a fixed tile based on its type and position.
     *
     * @param type tile type
     * @param row  row index
     * @param col  column index
     * @return set of openings (directions)
     */
    private EnumSet<Direction> getOpeningsForType(TileType type, int row, int col) {
        EnumSet<Direction> openings = EnumSet.noneOf(Direction.class);

        if (type == TileType.L) {
            if (row == 0 && col == 0) {
                openings.add(Direction.SOUTH);
                openings.add(Direction.EAST);
            } else if (row == 0 && col == 6) {
                openings.add(Direction.SOUTH);
                openings.add(Direction.WEST);
            } else if (row == 6 && col == 0) {
                openings.add(Direction.NORTH);
                openings.add(Direction.EAST);
            } else if (row == 6 && col == 6) {
                openings.add(Direction.NORTH);
                openings.add(Direction.WEST);
            }
        } else if (type == TileType.T) {
            if (row == 0) {
                openings.add(Direction.SOUTH);
                openings.add(Direction.EAST);
                openings.add(Direction.WEST);
            } else if (row == 6) {
                openings.add(Direction.NORTH);
                openings.add(Direction.EAST);
                openings.add(Direction.WEST);
            } else if (col == 0) {
                openings.add(Direction.NORTH);
                openings.add(Direction.SOUTH);
                openings.add(Direction.EAST);
            } else if (col == 6) {
                openings.add(Direction.NORTH);
                openings.add(Direction.SOUTH);
                openings.add(Direction.WEST);
            } else if (row == 2 && col == 2) {
                openings.add(Direction.NORTH);
                openings.add(Direction.EAST);
                openings.add(Direction.SOUTH);
            } else if (row == 2 && col == 4) {
                openings.add(Direction.EAST);
                openings.add(Direction.SOUTH);
                openings.add(Direction.WEST);
            } else if (row == 4 && col == 4) {
                openings.add(Direction.NORTH);
                openings.add(Direction.SOUTH);
                openings.add(Direction.WEST);
            } else if (row == 4 && col == 2) {
                openings.add(Direction.NORTH);
                openings.add(Direction.EAST);
                openings.add(Direction.WEST);
            }
        }

        return openings;
    }

    /**
     * Creates all mobile tiles used to fill non-fixed positions and the spare tile.
     *
     * @param poolT objectives allowed on T mobile tiles
     * @param poolL objectives allowed on L mobile tiles
     * @return list of mobile tiles (including the future spare tile)
     */
    private List<Tile> createMobileTiles(List<Objective> poolT, List<Objective> poolL) {
        List<Tile> tiles = new ArrayList<>();

        if (poolT.size() < 6) {
            throw new IllegalStateException("poolT doit contenir au moins 6 objectifs (T-goals)");
        }
        if (poolL.size() < 6) {
            throw new IllegalStateException("poolL doit contenir au moins 6 objectifs (L-goals)");
        }

        for (int i = 0; i < 6; i++) {
            tiles.add(new Tile(TileType.T, randomOpeningsT(), false, poolT.get(i)));
        }

        for (int i = 0; i < 12; i++) {
            tiles.add(new Tile(TileType.I, randomOpeningsI(), false, null));
        }

        for (int i = 0; i < 6; i++) {
            tiles.add(new Tile(TileType.L, randomOpeningsL(), false, poolL.get(i)));
        }

        for (int i = 0; i < 10; i++) {
            tiles.add(new Tile(TileType.L, randomOpeningsL(), false, null));
        }

        return tiles;
    }

    /** Generates a random opening set for a T tile (3 openings). */
    private EnumSet<Direction> randomOpeningsT() {
        Random rand = new Random();
        int choice = rand.nextInt(4);
        return switch (choice) {
            case 0 -> EnumSet.of(Direction.NORTH, Direction.EAST, Direction.SOUTH);
            case 1 -> EnumSet.of(Direction.EAST, Direction.SOUTH, Direction.WEST);
            case 2 -> EnumSet.of(Direction.SOUTH, Direction.WEST, Direction.NORTH);
            default -> EnumSet.of(Direction.WEST, Direction.NORTH, Direction.EAST);
        };
    }

    /** Generates a random opening set for an I tile (2 opposite openings). */
    private EnumSet<Direction> randomOpeningsI() {
        Random rand = new Random();
        if (rand.nextBoolean()) {
            return EnumSet.of(Direction.NORTH, Direction.SOUTH);
        } else {
            return EnumSet.of(Direction.EAST, Direction.WEST);
        }
    }

    /** Generates a random opening set for an L tile (2 adjacent openings). */
    private EnumSet<Direction> randomOpeningsL() {
        Random rand = new Random();
        int choice = rand.nextInt(4);
        return switch (choice) {
            case 0 -> EnumSet.of(Direction.NORTH, Direction.EAST);
            case 1 -> EnumSet.of(Direction.EAST, Direction.SOUTH);
            case 2 -> EnumSet.of(Direction.SOUTH, Direction.WEST);
            default -> EnumSet.of(Direction.WEST, Direction.NORTH);
        };
    }

    /**
     * Initializes all players and their starting positions.
     *
     * @param nbPlayers number of players
     */
    private void initPlayers(int nbPlayers) {
        players.clear();

        Position[] startPositions = {
                new Position(6, 6), new Position(0, 0),
                new Position(0, 6), new Position(6, 0)
        };

        PlayerColor[] colors = PlayerColor.values();

        for (int i = 0; i < nbPlayers; i++) {
            String playerName = "Player " + (i + 1);
            Player player = new Player(
                    i + 1,
                    playerName,
                    colors[i],
                    startPositions[i],
                    startPositions[i],
                    new ArrayList<>(),
                    0
            );
            players.add(player);
        }
    }

    /**
     * Distributes objective cards evenly between players.
     *
     * @param nbPlayers number of players
     */
    private void distributeCards(int nbPlayers) {
        List<ObjectiveCard> allCards = new ArrayList<>();
        for (Objective obj : objectives) {
            allCards.add(new ObjectiveCard(obj));
        }

        ObjectiveDeck deck = new ObjectiveDeck(allCards);
        int cardsPerPlayer = 24 / nbPlayers;

        for (Player player : players) {
            List<ObjectiveCard> playerCards = new ArrayList<>();
            for (int j = 0; j < cardsPerPlayer; j++) {
                if (!deck.isEmpty()) {
                    playerCards.add(deck.draw());
                }
            }
            player.setObjectives(playerCards);
        }
    }

    /**
     * Inserts the spare tile into the board (single action version).
     * <p>
     * Validates turn rules: game not over, not already inserted, valid position,
     * and not the opposite of the previous insertion.
     *
     * @param side  insertion side
     * @param index insertion index (odd indices only)
     */
    public void insertTile(Direction side, int index) {
        if (gameOver) {
            throw new IllegalStateException("Game is over");
        }
        if (tileInserted) {
            throw new IllegalStateException("Tile already inserted this turn");
        }
        if (!canInsertTile(side, index)) {
            throw new IllegalArgumentException("Invalid tile insertion position");
        }

        board.insertSpareTile(side, index);
        updatePlayersForInsertion(side, index);

        spareTile = board.getSpareTile();
        lastInsertionSide = side;
        lastInsertionIndex = index;
        tileInserted = true;

        notifyObservers();
    }

    /**
     * Inserts the spare tile using the command history (undo/redo support).
     *
     * @param side  insertion side
     * @param index insertion index
     */
    public void doInsertTile(Direction side, int index) {
        commandManager.executeCommand(new InsertTileCommand(this, side, index));
    }

    /**
     * Checks if an insertion is allowed by the rules.
     * <p>
     * Only odd indices are valid. The insertion cannot be the exact opposite
     * of the previous insertion (same index + opposite side).
     *
     * @param side  insertion side
     * @param index insertion index
     * @return true if insertion is allowed
     */
    public boolean canInsertTile(Direction side, int index) {
        if (index % 2 == 0 || index < 1 || index >= 7) {
            return false;
        }
        if (lastInsertionSide != null) {
            Direction oppositeSide = getOppositeSide(side);
            return lastInsertionSide != oppositeSide || lastInsertionIndex != index;
        }
        return true;
    }

    /**
     * Returns the opposite direction of a given side.
     *
     * @param side a direction
     * @return the opposite direction
     */
    private Direction getOppositeSide(Direction side) {
        return switch (side) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
        };
    }

    /**
     * Updates player positions after an insertion (players on the shifted row/column).
     *
     * @param side  insertion side
     * @param index insertion index
     */
    private void updatePlayersForInsertion(Direction side, int index) {
        int size = board.getSize();

        for (Player player : players) {
            Position pos = player.getCurrentPosition();
            int row = pos.getRow();
            int col = pos.getCol();

            switch (side) {
                case WEST -> {
                    if (row == index) {
                        col = (col == size - 1) ? 0 : col + 1;
                        player.setCurrentPosition(new Position(row, col));
                    }
                }
                case EAST -> {
                    if (row == index) {
                        col = (col == 0) ? size - 1 : col - 1;
                        player.setCurrentPosition(new Position(row, col));
                    }
                }
                case NORTH -> {
                    if (col == index) {
                        row = (row == size - 1) ? 0 : row + 1;
                        player.setCurrentPosition(new Position(row, col));
                    }
                }
                case SOUTH -> {
                    if (col == index) {
                        row = (row == 0) ? size - 1 : row - 1;
                        player.setCurrentPosition(new Position(row, col));
                    }
                }
            }
        }
    }

    /**
     * Moves the current player to a target cell.
     * <p>
     * A tile must have been inserted first. The target must be reachable,
     * except when the target is the current cell (stay on place).
     *
     * @param targetRow target row
     * @param targetCol target column
     */
    public void movePlayer(int targetRow, int targetCol) {
        if (gameOver) {
            throw new IllegalStateException("Game is over");
        }
        if (!tileInserted) {
            throw new IllegalStateException("Must insert tile before moving");
        }

        Player current = getCurrentPlayer();
        Position target = new Position(targetRow, targetCol);

        if (!current.getCurrentPosition().equals(target)) {
            if (!canMoveTo(current.getCurrentPosition(), target)) {
                throw new IllegalArgumentException("Invalid move");
            }
        }

        current.setCurrentPosition(target);
        checkObjectiveReached(current);

        checkGameOver();
        if (!gameOver) {
            nextPlayer();
        }

        notifyObservers();
    }

    /**
     * Moves the current player using the command history (undo/redo support).
     *
     * @param row target row
     * @param col target column
     */
    public void doMovePlayer(int row, int col) {
        commandManager.executeCommand(new MovePlayerCommand(this, row, col));
    }

    /**
     * Checks whether the current objective is reached and validates it if needed.
     *
     * @param player the player to check
     */
    private void checkObjectiveReached(Player player) {
        Tile currentTile = board.getTileAt(player.getCurrentPosition());

        if (!player.hasCompletedAllObjectives()) {
            ObjectiveCard currentCard = player.getCurrentObjective();

            if (currentTile.getObjective() != null && currentCard != null) {
                String tileName = currentTile.getObjective().getName();
                String wantedName = currentCard.getObjective().getName();

                if (tileName.equals(wantedName)) {
                    player.validateCurrentObjective();
                }
            }
        }
    }

    /**
     * Checks if the game is over and sets the winner accordingly.
     * <p>
     * In SIMPLIFIED mode, completing objectives is enough to win.
     * In STANDARD mode, the player must also return to the start position.
     */
    private void checkGameOver() {
        for (Player player : players) {
            if (!player.hasCompletedAllObjectives()) continue;

            if (mode == GameMode.SIMPLIFIED) {
                gameOver = true;
                winner = player;
                break;
            }

            if (player.getCurrentPosition().equals(player.getStartPosition())) {
                gameOver = true;
                winner = player;
                break;
            }
        }
    }

    /**
     * Checks if a target cell is reachable from a source cell.
     *
     * @param from starting position
     * @param to   target position
     * @return true if the target is reachable
     */
    public boolean canMoveTo(Position from, Position to) {
        Set<Position> reachable = pathFinder.getReachablePositions(board, from);
        return reachable.contains(to);
    }

    /**
     * Ends the current player's turn and moves to the next player.
     */
    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        tileInserted = false;
    }

    /**
     * Rotates the spare tile clockwise and notifies observers.
     */
    public void rotateSpareTile() {
        spareTile.rotate();
        notifyObservers();
    }

    /** @return the current player. */
    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }

    /** @return the game board. */
    public Board getBoard() { return board; }

    /** @return the current spare tile. */
    public Tile getSpareTile() { return spareTile; }

    /** @return true if the game is finished. */
    public boolean isGameOver() { return gameOver; }

    /** @return the winner player, or null if none. */
    public Player getWinner() { return winner; }

    /**
     * Returns an unmodifiable view of the players list.
     *
     * @return players list (read-only)
     */
    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }

    /** @return true if the tile has already been inserted during the current turn. */
    public boolean isTileInserted() { return tileInserted; }

    /**
     * Undoes the most recent command (if any) and notifies observers.
     */
    public void undo() {
        commandManager.undo();
        notifyObservers();
    }

    /**
     * Redoes the most recently undone command (if any) and notifies observers.
     */
    public void redo() {
        commandManager.redo();
        notifyObservers();
    }

    /** @return true if undo is possible. */
    public boolean canUndo() { return commandManager.canUndo(); }

    /** @return true if redo is possible. */
    public boolean canRedo() { return commandManager.canRedo(); }

    /** @return the last insertion side, or null if none. */
    public Direction getLastInsertionSide() { return lastInsertionSide; }

    /** @return the last insertion index. */
    public int getLastInsertionIndex() { return lastInsertionIndex; }

    /**
     * Returns the reachable positions for the current player (useful for AI).
     *
     * @return set of reachable positions from the current position
     */
    public Set<Position> getReachableForCurrentPlayer() {
        Player p = getCurrentPlayer();
        return pathFinder.getReachablePositions(board, p.getCurrentPosition());
    }

    /** Sets game over state (mainly used for tests or controller logic). */
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    /**
     * Sets the spare tile both in the facade and in the board.
     *
     * @param tile new spare tile
     */
    public void setSpareTile(Tile tile) {
        this.spareTile = tile;
        board.setSpareTile(tile);
    }

    /** Sets the winner player. */
    public void setWinner(Player winner) { this.winner = winner; }

    /**
     * Stores insertion metadata (used by undo/redo restoration).
     *
     * @param side  insertion side
     * @param index insertion index
     */
    public void setLastInsertion(Direction side, int index) {
        this.lastInsertionSide = side;
        this.lastInsertionIndex = index;
    }

    /** @return current player index. */
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }

    /** Sets current player index (used for restoration). */
    public void setCurrentPlayerIndex(int index) { this.currentPlayerIndex = index; }

    /** Sets whether a tile was inserted this turn (used for restoration). */
    public void setTileInserted(boolean inserted) { this.tileInserted = inserted; }

    /** @return the path finder used by the game. */
    public PathFinder getPathFinder() { return pathFinder; }

    /**
     * Sets the game mode (null defaults to STANDARD).
     *
     * @param mode game mode
     */
    public void setMode(GameMode mode) {
        this.mode = (mode == null) ? GameMode.STANDARD : mode;
    }

    /**
     * Copies current players positions (snapshot for undo/redo).
     *
     * @return list of positions (in players order)
     */
    public List<Position> copyPlayerPositions() {
        List<Position> positions = new ArrayList<>();
        for (Player p : players) {
            positions.add(p.getCurrentPosition());
        }
        return positions;
    }

    /**
     * Copies the board grid reference (snapshot for undo/redo).
     *
     * @return a 2D array of tiles representing the grid
     */
    public Tile[][] copyGrid() {
        int size = board.getSize();
        Tile[][] copy = new Tile[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                copy[row][col] = board.getTileAt(new Position(row, col));
            }
        }
        return copy;
    }

    /**
     * Restores the board grid (used for undo/redo).
     *
     * @param grid grid to restore
     */
    public void restoreGrid(Tile[][] grid) {
        board.setGrid(grid);
    }

    /**
     * Restores players positions (used for undo/redo).
     *
     * @param positions positions to restore (same order as players)
     */
    public void restorePlayerPositions(List<Position> positions) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setCurrentPosition(positions.get(i));
        }
    }

    /**
     * Copies the current objective index of each player (snapshot for undo/redo).
     *
     * @return list of objective indices
     */
    public List<Integer> copyObjectiveIndices() {
        List<Integer> indices = new ArrayList<>();
        for (Player p : players) {
            indices.add(p.getCurrentObjectiveIndex());
        }
        return indices;
    }

    /**
     * Copies the spare tile (snapshot for undo/redo).
     *
     * @return a copy of the spare tile
     */
    public Tile copySpareTile() {
        return spareTile.copy();
    }

    /**
     * Restores the objective index of each player (used for undo/redo).
     *
     * @param indices objective indices to restore (same order as players)
     */
    public void restoreObjectiveIndices(List<Integer> indices) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setCurrentObjectiveIndex(indices.get(i));
        }
    }

    /**
     * Registers an observer to be notified when the game state changes.
     *
     * @param o observer to add
     */
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    /**
     * Notifies all registered observers.
     */
    private void notifyObservers() {
        for (Observer o : observers) o.update();
    }

    /**
     * Remove an observer.
     *
     * @param observer observer to remove
     */
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
}
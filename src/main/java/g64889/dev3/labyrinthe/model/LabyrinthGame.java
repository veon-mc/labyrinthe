package g64889.dev3.labyrinthe.model;

import java.util.*;

public class LabyrinthGame {
    private Board board;
    private final List<Player> players;
    private int currentPlayerIndex;
    private Tile spareTile;
    private boolean gameOver;
    private Player winner;
    private ObjectiveDeck deck;
    private final List<Objective> objectives;
    private final PathFinder pathFinder;
    private Direction lastInsertionSide;
    private int lastInsertionIndex;
    private boolean tileInserted;
    private final CommandManager commandManager;
    private final List<Observer> observers = new ArrayList<>();


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

    public void startGame(int nbPlayers) {
        if (nbPlayers < 2 || nbPlayers > 4) {
            throw new IllegalArgumentException("Number of players must be between 2 and 4");
        }

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

    private void initBoard() {
        int size = 7;
        Tile[][] grid = new Tile[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (isFixedPosition(row, col)) {
                    TileType type = determineTileType(row, col); // coins L, sinon T
                    EnumSet<Direction> openings = getOpeningsForType(type, row, col);

                    Objective obj = isCorner(row, col) ? null : fixedObjectiveAt(row, col);
                    grid[row][col] = new Tile(type, openings, true, obj);
                }
            }
        }

        java.util.Set<String> usedFixed = new java.util.HashSet<>();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (isFixedPosition(r, c) && !isCorner(r, c)) {
                    usedFixed.add(fixedObjectiveAt(r, c).getName());
                }
            }
        }

        java.util.List<Objective> remaining = new java.util.ArrayList<>();
        for (Objective o : objectives) {
            if (!usedFixed.contains(o.getName())) remaining.add(o);
        }

        if (remaining.size() != 12) {
            throw new IllegalStateException("Il doit rester 12 objectifs mobiles, trouvé: " + remaining.size());
        }

        java.util.Collections.shuffle(remaining);
        java.util.List<Tile> mobileTiles = createMobileTiles(remaining, 0);
        java.util.Collections.shuffle(mobileTiles);

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

    private boolean isFixedPosition(int row, int col) {
        return (row % 2 == 0 && col % 2 == 0);
    }

    private boolean isCorner(int row, int col) {
        return (row == 0 || row == 6) && (col == 0 || col == 6);
    }

    private TileType determineTileType(int row, int col) {
        if (isCorner(row, col)) {
            return TileType.L;
        }
        return TileType.T;
    }

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

            // T fixes sur les bords
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
            }

            // T fixes au centre
            else if (row == 2 && col == 2) {
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

    private List<Tile> createMobileTiles(List<Objective> objectives, int startIndex) {
        List<Tile> tiles = new ArrayList<>();
        int objIndex = startIndex;

        // 6 tuiles T avec objectif
        for (int i = 0; i < 6; i++) {
            tiles.add(new Tile(
                    TileType.T,
                    randomOpeningsT(),
                    false,
                    objectives.get(objIndex++)
            ));
        }

        // 12 tuiles I sans objectif
        for (int i = 0; i < 12; i++) {
            tiles.add(new Tile(
                    TileType.I,
                    randomOpeningsI(),
                    false,
                    null
            ));
        }

        // 16 tuiles L (6 avec objectif, 10 sans)
        for (int i = 0; i < 6; i++) {
            tiles.add(new Tile(
                    TileType.L,
                    randomOpeningsL(),
                    false,
                    objectives.get(objIndex++)
            ));
        }
        for (int i = 0; i < 10; i++) {
            tiles.add(new Tile(
                    TileType.L,
                    randomOpeningsL(),
                    false,
                    null
            ));
        }

        return tiles;
    }

    private EnumSet<Direction> randomOpeningsT() {
        Random rand = new Random();
        int choice = rand.nextInt(4);

        // Type T = 3 ouvertures sur 4
        return switch (choice) {
            case 0 -> EnumSet.of(Direction.NORTH, Direction.EAST, Direction.SOUTH);  // pas WEST
            case 1 -> EnumSet.of(Direction.EAST, Direction.SOUTH, Direction.WEST);   // pas NORTH
            case 2 -> EnumSet.of(Direction.SOUTH, Direction.WEST, Direction.NORTH);  // pas EAST
            default -> EnumSet.of(Direction.WEST, Direction.NORTH, Direction.EAST);  // pas SOUTH
        };
    }

    private EnumSet<Direction> randomOpeningsI() {
        Random rand = new Random();

        // Type I = 2 ouvertures opposées
        if (rand.nextBoolean()) {
            return EnumSet.of(Direction.NORTH, Direction.SOUTH);  // vertical
        } else {
            return EnumSet.of(Direction.EAST, Direction.WEST);    // horizontal
        }
    }

    private EnumSet<Direction> randomOpeningsL() {
        Random rand = new Random();
        int choice = rand.nextInt(4);

        // Type L = 2 ouvertures adjacentes (angle droit)
        switch (choice) {
            case 0:
                return EnumSet.of(Direction.NORTH, Direction.EAST);
            case 1:
                return EnumSet.of(Direction.EAST, Direction.SOUTH);
            case 2:
                return EnumSet.of(Direction.SOUTH, Direction.WEST);
            default:
                return EnumSet.of(Direction.WEST, Direction.NORTH);
        }
    }

    private void initPlayers(int nbPlayers) {
        players.clear();
        Position[] startPositions = {
                new Position(0, 0), new Position(0, 6),
                new Position(6, 6), new Position(6, 0)
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

    private void distributeCards(int nbPlayers) {
        List<ObjectiveCard> allCards = new ArrayList<>();
        for (Objective obj : objectives) {
            allCards.add(new ObjectiveCard(obj));
        }
        deck = new ObjectiveDeck(allCards);
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

    public void doInsertTile(Direction side, int index) {
        commandManager.executeCommand(new InsertTileCommand(this, side, index));
    }

    public boolean canInsertTile(Direction side, int index) {
        if (index % 2 == 0 || index < 1 || index >= 7) {
            return false;
        }
        if (lastInsertionSide != null) {
            Direction oppositeSide = getOppositeSide(side);
            if (lastInsertionSide == oppositeSide && lastInsertionIndex == index) {
                return false;
            }
        }
        return true;
    }

    private Direction getOppositeSide(Direction side) {
        return switch (side) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
            default -> null;
        };
    }

    private void updatePlayersForInsertion(Direction side, int index) {
        int size = board.getSize();

        for (Player player : players) {
            Position pos = player.getCurrentPosition();
            int row = pos.getRow();
            int col = pos.getCol();

            switch (side) {
                case WEST -> {
                    if (row == index) {
                        if (col == size - 1) {
                            col = 0;
                        } else {
                            col = col + 1;
                        }
                        player.setCurrentPosition(new Position(row, col));
                    }
                }
                case EAST -> {
                    if (row == index) {
                        if (col == 0) {
                            col = size - 1;
                        } else {
                            col = col - 1;
                        }
                        player.setCurrentPosition(new Position(row, col));
                    }
                }
                case NORTH -> {
                    if (col == index) {
                        if (row == size - 1) {
                            row = 0;
                        } else {
                            row = row + 1;
                        }
                        player.setCurrentPosition(new Position(row, col));
                    }
                }
                case SOUTH -> {
                    if (col == index) {
                        if (row == 0) {
                            row = size - 1;
                        } else {
                            row = row - 1;
                        }
                        player.setCurrentPosition(new Position(row, col));
                    }
                }
            }
        }
    }

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
    }

    public void doMovePlayer(int row, int col) {
        commandManager.executeCommand(new MovePlayerCommand(this, row, col));
    }

    private void checkObjectiveReached(Player player) {
        Tile currentTile = board.getTileAt(player.getCurrentPosition());

        if (!player.hasCompletedAllObjectives()) {
            ObjectiveCard currentCard = player.getCurrentObjective();

            if (currentTile.getObjective() != null && currentCard != null) {
                if (currentTile.getObjective().equals(currentCard.getObjective())) {
                    player.validateCurrentObjective();
                }
            }
        }
        notifyObservers();
    }

    private void checkGameOver() {
        for (Player player : players) {
            if (player.hasCompletedAllObjectives() &&
                    player.getCurrentPosition().equals(player.getStartPosition())) {
                gameOver = true;
                winner = player;
                break;
            }
        }
    }

    public boolean canMoveTo(Position from, Position to) {
        Set<Position> reachable = pathFinder.getReachablePositions(board, from);
        return reachable.contains(to);
    }

    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        tileInserted = false;
    }

    public void rotateSpareTile() {
        spareTile.rotate();
        notifyObservers();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Board getBoard() {
        return board;
    }

    public Tile getSpareTile() {
        return spareTile;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public boolean isTileInserted() {
        return tileInserted;
    }

    public void undo() {
        commandManager.undo();
        notifyObservers();
    }

    public void redo() {
        commandManager.redo();
        notifyObservers();
    }

    public boolean canUndo() {
        return commandManager.canUndo();
    }

    public boolean canRedo() {
        return commandManager.canRedo();
    }

    public Direction getLastInsertionSide() {
        return lastInsertionSide;
    }

    public int getLastInsertionIndex() {
        return lastInsertionIndex;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setSpareTile(Tile tile) {
        this.spareTile = tile;
        board.setSpareTile(tile);
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public void setLastInsertion(Direction side, int index) {
        this.lastInsertionSide = side;
        this.lastInsertionIndex = index;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int index) {
        this.currentPlayerIndex = index;
    }

    public void setTileInserted(boolean inserted) {
        this.tileInserted = inserted;
    }

    public PathFinder getPathFinder() {
        return pathFinder;
    }

    public List<Position> copyPlayerPositions() {
        List<Position> positions = new ArrayList<>();
        for (Player p : players) {
            positions.add(p.getCurrentPosition());
        }
        return positions;
    }

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

    public void restoreGrid(Tile[][] grid) {
        board.setGrid(grid);
    }

    public void restorePlayerPositions(List<Position> positions) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setCurrentPosition(positions.get(i));
        }
    }

    public List<Integer> copyObjectiveIndices() {
        List<Integer> indices = new ArrayList<>();
        for (Player p : players) {
            indices.add(p.getCurrentObjectiveIndex());
        }
        return indices;
    }

    public Tile copySpareTile() {
        return spareTile.copy(); // idéal
    }

    public void restoreObjectiveIndices(List<Integer> indices) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setCurrentObjectiveIndex(indices.get(i));
        }
    }

    public void addObserver(Observer o) { observers.add(o); }
    public void removeObserver(Observer o) { observers.remove(o); }

    private void notifyObservers() {
        for (Observer o : observers) o.update();
    }
}


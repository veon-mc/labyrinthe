package g64889.dev3.labyrinthe.console;

import g64889.dev3.labyrinthe.model.*;
import g64889.dev3.labyrinthe.model.enumeration.Direction;

import java.util.List;
import java.util.Scanner;

/**
 * ConsoleView is responsible for all console-based input and output
 * interactions with the user.
 * <p>
 * This class displays game information (board, players, objectives, messages)
 * and reads user commands during the different phases of the game.
 * <p>
 * It does not contain any game logic and only communicates with the model
 * through data passed as parameters.
 */
public class ConsoleView {

    /** Scanner used to read user input from the console */
    private final Scanner scanner = new Scanner(System.in);

    // =========================
    // Display messages
    // =========================

    /**
     * Displays the welcome message when the application starts.
     */
    public void showWelcome() {
        System.out.println("=================================");
        System.out.println("   Bienvenue dans LABYRINTHE");
        System.out.println("=================================\n");
    }

    /**
     * Displays a message indicating that the game has started.
     */
    public void showGameStart() {
        System.out.println("\nLa partie commence !\n");
    }

    /**
     * Displays the header of the current player's turn.
     *
     * @param currentPlayer the player whose turn is starting
     */
    public void displayTurnHeader(Player currentPlayer) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Tour de " + currentPlayer.getName()
                + " (ID: " + currentPlayer.getId() + ")");
        System.out.println("=".repeat(50));
    }

    /**
     * Displays the insertion phase message.
     */
    public void showInsertPhase() {
        System.out.println("\n--- Phase 1 : Insertion de la tuile supplémentaire ---");
    }

    /**
     * Displays the movement phase message and the player's current position.
     *
     * @param player the current player
     */
    public void showMovePhase(Player player) {
        System.out.println("\n--- Phase 2 : Déplacement ---");
        System.out.println("Position actuelle : ligne "
                + player.getCurrentPosition().getRow()
                + ", colonne "
                + player.getCurrentPosition().getCol());
    }

    /**
     * Displays a message after a successful tile insertion.
     */
    public void showBoardAfterInsertion() {
        System.out.println("\nPlateau après insertion :");
    }

    /**
     * Displays a message indicating that the spare tile has been rotated.
     */
    public void showSpareRotated() {
        System.out.println("Tuile pivotée !");
    }

    /**
     * Displays a confirmation message for a successful tile insertion.
     *
     * @param side  the side from which the tile was inserted
     * @param index the index of insertion
     */
    public void showTileInserted(Direction side, int index) {
        System.out.println("\n✓ Tuile insérée avec succès !");
        System.out.println("  → Insertion depuis " + side + " à l'index " + index);
    }

    /**
     * Displays an error message when a tile insertion is invalid.
     *
     * @param msg explanation of the error
     */
    public void showInsertError(String msg) {
        System.out.println("\n✗ Insertion invalide : " + msg);
        System.out.println("  Vérifiez que :");
        System.out.println("  - Le côté est NORTH, SOUTH, EAST ou WEST");
        System.out.println("  - L'index est 1, 3 ou 5");
        System.out.println("  - Ce n'est pas l'annulation du coup précédent");
    }

    /**
     * Displays an error message when a movement is invalid.
     *
     * @param msg explanation of the error
     */
    public void showMoveError(String msg) {
        System.out.println("✗ Déplacement invalide : " + msg);
        System.out.println("  Vous ne pouvez vous déplacer que sur des cases accessibles.");
    }

    /**
     * Displays a generic invalid command message.
     */
    public void showInvalidCommand() {
        System.out.println("Commande invalide.");
    }

    /** Displays an undo confirmation message. */
    public void showUndo() {
        System.out.println("↩ Undo effectué.");
    }

    /** Displays a redo confirmation message. */
    public void showRedo() {
        System.out.println("↪ Redo effectué.");
    }

    /** Displays a message when undo is not possible. */
    public void showCannotUndo() {
        System.out.println("✗ Impossible : rien à undo.");
    }

    /** Displays a message when redo is not possible. */
    public void showCannotRedo() {
        System.out.println("✗ Impossible : rien à redo.");
    }

    /** Displays a message when the player stays on the same cell. */
    public void showStayed() {
        System.out.println("✓ Vous restez sur place.");
    }

    /**
     * Displays a confirmation message after a successful move.
     *
     * @param r destination row
     * @param c destination column
     */
    public void showMoved(int r, int c) {
        System.out.println("✓ Déplacement effectué vers ligne " + r + ", colonne " + c + " !");
    }

    // =========================
    // User input
    // =========================

    /**
     * Asks the user for the number of players.
     *
     * @return a valid number of players (between 2 and 4)
     */
    public int askNumberOfPlayers() {
        int nbPlayers = 0;
        while (nbPlayers < 2 || nbPlayers > 4) {
            System.out.print("Nombre de joueurs (2-4) : ");
            try {
                nbPlayers = Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        }
        return nbPlayers;
    }

    /**
     * Asks the user for a command during the insertion phase.
     *
     * @return the command entered by the user
     */
    public String askInsertCommand() {
        System.out.println("\nCommandes :");
        System.out.println("  r - Faire pivoter la tuile supplémentaire");
        System.out.println("  i - Insérer la tuile");
        System.out.println("  u - Undo");
        System.out.println("  d - Redo");
        System.out.print("Votre choix : ");
        return scanner.nextLine().trim().toLowerCase();
    }

    /**
     * Asks the user for a tile insertion side.
     *
     * @return a valid {@link Direction}
     */
    public Direction askSide() {
        while (true) {
            System.out.print("Côté (NORTH/SOUTH/EAST/WEST) : ");
            try {
                return Direction.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("✗ Côté invalide.");
            }
        }
    }

    /**
     * Asks the user for a tile insertion index.
     *
     * @return a valid index (1, 3 or 5)
     */
    public int askIndex() {
        while (true) {
            System.out.print("Index (1, 3 ou 5) : ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("✗ Index invalide.");
            }
        }
    }

    /**
     * Asks the user for a command during the movement phase.
     *
     * @return the command entered by the user
     */
    public String askMoveCommand() {
        System.out.println("\nCommandes :");
        System.out.println("  m    - Se déplacer");
        System.out.println("  stay - Rester sur place");
        System.out.println("  u    - Undo");
        System.out.println("  d    - Redo");
        System.out.print("Votre choix : ");
        return scanner.nextLine().trim().toLowerCase();
    }

    /**
     * Asks the user for a row coordinate.
     *
     * @return a row index
     */
    public int askRow() {
        while (true) {
            System.out.print("Ligne (0-6) : ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("✗ Ligne invalide.");
            }
        }
    }

    /**
     * Asks the user for a column coordinate.
     *
     * @return a column index
     */
    public int askCol() {
        while (true) {
            System.out.print("Colonne (0-6) : ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("✗ Colonne invalide.");
            }
        }
    }

    // =========================
    // Game-related display
    // =========================

    /**
     * Displays the current objective of a player.
     *
     * @param player the player whose objective is displayed
     */
    public void displayCurrentObjective(Player player) {
        if (!player.hasCompletedAllObjectives()) {
            ObjectiveCard card = player.getCurrentObjective();
            System.out.println("\nObjectif actuel : "
                    + card.getObjective().getName());
        } else {
            System.out.println("\n✓ Tous les objectifs atteints !");
        }
    }

    /**
     * Displays the winner at the end of the game.
     *
     * @param winner the winning player, or null if the game was aborted
     */
    public void displayWinner(Player winner) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           FIN DE LA PARTIE");
        System.out.println("=".repeat(50));

        if (winner != null) {
            System.out.println("\n🎉 " + winner.getName() + " a gagné ! 🎉");
        } else {
            System.out.println("\nPartie abandonnée.");
        }
    }

    /**
     * Displays the spare tile in ASCII form.
     *
     * @param spare the spare tile
     */
    public void displaySpareTile(Tile spare) {
        System.out.println("\nTuile supplémentaire :");
        System.out.println("  " + (spare.hasOpening(Direction.NORTH) ? "│" : " "));
        System.out.print(spare.hasOpening(Direction.WEST) ? "──" : "  ");
        System.out.print(spare.getObjective() != null
                ? spare.getObjective().getName().charAt(0)
                : '·');
        System.out.println(spare.hasOpening(Direction.EAST) ? "──" : "  ");
        System.out.println("  " + (spare.hasOpening(Direction.SOUTH) ? "│" : " "));
    }

    /**
     * Displays the full board with players and objectives.
     *
     * @param board   the game board
     * @param players the list of players
     */
    public void displayBoard(Board board, List<Player> players) {
        // No game logic is implemented here
    }
}
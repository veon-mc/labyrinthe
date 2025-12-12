package g64889.dev3.labyrinthe.console;

import g64889.dev3.labyrinthe.model.*;

import java.util.Scanner;
import java.util.List;

public class ConsoleView {

    private final Scanner scanner = new Scanner(System.in);

    // =========================
    // Messages / affichage simple
    // =========================

    public void showWelcome() {
        System.out.println("=================================");
        System.out.println("   Bienvenue dans LABYRINTHE");
        System.out.println("=================================\n");
    }

    public void showGameStart() {
        System.out.println("\nLa partie commence !\n");
    }

    public void displayTurnHeader(Player currentPlayer) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Tour de " + currentPlayer.getName() + " (ID: " + currentPlayer.getId() + ")");
        System.out.println("=".repeat(50));
    }

    public void showInsertPhase() {
        System.out.println("\n--- Phase 1 : Insertion de la tuile supplémentaire ---");
    }

    public void showMovePhase(Player player) {
        System.out.println("\n--- Phase 2 : Déplacement ---");
        System.out.println("Position actuelle : ligne " + player.getCurrentPosition().getRow() +
                ", colonne " + player.getCurrentPosition().getCol());
    }

    public void showBoardAfterInsertion() {
        System.out.println("\nPlateau après insertion :");
    }

    public void showSpareRotated() {
        System.out.println("Tuile pivotée !");
    }

    public void showTileInserted(Direction side, int index) {
        System.out.println("\n✓ Tuile insérée avec succès !");
        System.out.println("  → Insertion depuis " + side + " à l'index " + index);
    }

    public void showInsertError(String msg) {
        System.out.println("\n✗ Insertion invalide : " + msg);
        System.out.println("  Vérifiez que :");
        System.out.println("  - Le côté est NORTH, SOUTH, EAST ou WEST");
        System.out.println("  - L'index est 1, 3 ou 5");
        System.out.println("  - Ce n'est pas l'annulation du coup précédent");
    }

    public void showMoveError(String msg) {
        System.out.println("✗ Déplacement invalide : " + msg);
        System.out.println("  Vous ne pouvez vous déplacer que sur des cases accessibles.");
    }

    public void showInvalidCommand() {
        System.out.println("Commande invalide.");
    }

    public void showUndo() {
        System.out.println("↩ Undo effectué.");
    }

    public void showRedo() {
        System.out.println("↪ Redo effectué.");
    }

    public void showCannotUndo() {
        System.out.println("✗ Impossible : rien à undo.");
    }

    public void showCannotRedo() {
        System.out.println("✗ Impossible : rien à redo.");
    }

    public void showStayed() {
        System.out.println("✓ Vous restez sur place.");
    }

    public void showMoved(int r, int c) {
        System.out.println("✓ Déplacement effectué vers ligne " + r + ", colonne " + c + " !");
    }

    // =========================
    // Entrées utilisateur
    // =========================

    public int askNumberOfPlayers() {
        int nbPlayers = 0;
        while (nbPlayers < 2 || nbPlayers > 4) {
            System.out.print("Nombre de joueurs (2-4) : ");
            try {
                nbPlayers = Integer.parseInt(scanner.nextLine().trim());
                if (nbPlayers < 2 || nbPlayers > 4) {
                    System.out.println("Le nombre de joueurs doit être entre 2 et 4.");
                }
            } catch (Exception e) {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        }
        return nbPlayers;
    }

    /**
     * Phase insertion : commandes possibles
     * r = rotate, i = insert, u = undo, d = redo
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

    public Direction askSide() {
        while (true) {
            System.out.print("Côté (NORTH/SOUTH/EAST/WEST) : ");
            String sideStr = scanner.nextLine().trim().toUpperCase();
            try {
                return Direction.valueOf(sideStr);
            } catch (IllegalArgumentException e) {
                System.out.println("✗ Côté invalide. Essayez : NORTH, SOUTH, EAST, WEST");
            }
        }
    }

    public int askIndex() {
        while (true) {
            System.out.print("Index (1, 3 ou 5) : ");
            String s = scanner.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("✗ Index invalide. Essayez : 1, 3 ou 5");
            }
        }
    }

    /**
     * Phase déplacement : commandes possibles
     * m = move, stay = rester, u = undo, d = redo
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
    // Affichage "métier"
    // =========================

    public void displayCurrentObjective(Player player) {
        if (!player.hasCompletedAllObjectives()) {
            ObjectiveCard card = player.getCurrentObjective();
            System.out.println("\nObjectif actuel : " + card.getObjective().getName());
            System.out.println("Objectifs restants : " +
                    (player.getObjectives().size() - player.getCurrentObjectiveIndex()));
        } else {
            System.out.println("\n✓ Tous les objectifs atteints ! Retournez à votre point de départ.");
        }
    }

    public void displayWinner(Player winner) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           FIN DE LA PARTIE");
        System.out.println("=".repeat(50));

        if (winner != null) {
            System.out.println("\n🎉 " + winner.getName() + " a gagné ! 🎉");
        } else {
            System.out.println("\nPartie abandonnée.");
        }

        System.out.println("\nMerci d'avoir joué !");
    }

    public void displaySpareTile(Tile spare) {
        System.out.println("\nTuile supplémentaire :");
        System.out.println("  " + (spare.hasOpening(Direction.NORTH) ? "│" : " "));
        System.out.print(spare.hasOpening(Direction.WEST) ? "──" : "  ");
        System.out.print(spare.getObjective() != null ? spare.getObjective().getName().charAt(0) : '·');
        System.out.println(spare.hasOpening(Direction.EAST) ? "──" : "  ");
        System.out.println("  " + (spare.hasOpening(Direction.SOUTH) ? "│" : " "));
    }

    public void displayBoard(Board board, List<Player> players) {
        int size = board.getSize();

        System.out.println();

        System.out.print("    ");
        for (int col = 0; col < size; col++) {
            if (col == 1 || col == 3 || col == 5) {
                System.out.print(" ↓  ");
            } else {
                System.out.print("    ");
            }
        }
        System.out.println();

        System.out.print("    ");
        for (int col = 0; col < size; col++) {
            System.out.print(" " + col + "  ");
        }
        System.out.println();

        System.out.print("   ╔");
        for (int col = 0; col < size; col++) {
            System.out.print("═══");
            if (col < size - 1) System.out.print("╦");
        }
        System.out.println("╗");

        for (int row = 0; row < size; row++) {
            if (row == 1 || row == 3 || row == 5) System.out.print(" →");
            else System.out.print("  ");

            System.out.print(row + "║");

            for (int col = 0; col < size; col++) {
                Tile tile = board.getTileAt(new Position(row, col));
                System.out.print(" " + (tile.hasOpening(Direction.NORTH) ? "│" : " ") + " ");
                if (col < size - 1) System.out.print("║");
            }

            System.out.print("║");
            if (row == 1 || row == 3 || row == 5) System.out.print("←");
            System.out.println();

            System.out.print("   ║");
            for (int col = 0; col < size; col++) {
                Tile tile = board.getTileAt(new Position(row, col));
                char left = tile.hasOpening(Direction.WEST) ? '─' : ' ';
                char right = tile.hasOpening(Direction.EAST) ? '─' : ' ';
                char center = getTileCenter(tile, row, col, players);
                System.out.print("" + left + center + right);
                if (col < size - 1) System.out.print("║");
            }
            System.out.println("║");

            System.out.print("   ║");
            for (int col = 0; col < size; col++) {
                Tile tile = board.getTileAt(new Position(row, col));
                System.out.print(" " + (tile.hasOpening(Direction.SOUTH) ? "│" : " ") + " ");
                if (col < size - 1) System.out.print("║");
            }
            System.out.println("║");

            if (row < size - 1) {
                System.out.print("   ╠");
                for (int col = 0; col < size; col++) {
                    System.out.print("═══");
                    if (col < size - 1) System.out.print("╬");
                }
                System.out.println("╣");
            }
        }

        System.out.print("   ╚");
        for (int col = 0; col < size; col++) {
            System.out.print("═══");
            if (col < size - 1) System.out.print("╩");
        }
        System.out.println("╝");

        System.out.print("    ");
        for (int col = 0; col < size; col++) {
            if (col == 1 || col == 3 || col == 5) System.out.print(" ↑  ");
            else System.out.print("    ");
        }
        System.out.println();
    }

    private char getTileCenter(Tile tile, int row, int col, List<Player> players) {
        for (Player p : players) {
            if (p.getCurrentPosition().getRow() == row && p.getCurrentPosition().getCol() == col) {
                return (char) ('0' + p.getId());
            }
        }

        if (tile.getObjective() != null) {
            return tile.getObjective().getName().charAt(0);
        }

        return '·';
    }
}
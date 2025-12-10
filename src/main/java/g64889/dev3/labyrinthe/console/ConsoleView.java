package g64889.dev3.labyrinthe.console;

import g64889.dev3.labyrinthe.model.*;
import java.util.Scanner;


public class ConsoleView {

    private final LabyrinthGame game;
    private final Scanner scanner;

    public ConsoleView() {
        this.game = new LabyrinthGame();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();
        view.start();
    }

    public void start() {
        System.out.println("=================================");
        System.out.println("   Bienvenue dans LABYRINTHE");
        System.out.println("=================================\n");

        int nbPlayers = askNumberOfPlayers();
        game.startGame(nbPlayers);

        System.out.println("\nLa partie commence !\n");

        playGame();
    }

    private int askNumberOfPlayers() {
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

    private void playGame() {
        while (!game.isGameOver()) {
            playTurn();
        }

        displayWinner();
    }


    private void playTurn() {
        Player currentPlayer = game.getCurrentPlayer();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Tour de " + currentPlayer.getName() + " (ID: " + currentPlayer.getId() + ")");
        System.out.println("=".repeat(50));

        displayBoard();
        displayCurrentObjective(currentPlayer);

        insertTilePhase();

        System.out.println("\nPlateau après insertion :");
        displayBoard();

        movePlayerPhase(currentPlayer);
    }

    private void insertTilePhase() {
        System.out.println("\n--- Phase 1 : Insertion de la tuile supplémentaire ---");
        displaySpareTile();

        boolean tileInserted = false;
        while (!tileInserted) {
            System.out.println("\nCommandes :");
            System.out.println("  r - Faire pivoter la tuile supplémentaire");
            System.out.println("  i - Insérer la tuile");
            System.out.print("Votre choix : ");

            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("r")) {
                game.rotateSpareTile();
                System.out.println("Tuile pivotée !");
                displaySpareTile();
            } else if (choice.equals("i")) {
                tileInserted = askAndInsertTile();
            } else {
                System.out.println("Commande invalide.");
            }
        }
    }

    private boolean askAndInsertTile() {
        System.out.println("\n=== Insertion de la tuile ===");
        System.out.println("\nChoisissez le côté et l'index (ligne ou colonne) :");
        System.out.println();
        System.out.println("  NORTH (haut)   : pousse la colonne 1, 3 ou 5 vers le BAS");
        System.out.println("  SOUTH (bas)    : pousse la colonne 1, 3 ou 5 vers le HAUT");
        System.out.println("  EAST (droite)  : pousse la ligne 1, 3 ou 5 vers la GAUCHE");
        System.out.println("  WEST (gauche)  : pousse la ligne 1, 3 ou 5 vers la DROITE");
        System.out.println();
        System.out.println("Indices valides : 1, 3, 5");
        System.out.println();

        System.out.print("Côté (NORTH/SOUTH/EAST/WEST) : ");
        String sideStr = scanner.nextLine().trim().toUpperCase();

        System.out.print("Index (1, 3 ou 5) : ");
        String indexStr = scanner.nextLine().trim();

        try {
            Direction side = Direction.valueOf(sideStr);
            int index = Integer.parseInt(indexStr);

            game.insertTile(side, index);
            System.out.println("\n✓ Tuile insérée avec succès !");
            System.out.println("  → Insertion depuis " + side + " à l'index " + index);
            return true;

        } catch (IllegalArgumentException e) {
            System.out.println("\n✗ Insertion invalide : " + e.getMessage());
            System.out.println("  Vérifiez que :");
            System.out.println("  - Le côté est NORTH, SOUTH, EAST ou WEST");
            System.out.println("  - L'index est 1, 3 ou 5");
            System.out.println("  - Ce n'est pas l'annulation du coup précédent");
            return false;
        } catch (IllegalStateException e) {
            System.out.println("\n✗ Erreur : " + e.getMessage());
            return false;
        }
    }

    private void movePlayerPhase(Player player) {
        System.out.println("\n--- Phase 2 : Déplacement ---");
        System.out.println("Position actuelle : ligne " + player.getCurrentPosition().getRow() +
                ", colonne " + player.getCurrentPosition().getCol());

        boolean moved = false;
        while (!moved) {
            System.out.println("\nOù voulez-vous vous déplacer ?");
            System.out.println("  Format : ligne colonne  (ex: 2 4)");
            System.out.println("  ou tapez 'stay' pour rester sur place");
            System.out.print("Votre choix : ");

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("✗ Veuillez entrer une commande.");
                continue;
            }

            if (input.equalsIgnoreCase("stay")) {
                int row = player.getCurrentPosition().getRow();
                int col = player.getCurrentPosition().getCol();
                game.movePlayer(row, col);
                System.out.println("✓ Vous restez sur place.");
                moved = true;
            } else {
                try {
                    String[] parts = input.split("\\s+");
                    if (parts.length != 2) {
                        System.out.println("✗ Format invalide. Exemple : 2 4");
                        continue;
                    }

                    int targetRow = Integer.parseInt(parts[0]);
                    int targetCol = Integer.parseInt(parts[1]);

                    game.movePlayer(targetRow, targetCol);
                    System.out.println("✓ Déplacement effectué vers ligne " + targetRow + ", colonne " + targetCol + " !");
                    moved = true;

                } catch (NumberFormatException e) {
                    System.out.println("✗ Veuillez entrer des nombres valides. Exemple : 2 4");
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("✗ Déplacement invalide : " + e.getMessage());
                    System.out.println("  Vous ne pouvez vous déplacer que sur des cases accessibles.");
                }
            }
        }
    }

    private void displayBoard() {
        Board board = game.getBoard();
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
            if (col < size - 1) {
                System.out.print("╦");
            }
        }
        System.out.println("╗");

        for (int row = 0; row < size; row++) {
            if (row == 1 || row == 3 || row == 5) {
                System.out.print(" →");
            } else {
                System.out.print("  ");
            }
            System.out.print(row + "║");

            for (int col = 0; col < size; col++) {
                Tile tile = board.getTileAt(new Position(row, col));
                System.out.print(" " + (tile.hasOpening(Direction.NORTH) ? "│" : " ") + " ");
                if (col < size - 1) {
                    System.out.print("║");
                }
            }
            System.out.print("║");
            if (row == 1 || row == 3 || row == 5) {
                System.out.print("←");
            }
            System.out.println();

            System.out.print("   ║");
            for (int col = 0; col < size; col++) {
                Tile tile = board.getTileAt(new Position(row, col));
                char left = tile.hasOpening(Direction.WEST) ? '─' : ' ';
                char right = tile.hasOpening(Direction.EAST) ? '─' : ' ';
                char center = getTileCenter(tile, row, col);

                System.out.print("" + left + center + right);
                if (col < size - 1) {
                    System.out.print("║");
                }
            }
            System.out.println("║");

            System.out.print("   ║");
            for (int col = 0; col < size; col++) {
                Tile tile = board.getTileAt(new Position(row, col));
                System.out.print(" " + (tile.hasOpening(Direction.SOUTH) ? "│" : " ") + " ");
                if (col < size - 1) {
                    System.out.print("║");
                }
            }
            System.out.println("║");

            if (row < size - 1) {
                System.out.print("   ╠");
                for (int col = 0; col < size; col++) {
                    System.out.print("═══");
                    if (col < size - 1) {
                        System.out.print("╬");
                    }
                }
                System.out.println("╣");
            }
        }

        System.out.print("   ╚");
        for (int col = 0; col < size; col++) {
            System.out.print("═══");
            if (col < size - 1) {
                System.out.print("╩");
            }
        }
        System.out.println("╝");

        System.out.print("    ");
        for (int col = 0; col < size; col++) {
            if (col == 1 || col == 3 || col == 5) {
                System.out.print(" ↑  ");
            } else {
                System.out.print("    ");
            }
        }
        System.out.println();
    }

    private char getTileCenter(Tile tile, int row, int col) {
        for (Player p : game.getPlayers()) {
            if (p.getCurrentPosition().getRow() == row &&
                    p.getCurrentPosition().getCol() == col) {
                return (char) ('0' + p.getId());
            }
        }

        if (tile.getObjective() != null) {
            return tile.getObjective().getName().charAt(0);
        }

        return '·';
    }

    private void displaySpareTile() {
        Tile spare = game.getSpareTile();
        System.out.println("\nTuile supplémentaire :");
        System.out.println("  " + (spare.hasOpening(Direction.NORTH) ? "│" : " "));
        System.out.print(spare.hasOpening(Direction.WEST) ? "──" : "  ");
        System.out.print(spare.getObjective() != null ? spare.getObjective().getName().charAt(0) : '·');
        System.out.println(spare.hasOpening(Direction.EAST) ? "──" : "  ");
        System.out.println("  " + (spare.hasOpening(Direction.SOUTH) ? "│" : " "));
    }


    private void displayCurrentObjective(Player player) {
        if (!player.hasCompletedAllObjectives()) {
            ObjectiveCard card = player.getCurrentObjective();
            System.out.println("\nObjectif actuel : " + card.getObjective().getName());
            System.out.println("Objectifs restants : " +
                    (player.getObjectives().size() - player.getCurrentObjectiveIndex()));  // ← Cette ligne a été ajoutée
        } else {
            System.out.println("\n✓ Tous les objectifs atteints ! Retournez à votre point de départ.");
        }
    }

    private void displayWinner() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           FIN DE LA PARTIE");
        System.out.println("=".repeat(50));

        Player winner = game.getWinner();
        if (winner != null) {
            System.out.println("\n🎉 " + winner.getName() + " a gagné ! 🎉");
        } else {
            System.out.println("\nPartie abandonnée.");
        }

        System.out.println("\nMerci d'avoir joué !");
    }

}
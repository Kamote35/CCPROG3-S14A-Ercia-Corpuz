import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {
    final public ArrayList<Player> players;
    final public ArrayList<Block> board;
    Random rand;
    Scanner scanner; // Added scanner

    // constructors
    public Game() {
        players = new ArrayList<>();
        board = new ArrayList<>();
        rand = new Random();
        scanner = new Scanner(System.in); // Initialized scanner
        initializeBoard();
    }

    private void initializeBoard() {
        // Block 1 - Block 5
        board.add(new SpecialBlock("GO", 1, "GO"));
        board.add(new PropertyBlock("Walled City Manila (Intramuros & Port Area)","Muralla Street", 2, 25000.00, 5000.00));
        board.add(new SpecialBlock("Internal Revenue Allotment", 3, "INTERNAL_REVENUE"));
        board.add(new PropertyBlock("Walled City Manila (Intramuros & Port Area)","Muralla Street", 4, 25000.00, 5000.00));
        board.add(new SpecialBlock("Income Tax", 5, "INCOME_TAX"));

        // Block 6 - Block 10
        board.add(new SpecialBlock("LRT1", 6, "LRT1"));
        board.add(new PropertyBlock("Downtown Manila (Binondo, Quiapo, San Nicolas, & Sta. Cruz)", "Ongping Street", 7, 15000.00, 2500.00));
        board.add(new SpecialBlock("Chance", 8, "Chance"));
        board.add(new PropertyBlock("Downtown Manila (Binondo, Quiapo, San Nicolas, & Sta. Cruz)", "Ongping Street", 9, 15000.00, 2500.00));
        board.add(new PropertyBlock("Downtown Manila (Binondo, Quiapo, San Nicolas, & Sta. Cruz)", "Ongping Street", 10, 15000.00, 2500.00));

        // Block 11 - 15
        board.add(new SpecialBlock("Manila City Jail", 11, "Manila City Jail"));
        board.add(new PropertyBlock("Old Manila (Tondo & Gagalangin)", "Abad Santos Aveneue", 12, 15000.00, 2500.00));
        board.add(new SpecialBlock("Meralco", 13, "Meralco"));
        board.add(new PropertyBlock("Old Manila (Tondo & Gagalangin)", "Abad Santos Aveneue", 14, 15000.00, 2500.00));
        board.add(new PropertyBlock("Old Manila (Tondo & Gagalangin)", "Abad Santos Aveneue", 15, 15000.00, 2500.00));

        // Block 16 - 20
        board.add(new SpecialBlock("PNR", 16, "PNR"));
        board.add(new PropertyBlock("University Belt (Sampaloc)", "Espana Boulevard", 17, 25000.00, 5000.00));
        board.add(new SpecialBlock("Internal Revenue Allotmet", 18, "Internal Revenue Allotment"));
        board.add(new PropertyBlock("University Belt (Sampaloc)", "Espana Boulevard", 19, 25000.00, 5000.00));
        board.add(new PropertyBlock("University Belt (Sampaloc)", "Espana Boulevard", 20, 25000.00, 5000.00));

        // Block 21 - 25
        board.add(new SpecialBlock("Free Parking", 21, "Free Parking"));
        board.add(new PropertyBlock("Residential Manila (Pandacan, Sta. Ana, & Sta. Mesa)", "Pedro Gil Street", 22, 12500.00, 1500.00));
        board.add(new SpecialBlock("Chance", 23, "Chance"));
        board.add(new PropertyBlock("Residential Manila (Pandacan, Sta. Ana, & Sta. Mesa)", "Pedro Gil Street", 24, 12500.00, 1500.00));
        board.add(new PropertyBlock("Residential Manila (Pandacan, Sta. Ana, & Sta. Mesa)", "Pedro Gil Street", 25, 12500.00, 1500.00));

        // Block 26 - 30
        board.add(new SpecialBlock("LRT2", 26, "LRT2"));
        board.add(new PropertyBlock("River South Manila (Paco, & San Andres)", "Paz Mendoza Guazon Street (Otis)", 27, 15000.00, 2500.00));
        board.add(new PropertyBlock("River South Manila (Paco, & San Andres)", "Paz Mendoza Guazon Street (Otis)", 28, 15000.00, 2500.00));
        board.add(new SpecialBlock("Manyilad", 29, "Maynilad"));
        board.add(new PropertyBlock("River South Manila (Paco, & San Andres)", "Paz Mendoza Guazon Street (Otis)", 30, 15000.00, 2500.00));

        // Block 31 - 35
        board.add(new SpecialBlock("Manila Police District", 31, "Manila Police District"));
        board.add(new PropertyBlock("Vibrant Manila (Malate)", "Taft Avenue", 32, 25000.00, 5000.00));
        board.add(new PropertyBlock("Vibrant Manila (Malate)", "Taft Avenue", 33, 25000.00, 5000.00));
        board.add(new SpecialBlock("Internal Revenue Allotment", 34, "Internal Revenue Allotment"));
        board.add(new PropertyBlock("Vibrant Manila (Malate)", "Taft Avenue", 35, 25000.00, 5000.00));

        // Block 36 - 40
        board.add(new SpecialBlock("MRT3", 36, "MRT3"));
        board.add(new SpecialBlock("Chance", 37, "Chance"));
        board.add(new PropertyBlock("Cultural Manila (Ermita)", "United Nations Avenue", 38, 17500.00, 2125.00));
        board.add(new SpecialBlock("Real Property Tax", 39, "Real Property Tax"));
        board.add(new PropertyBlock("Cultural Manila (Ermita)", "United Nations Avenue", 40, 17500.00, 2125.00));
    }    

    public void start() {
        System.out.println("Welcome to the game!");
        System.out.print("Enter number of players (2-4): ");
        int num = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < num; i++) {
            System.out.print("Enter name of Player " + (i + 1) + ": ");
            String name = scanner.nextLine();
            players.add(new Player(name));
        }

        boolean gameOver = false;

        while (!gameOver) {
            for (Player player : players) {
                System.out.println("\nIt's " + player.getName() + "'s turn. Cash: Php " + player.getCash());
                System.out.println("Press ENTER to roll the dice...");
                scanner.nextLine(); // Wait for player to press Enter

                int d1 = rollDice();
                int d2 = rollDice();
                int move = d1 + d2;

                System.out.println("Rolled: a " + d1 + " and a " + d2 + ".\nMove: " + move + " blocks.");

                player.updatePositionBlock(player.getPositionBlock() + move);
                if (player.getPositionBlock() > board.size()) {
                    player.updatePositionBlock(player.getPositionBlock() % board.size());
                    System.out.println(player.getName() + " passed GO! + Php 2,500");
                    player.updateCash(2500);
                }

                Block block = board.get(player.getPositionBlock() - 1);
                block.landedOn(player, this);

                System.out.println(player.getName() + "'s cash: Php " + player.getCash());
                System.out.println(player.getName() + "'s net worth: Php " + player.getNetWorth());
                System.out.println(player.getName() + "'s position: " + player.getPositionBlock() + " (" + board.get(player.getPositionBlock() - 1).getName() + ")");

                if (player.getCash() < 0) {
                    System.out.println(player.getName() + " is bankrupt! Game over.");
                    gameOver = true;
                    break;
                }
            }
        }
    }

    public int rollDice() {
        return rand.nextInt(6) + 1;
    }
}
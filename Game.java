import java.util.ArrayList;
import java.util.Random; 

public class Game {
    final public ArrayList<Player> players;
    final public ArrayList<Block> board;
    Random rand;

    // constructors
    public Game() {
        players = new ArrayList<>();
        board = new ArrayList<>();
        rand = new Random();
        initializeBoard();
    }

    public void initializeBoard() {
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
    }

    public void start() {
        System.out.println("Welcome to the game!");
        System.out.println("Enter number of players (2-4):");
        int num = Integer.parseInt(System.console().readLine());
        for (int i = 0; i < num; i++) {
            System.out.print("Enter name of Player " + (i + 1) + ": ");
            String name = System.console().readLine();
            players.add(new Player(name));
        }

        boolean gameOver = false;

        while (!gameOver) {
            for (Player player : players) {
                System.out.println("\n" + player.name + "'s turn. Cash: ₱" + player.getCash());

                int d1 = rollDice();
                int d2 = rollDice();
                int move = d1 + d2;

                System.out.println("Rolled: " + d1 + " and " + d2 + " -> Move: " + move);


                player.updatePositionBlock(player.getPositionBlock() + move);
                if (player.getPositionBlock() > board.size()) {
                    player.updatePositionBlock(player.getPositionBlock() % board.size());
                    System.out.println(player.name + " passed GO! +₱2,500");
                    player.updateCash(2500);
                }

                Block block = board.get(player.getPositionBlock() - 1);
                block.landedOn(player, this);

                System.out.println(player.name + "'s cash: ₱" + player.getCash());
                System.out.println(player.name + "'s net worth: ₱" + player.getNetWorth());

                if (player.getCash() < 0) {
                    System.out.println(player.name + " is bankrupt! Game over.");
                    gameOver = true;
                    break;
                }
            }
        }
    }

    public int rollDice() {
        return rand.nextInt(6) + 1; // returns a number between 1 and 6
    }
}
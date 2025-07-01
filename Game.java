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
        board.add(new SpecialBlock("GO", 1, "GO"));
        board.add(new PropertyBlock("Muralla Street", 2, 25000, 5000));
        board.add(new SpecialBlock("Internal Revenue Allotment", 3, "INTERNAL_REVENUE"));
        board.add(new SpecialBlock("Income Tax", 4, "INCOME_TAX"));
        board.add(new SpecialBlock("Meralco", 5, "MERALCO"));
        board.add(new SpecialBlock("Maynilad", 6, "MAYNILAD"));
        board.add(new SpecialBlock("Real Property Tax", 7, "REAL_PROPERTY_TAX"));
        board.add(new SpecialBlock("Manila City Jail", 8, "JAIL"));
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
                System.out.println("\n" + player.name + "'s turn. Cash: ₱" + player.cash);

                int d1 = rollDice();
                int d2 = rollDice();
                int move = d1 + d2;

                System.out.println("Rolled: " + d1 + " and " + d2 + " -> Move: " + move);

                player.position += move;
                if (player.position > board.size()) {
                    player.position = player.position % board.size();
                    System.out.println(player.name + " passed GO! +₱2,500");
                    player.cash += 2500;
                }

                Block block = board.get(player.position - 1);
                block.landedOn(player, this);

                System.out.println(player.name + "'s cash: ₱" + player.cash);
                System.out.println(player.name + "'s net worth: ₱" + player.getNetWorth());

                if (player.cash < 0) {
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
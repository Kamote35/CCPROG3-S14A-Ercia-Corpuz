import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


// Game class where the main gameplay of the Monopoly Manila will happen
public class Game {

    final public ArrayList<Player> players;
    final public ArrayList<Block> board;
    public Random rand;
    public Scanner scanner; // Added scanner

    // constructors
    public Game() {
        players = new ArrayList<>();
        board = new ArrayList<>();
        rand = new Random();
        scanner = new Scanner(System.in); // Initialized scanner
        initializeBoard();
    }
    
    /** 
     * Initializes the game board with various blocks
     */
    private void initializeBoard() {
        // Block 1 - Block 5
        board.add(new SpecialBlock("GO", 1, "GO"));
        board.add(new PropertyBlock("Walled City Manila (Intramuros & Port Area)","Muralla Street", 2, 25000.00, 5000.00));
        board.add(new SpecialBlock("Internal Revenue Allotment", 3, "Internal Revenue Allotment"));
        board.add(new PropertyBlock("Walled City Manila (Intramuros & Port Area)","Muralla Street", 4, 25000.00, 5000.00));
        board.add(new SpecialBlock("Income Tax", 5, "Income Tax"));

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

    /** 
     * Starts the main game
     */
    public void start() {

        int num;

        do {
            System.out.println("Welcome to the game!");
            System.out.print("Enter number of players (2-4): ");
            num = Integer.parseInt(scanner.nextLine());

            if (num < 2 || num > 4) {
                System.out.println("Invalid number of players.\n");
            }
        } while (num < 2 || num > 4);

        for (int i = 0; i < num; i++) {
            System.out.print("Enter name of Player " + (i + 1) + ": ");
            String name = scanner.nextLine();
            players.add(new Player(name));
        }

        boolean gameOver = false;

        while (!gameOver) {
            List<Player> playersToRemove = new ArrayList<>();

            for (Player player : players) {
                player.updateNetWorth();
                System.out.printf("%nIt's %s's turn! [Level %d] Cash: Php %.2f%n", player.getName(), player.getPlayerLvl(), player.getCash());
                int doubleCount = 0;
                int d1;
                int d2;

                do { 
                System.out.println("Press ENTER to roll the dice...");
                scanner.nextLine(); // Wait for player to press Enter

                d1 = rollDice();
                d2 = rollDice();
                int move = d1 + d2;

                    if (d1 != d2) {
                        System.out.println(player.getName() + " rolled a " + d1 + " and a " + d2 + " -> Move: " + move + " blocks.");
                    } else {
                        System.out.println(player.getName() + " rolled a double! (" + d1 + " and " + d2 + ") -> Move: " + move + " blocks and roll again.");
                        doubleCount++;
                    }

                    if (((player.getPositionBlock() + move) > board.size()) && (player.getPositionBlock() + move) % board.size() != 1) {
                        System.out.println(player.getName() + " passed GO! + Php 2,500");
                        player.updateCash(2500);
                    }
                    player.updatePositionBlock((player.getPositionBlock() + move) % board.size());

                    Block block;
                    
                    // error handling in case player lands exactly on the GO! block
                    if (player.getPositionBlock() == 0) {
                        block = board.get(player.getPositionBlock());
                    }
                    else {
                        block = board.get(player.getPositionBlock() - 1);
                    }
                   
                    block.landedOn(player, this, scanner);

                    // same error handling in case player lands exactly on the GO! block
                    // update block in case player lands on Manila Police District
                    if (player.getPositionBlock() == 0) { 
                        block = board.get(player.getPositionBlock());
                    }
                    else {
                        block = board.get(player.getPositionBlock() - 1);
                    }

                    System.out.println(player.getName() + "'s cash: Php " + player.getCash());
                    player.updateNetWorth();
                    System.out.println(player.getName() + "'s net worth: Php " + player.getNetWorth());
                    System.out.println(player.getName() + "'s position: " + player.getPositionBlock() + " (" + block.getName() + ")");

                    if (player.getCash() >= 0) {
                        player.updatePlayerLvl();
                    }

                   if (player.getCash() < 0) {
                        System.out.println(player.getName() + " is bankrupt! You are out of the game.");
                        playersToRemove.add(player);
                        doubleCount = 0;
                    }

                    if (d1 == d2 && doubleCount < 3 && player.getCash() >= 0) {
                        System.out.println(player.getName() + " gets to roll again!");
                    } 
                } while (doubleCount < 3 && d1 == d2);
                
                if (doubleCount == 3) { // get arrested for speeding
                    System.out.println(player.getName() + " rolled a double three times in a row! Go to Jail for Speeding!");
                    player.updatePositionBlock(11); // Move to Jail

                    Block block;

                    if (player.getPositionBlock() == 0) {
                        block = board.get(player.getPositionBlock());
                    }
                    else {
                        block = board.get(player.getPositionBlock() - 1);
                    }
                   int oldLevel = player.getPlayerLvl(); // Save current level before block effect

                    block.landedOn(player, this, scanner);

                    // same error handling in case player lands exactly on the GO! block
                    if (player.getPositionBlock() == 0) {
                    block = board.get(player.getPositionBlock());
                    } else {
                    block = board.get(player.getPositionBlock() - 1);
                    }
                    player.updateNetWorth();
                    player.updatePlayerLvl(); // Update level AFTER net worth

                    if (player.getPlayerLvl() > oldLevel) {
                         System.out.printf("%s has leveled up to Level %d!%n", player.getName(), player.getPlayerLvl());
                    }
                    System.out.println(player.getName() + "'s cash: Php " + player.getCash());
                    System.out.println(player.getName() + "'s net worth: Php " + player.getNetWorth());
                    System.out.println(player.getName() + "'s position: " + player.getPositionBlock() + " (" + block.getName() + ")");

                    if (player.getCash() >= 0) {
                        player.updatePlayerLvl();
                    }

                    if (player.getCash() < 0) {
                        System.out.println(player.getName() + " is bankrupt! You are out of the game.");
                        playersToRemove.add(player);
                    }
                }
                
            }

            // remove the bankrupt player from the arraylist of players
            if (players.size() > 1) {
                for (Player bankruptPlayer : playersToRemove) {
                    System.out.println("");
                    for (PropertyBlock property : bankruptPlayer.getOwnedProperties()) {
                        if (this.board.contains(property)) {
                            ((PropertyBlock) this.board.get(this.board.indexOf(property))).goPublic(); // Sets the property to be available to public
                        }
                    }

                    players.remove(bankruptPlayer); // removes first instance of bankruptPlayer from the arraylist
                }
            }
            
            gameOver = players.size() <= 1; // update gameOver flag
        }

        if (gameOver) {
            System.out.println("Game over!");
            if (players.size() == 1) {
                System.out.println(players.get(0).getName() + " has MONOPOLY over the heart of Manila!");
                System.out.println(players.get(0).getName() + " wins with a net worth of Php " + players.get(0).getNetWorth() + "!");

            }
        }
    }

    /** 
     * for Dice rolling Mechanic
     * @return int
     */
    public int rollDice() {
        return rand.nextInt(6) + 1;
    }

    /**
     * Checks the board for railroad blocks and returns the block numbers of railroad blocks
     * where there are no other players currently located (excluding the current player)
     * @param currentPlayer The player whose turn it is
     * @return ArrayList<Integer> containing block numbers of empty railroad blocks
     */
    public ArrayList<SpecialBlock> getEmptyRailroadBlocks(Player currentPlayer) {
        ArrayList<SpecialBlock> emptyRailroadBlocks = new ArrayList<>();
        
        // Railroad block numbers: LRT1 (6), PNR (16), LRT2 (26), MRT3 (36)
        String[] railroadBlockStations = {"LRT1", "PNR", "LRT2", "MRT3"};
        
        for (String railroadBlock : railroadBlockStations) {
            boolean hasOtherPlayer = false;
            
            for (Player player : players) {
                if (player != currentPlayer && this.board.get(player.getPositionBlock() - 1).getName().equals(railroadBlock)) {
                    hasOtherPlayer = true;
                    break;
                }
            }
            
            if (!hasOtherPlayer) {
                for (Block block : board) {
                    if (block instanceof SpecialBlock && block.getName().equals(railroadBlock)) {
                        emptyRailroadBlocks.add((SpecialBlock) block);
                        break; // Only add the first instance of that railroad block
                    }
                }
            }
        }
        return emptyRailroadBlocks;
    }
}
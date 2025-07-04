import java.util.Random;
import java.util.Scanner;

public class SpecialBlock extends Block {
    // Properties
    public String type;

    // Constructor
    public SpecialBlock(String name, int blockNumber, String type){
        super(name, blockNumber);
        this.type = type;
    }

    // Override methods
    @Override
    public void landedOn(Player player, Game game, Scanner scanner) { 

        int rand = new Random().nextInt(3); // for chance block

        switch(type) {
            case "GO":
                System.out.println(player.getName() + " has passed the GO block! + Php 2,500");
                player.updateCash(2500);
                break;
                
            case "Manila Police District":
                System.out.println(player.getName() + " has landed on Manila Police District.");
                System.out.println("You have been arrested! You will be sent to Manila City Jail.");
                player.updatePositionBlock(11); // Block 11 is the block number allocated for Manila City Jail in the Game board

            case "Manila City Jail":
                System.out.println(player.getName() + " has landed on Manila City Jail.");
                System.out.println("Roll the dice thrice, if you roll a double, you can get out of jail.");
                System.out.println("If you do not roll a double after three tries, you will have to pay Php 5,000 to get out of jail.");
                
                specialBlockManilaCityJail(player, game, scanner);
                break;

                
            case "Meralco":
                System.out.println(player.getName() + " has landed on Meralco.");
                System.out.println("You have to pay Php 2,500 for your electric bill.");
                player.updateCash(-2500);
                break;

            case "Maynilad":
                System.out.println(player.getName() + " has landed on Maynilad.");
                System.out.println("You have to pay Php 1,000 for your water bill.");
                player.updateCash(-1000);
                break;

            case "Income Tax":
                System.out.println(player.getName() + " has landed on Income Tax.");
                System.out.println("You have to pay Php 7,500 for your income tax.");
                player.updateCash(-7500);
                break;

            case "Real Property Tax":
                System.out.println(player.getName() + " has landed on Real Property Tax.");
                double tax = calculateRealPropertyTax(player);
                System.out.println("You have to pay Php " + tax + " for your real property tax.");
                player.updateCash(tax * -1);
                break;

            case "Internal Revenue Allotment":
                System.out.println(player.getName() + " has landed on Internal Revenue Allotment.");
                System.out.println("You have earned Php 5,000.");
                player.updateCash(5000.00);
                break;

            // Railroad blocks
            case "LRT1":
            case "LRT2":
            case "MRT3":
            case "PNR":
                System.out.println(player.getName() + " has landed on " + type + ".");
                // For future implementation (Phase 2)
                break;
            
            case "Chance":
                switch (rand) {
                    case 0:
                        System.out.println(player.getName() + " has landed on Chance! You have been given Php 20,000.");
                        player.updateCash(20000.00);
                        break;
                    case 1:
                        System.out.println(player.getName() + " has landed on Chance! You have been given a free property.");
                        // function call to give player a free property
                        giveFreeProperty(player, game);
                        break;
                    case 2:
                        System.out.println(player.getName() + " has landed on Chance! Php 5,000 has been taken away from you.");
                        player.updateCash(5000.00 *-1);
                        break;
                }
                break;

            case "Free Parking":
                System.out.println(player.getName() + " has landed on Free Parking! You can rest here for a turn.");
                break;

        }
    }


    // methods
    private void specialBlockManilaCityJail(Player player, Game game, Scanner scanner) {
        int ctr = 0;
        boolean isDouble = false;
        int d1 = game.rollDice();
        int d2 = game.rollDice();
        
        
        

         do {
            System.out.println("Press ENTER to roll the dice...");
            scanner.nextLine(); // Wait for player to press Enter

            System.out.println(player.getName() + " rolled a " + d1 + " and a " + d2 + ".");
            if (d1 == d2) {
                isDouble = true;
                System.out.println(player.getName() + " rolled a double! You can get out of jail.");
                player.updatePositionBlock(12); // Move player out of jail
            }
            else if (d1 != d2 && ctr < 3){

                if (ctr < 2) {
                    System.out.println(player.getName() + " did not roll a double, roll again.");
                }
                d1 = game.rollDice();
                d2 = game.rollDice();
            }
            ctr++;
        } while (ctr < 3 && !isDouble);
        if (ctr == 3 && !isDouble) {
            System.out.println(player.getName() + " did not roll a double. You will have to pay Php 5,000 to get out of jail.");
            player.updateCash(5000.00 *-1);
        }

    }

    private double calculateRealPropertyTax(Player player) {
        
        double totalTax = 0.00;

        for (PropertyBlock property : player.getOwnedProperties()) {
            if (property != null) { 
                totalTax = totalTax + (property.price * 0.05); 
            }
        }
        return totalTax;
    }

    private void giveFreeProperty (Player player, Game game) {
        // This method gives the next free property from GO! block.
        int i = 0;

       
        while (i < game.board.size()) {
            if (game.board.get(i) instanceof PropertyBlock) { // checks if a block is of PropertyBlock class type
                PropertyBlock property = (PropertyBlock) game.board.get(i); // explicit cast for Java <16
                if (property.getOwner() == null) { // casts into PropertyBlock to access its methods, then checks if it's now owned yet.
                    property.setOwner(player);
                    player.updateOwnedProperties(property);
                    player.updateNetWorth();
                    return;
                }
            
            }
            i++;
        }
        if (i == game.board.size()) {
            System.out.println("There are no more available properties in the board!");
        }

        
    }
    
}
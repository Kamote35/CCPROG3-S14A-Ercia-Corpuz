
import java.util.List;
import java.util.Scanner;

public class MonopolyManilaController {
    private MonopolyManilaGUI view;
    private Game game;
    private int currentPlayerIndex = 0;
    private boolean gameStarted = false;

    // Called by GUI to show the start dialog (for MVC consistency)
    public void showStartDialog() {
        view.showStartDialog();
    }

    public MonopolyManilaController(MonopolyManilaGUI view) {
        this.view = view;
    }

    public void startGame(List<String> playerNames) {
        game = new Game();
        game.players.clear();
        for (String name : playerNames) {
            game.players.add(new Player(name));
        }
        currentPlayerIndex = 0;
        gameStarted = true;
        view.updatePlayerPanel(game.players);
        view.updateBoardPanel(game.board, game.players);
        view.log("Game started with players: " + String.join(", ", playerNames));
        view.setRollDiceEnabled(true);
        nextTurn();
    }

    public void handleRollDice() {
        if (!gameStarted || game.players.isEmpty()) return;
        Player player = game.players.get(currentPlayerIndex);
        int doubleCount = 0;
        boolean turnOver = false;
        do {
            System.out.println("Input position: ");
            Scanner scanner = new Scanner(System.in);
            int d1 = scanner.nextInt();
            int d2 = d1;
            int move = d1;
            view.log(player.getName() + " rolled " + d1 + " and " + d2 + " (move: " + move + ")");
            int newPos = (move) % game.board.size();
            if (newPos == 0) newPos = game.board.size();
            player.updatePositionBlock(newPos);
            Block block = game.board.get(newPos - 1);
            view.log(player.getName() + " landed on block " + newPos + ": " + block.getName());

            // Property and special block handling
            if (block instanceof PropertyBlock property) {
                if (property.getOwner() == null) {
                    // Calculate discounted price
                    double originalPrice = property.price;
                    double discountRate = player.getDiscountRate();
                    double discountedPrice = originalPrice * (1 - discountRate);
                    
                    String discountText = "";
                    if (discountRate > 0) {
                        discountText = "\nOriginal Price: Php " + String.format("%.2f", originalPrice) +
                                     "\nYour Discount (" + (int)(discountRate * 100) + "%): -Php " + String.format("%.2f", originalPrice - discountedPrice) +
                                     "\nDiscounted Price: Php " + String.format("%.2f", discountedPrice);
                    }
                    
                    int option = javax.swing.JOptionPane.showConfirmDialog(view,
                            player.getName() + " (Level " + player.getPlayerLvl() + "), do you want to buy this property?\n\n" +
                            property.getName() + discountText +
                            (discountRate > 0 ? "" : "\nPrice: Php " + String.format("%.2f", originalPrice)),
                            "Buy Property", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                    if (option == javax.swing.JOptionPane.YES_OPTION) {
                        if (player.getCash() >= discountedPrice) {
                            // Apply the discounted price
                            player.updateCash(-discountedPrice);
                            property.setOwner(player);
                            player.updateOwnedProperties(property);
                            
                            if (discountRate > 0) {
                                view.log(player.getName() + " bought " + property.getName() + " with " + (int)(discountRate * 100) + "% discount for Php " + String.format("%.2f", discountedPrice) + " (Original: Php " + String.format("%.2f", originalPrice) + ")");
                            } else {
                                view.log(player.getName() + " bought " + property.getName() + " for Php " + String.format("%.2f", discountedPrice));
                            }
                            
                            // Check for level up after purchase
                            checkAndHandleLevelUp(player);
                            
                            // Check for immediate bankruptcy after purchase
                            if (checkAndHandleImmediateBankruptcy(player)) {
                                return; // Exit immediately if player went bankrupt and was removed
                            }
                        } else {
                            javax.swing.JOptionPane.showMessageDialog(view, "Not enough cash to buy this property!", "Insufficient Funds", javax.swing.JOptionPane.WARNING_MESSAGE);
                            view.log(player.getName() + " could not afford " + property.getName());
                        }
                    } else {
                        view.log(player.getName() + " chose not to buy " + property.getName());
                    }
                } else if (property.getOwner() == player) {
                    // Player landed on their own property
                    view.log(player.getName() + " landed on their own property: " + property.getName());
                    view.log("You can collect rent from other players who land here!");
                } else if (property.getOwner() != player) {
                    // Handle rent payment
                    Player owner = property.getOwner();
                    double rent = property.rentprice;
                    
                    view.log(player.getName() + " landed on " + property.getName() + " owned by " + owner.getName());
                    view.log("Rent: Php " + String.format("%.2f", rent));
                    
                    // Show rent payment dialog
                    javax.swing.JOptionPane.showMessageDialog(view,
                        player.getName() + " must pay rent to " + owner.getName() + "!\n\n" +
                        "Property: " + property.getName() + "\n" +
                        "Rent Amount: Php " + String.format("%.2f", rent),
                        "Rent Payment", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    
                    // Check if player can afford rent
                    if (player.getCash() >= rent) {
                        // Player pays rent
                        player.updateCash(-rent);
                        owner.updateCash(rent);
                        view.log(player.getName() + " paid Php " + String.format("%.2f", rent) + " rent to " + owner.getName());
                        view.log(player.getName() + " remaining cash: Php " + String.format("%.2f", player.getCash()));
                        view.log(owner.getName() + " received rent payment of Php " + String.format("%.2f", rent));
                        
                        // Check for level changes for both players
                        checkAndHandleLevelUp(owner); // Owner might level up from receiving rent
                    } else {
                        // Player can't afford rent - bankruptcy
                        double playerCash = player.getCash();
                        player.updateCash(-playerCash); // Take all remaining cash
                        owner.updateCash(playerCash); // Owner gets what player had
                        view.log(player.getName() + " cannot afford the full rent of Php " + String.format("%.2f", rent));
                        view.log(player.getName() + " paid all remaining cash (Php " + String.format("%.2f", playerCash) + ") to " + owner.getName());
                        
                        // Handle immediate bankruptcy and return properties to market
                        handlePlayerBankruptcy(player);
                        
                        // Remove bankrupt player from the game
                        int bankruptPlayerIndex = game.players.indexOf(player);
                        if (bankruptPlayerIndex != -1) {
                            game.players.remove(bankruptPlayerIndex);
                            view.log(player.getName() + " has been removed from the game due to bankruptcy.");
                            
                            // Adjust current player index if necessary
                            if (bankruptPlayerIndex < currentPlayerIndex) {
                                currentPlayerIndex--;
                            } else if (bankruptPlayerIndex == currentPlayerIndex) {
                                // Current player went bankrupt, adjust index
                                if (currentPlayerIndex >= game.players.size()) {
                                    currentPlayerIndex = 0;
                                }
                                // Mark turn as over since current player is gone
                                turnOver = true;
                            }
                            
                            // Update UI immediately
                            view.updatePlayerPanel(game.players);
                            view.updateBoardPanel(game.board, game.players);
                        }
                        
                        // Check for level changes for owner
                        checkAndHandleLevelUp(owner); // Owner might level up from receiving payment
                        
                        javax.swing.JOptionPane.showMessageDialog(view,
                            player.getName() + " cannot afford the rent!\n" +
                            "Paid all remaining cash: Php " + String.format("%.2f", playerCash) + "\n" +
                            player.getName() + " is now bankrupt!\n" +
                            "All owned properties have been returned to the market.",
                            "Bankruptcy", javax.swing.JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else if (block instanceof SpecialBlock special) {
                String type = special.type;
                switch (type) {
                    case "GO":
                        view.log(player.getName() + " has passed the GO block! + Php 2,500");
                        player.updateCash(2500);
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has passed the GO block! + Php 2,500", "GO", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        checkAndHandleLevelUp(player); // Check for level up after GO bonus
                        break;
                    case "Manila Police District":
                        view.log(player.getName() + " has landed on Manila Police District. You have been arrested! You will be sent to Manila City Jail.");
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Manila Police District. You have been arrested! You will be sent to Manila City Jail.", "Police District", javax.swing.JOptionPane.WARNING_MESSAGE);
                        player.updatePositionBlock(11);
                        view.log(player.getName() + " has landed on Manila City Jail. Roll the dice thrice, if you roll a double, you can get out of jail. If not, pay Php 5,000.");
                        int jailTries = 0;
                        boolean freed = false;
                        while (jailTries < 3 && !freed) {
                            int jailD1 = game.rollDice();
                            int jailD2 = game.rollDice();
                            javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " rolled a " + jailD1 + " and a " + jailD2 + ".", "Jail Roll", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                            if (jailD1 == jailD2) {
                                freed = true;
                                view.log(player.getName() + " rolled a double! You can get out of jail.");
                                javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " rolled a double! You can get out of jail.", "Jail", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                                player.updatePositionBlock(12); // Move out of jail
                            }
                            jailTries++;
                        }
                        if (!freed) {
                            view.log(player.getName() + " did not roll a double. You will have to pay Php 5,000 to get out of jail.");
                            javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " did not roll a double. You will have to pay Php 5,000 to get out of jail.", "Jail", javax.swing.JOptionPane.WARNING_MESSAGE);
                            player.updateCash(-5000);
                            
                            // Check for immediate bankruptcy after jail payment
                            if (checkAndHandleImmediateBankruptcy(player)) {
                                return; // Exit immediately if player went bankrupt and was removed
                            }
                        }
                        break;
                    case "Manila City Jail":
                        view.log(player.getName() + " has landed on Manila City Jail. Roll the dice thrice, if you roll a double, you can get out of jail. If not, pay Php 5,000.");
                        jailTries = 0;
                        freed = false;
                        while (jailTries < 3 && !freed) {
                            int jailD1 = game.rollDice();
                            int jailD2 = game.rollDice();
                            javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " rolled a " + jailD1 + " and a " + jailD2 + ".", "Jail Roll", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                            if (jailD1 == jailD2) {
                                freed = true;
                                view.log(player.getName() + " rolled a double! You can get out of jail.");
                                javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " rolled a double! You can get out of jail.", "Jail", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                                player.updatePositionBlock(12); // Move out of jail
                            }
                            jailTries++;
                        }
                        if (!freed) {
                            view.log(player.getName() + " did not roll a double. You will have to pay Php 5,000 to get out of jail.");
                            javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " did not roll a double. You will have to pay Php 5,000 to get out of jail.", "Jail", javax.swing.JOptionPane.WARNING_MESSAGE);
                            player.updateCash(-5000);
                            
                            // Check for immediate bankruptcy after jail payment
                            if (checkAndHandleImmediateBankruptcy(player)) {
                                return; // Exit immediately if player went bankrupt and was removed
                            }
                        }
                        break;
                    case "Meralco":
                        view.log(player.getName() + " has landed on Meralco. You have to pay Php 2,500 for your electric bill.");
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Meralco. You have to pay Php 2,500 for your electric bill.", "Meralco", javax.swing.JOptionPane.WARNING_MESSAGE);
                        player.updateCash(-2500);
                        
                        // Check for immediate bankruptcy after bill payment
                        if (checkAndHandleImmediateBankruptcy(player)) {
                            return; // Exit immediately if player went bankrupt and was removed
                        }
                        break;
                    case "Maynilad":
                        view.log(player.getName() + " has landed on Maynilad. You have to pay Php 1,000 for your water bill.");
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Maynilad. You have to pay Php 1,000 for your water bill.", "Maynilad", javax.swing.JOptionPane.WARNING_MESSAGE);
                        player.updateCash(-1000);
                        
                        // Check for immediate bankruptcy after bill payment
                        if (checkAndHandleImmediateBankruptcy(player)) {
                            return; // Exit immediately if player went bankrupt and was removed
                        }
                        break;
                    case "Income Tax":
                        view.log(player.getName() + " has landed on Income Tax. You have to pay Php 7,500 for your income tax.");
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Income Tax. You have to pay Php 7,500 for your income tax.", "Income Tax", javax.swing.JOptionPane.WARNING_MESSAGE);
                        player.updateCash(-7500);
                        
                        // Check for immediate bankruptcy after tax payment
                        if (checkAndHandleImmediateBankruptcy(player)) {
                            return; // Exit immediately if player went bankrupt and was removed
                        }
                        break;
                    case "Real Property Tax":
                        double totalTax = 0.0;
                        for (PropertyBlock prop : player.getOwnedProperties()) {
                            if (prop != null) totalTax += prop.price * 0.05;
                        }
                        view.log(player.getName() + " has landed on Real Property Tax. You have to pay Php " + totalTax + ".");
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Real Property Tax. You have to pay Php " + totalTax + ".", "Real Property Tax", javax.swing.JOptionPane.WARNING_MESSAGE);
                        player.updateCash(-totalTax);
                        break;
                    case "Internal Revenue Allotment":
                        view.log(player.getName() + " has landed on Internal Revenue Allotment. You have earned Php 5,000.");
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Internal Revenue Allotment. You have earned Php 5,000.", "IRA", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        player.updateCash(5000);
                        break;
                    case "LRT1":
                    case "LRT2":
                    case "MRT3":
                    case "PNR":
                        view.log(player.getName() + " has landed on " + type + ". Checking next available rail station...");
                        java.util.List<Integer> railIdxs = new java.util.ArrayList<>();
                        String[] rails = {"LRT1", "PNR", "LRT2", "MRT3"};
                        for (int i = 0; i < game.board.size(); i++) {
                            Block b = game.board.get(i);
                            if (b instanceof SpecialBlock sb && java.util.Arrays.asList(rails).contains(sb.type) && !b.getName().equals(type)) {
                                railIdxs.add(i);
                            }
                        }
                        if (railIdxs.isEmpty()) {
                            view.log("All rail stations are closed.");
                            javax.swing.JOptionPane.showMessageDialog(view, "All rail stations are closed.", "Railroad", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            int randIdx = railIdxs.get(new java.util.Random().nextInt(railIdxs.size()));
                            player.updatePositionBlock(randIdx + 1);
                            Block dest = game.board.get(randIdx);
                            view.log("Arriving in " + dest.getName() + " station.");
                            javax.swing.JOptionPane.showMessageDialog(view, "Arriving in " + dest.getName() + " station.", "Railroad", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    case "Chance":
                        int rand = new java.util.Random().nextInt(3);
                        if (rand == 0) {
                            view.log(player.getName() + " has landed on Chance! You have been given Php 20,000.");
                            javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Chance! You have been given Php 20,000.", "Chance", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                            player.updateCash(20000);
                        } else if (rand == 1) {
                            view.log(player.getName() + " has landed on Chance! You have been given a free property.");
                            javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Chance! You have been given a free property.", "Chance", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                            // Give next available property
                            boolean found = false;
                            for (Block b : game.board) {
                                if (b instanceof PropertyBlock prop) {
                                    if (prop.getOwner() == null) {
                                        prop.setOwner(player);
                                        player.updateOwnedProperties(prop);
                                        player.updateNetWorth();
                                        view.log(player.getName() + " received free property: " + prop.getName());
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if (!found) {
                                view.log("There are no more available properties in the board!");
                                javax.swing.JOptionPane.showMessageDialog(view, "There are no more available properties in the board!", "Chance", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            view.log(player.getName() + " has landed on Chance! Php 5,000 has been taken away from you.");
                            javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Chance! Php 5,000 has been taken away from you.", "Chance", javax.swing.JOptionPane.WARNING_MESSAGE);
                            player.updateCash(-5000);
                        }
                        break;
                    case "Free Parking":
                        view.log(player.getName() + " has landed on Free Parking! You can rest here for a turn.");
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Free Parking! You can rest here for a turn.", "Free Parking", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        break;
                }
            }

            // Speeding rule: 3 consecutive doubles
            if (d1 == d2) {
            //     doubleCount++;
            //     if (doubleCount == 3) {
            //         view.log(player.getName() + " rolled a double three times in a row! Go to Jail for Speeding!");
            //         javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " rolled a double three times in a row! Go to Jail for Speeding!", "Speeding", javax.swing.JOptionPane.WARNING_MESSAGE);
            //         player.updatePositionBlock(11); // Jail
            //         turnOver = true;
            //     } else {
            //         view.log(player.getName() + " rolled a double! Roll again.");
            //         javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " rolled a double! Roll again.", "Double!", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            //     }
            // } else {
                turnOver = true;
            }
        } while (!turnOver);

        // Update all players' levels at the end of the turn
        for (Player p : game.players) {
            checkAndHandleLevelUp(p);
        }

        view.updatePlayerPanel(game.players);
        view.updateBoardPanel(game.board, game.players);

        // Remove bankrupt players (cash < 0) and handle their properties
        java.util.List<Player> toRemove = new java.util.ArrayList<>();
        for (Player p : game.players) {
            if (p.getCash() < 0) {
                handlePlayerBankruptcy(p); // Handle bankruptcy and return properties
                toRemove.add(p);
                view.log(p.getName() + " has been removed from the game due to bankruptcy.");
            }
        }
        
        // Remove bankrupt players from the game
        for (Player p : toRemove) {
            int idx = game.players.indexOf(p);
            if (idx != -1) {
                game.players.remove(idx);
                if (idx < currentPlayerIndex) {
                    currentPlayerIndex--;
                } else if (idx == currentPlayerIndex && currentPlayerIndex >= game.players.size()) {
                    // If current player goes bankrupt and was the last player, reset index
                    currentPlayerIndex = 0;
                }
            }
        }
        
        // Update panels after player removal
        if (!toRemove.isEmpty()) {
            view.updatePlayerPanel(game.players);
            view.updateBoardPanel(game.board, game.players);
        }

        // Win condition: only one player left or no players left
        if (game.players.size() == 1) {
            Player winner = game.players.get(0);
            String winMsg = "Game over! " + winner.getName() + " has MONOPOLY over the heart of Manila!\n" +
                winner.getName() + " wins with a net worth of Php " + winner.getNetWorth() + "!";
            view.log(winMsg);
            javax.swing.JOptionPane.showMessageDialog(view,
                winMsg + "\n\nCongratulations, " + winner.getName() + "! You have won the game!",
                "Game Over - Winner!", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            int playAgain = javax.swing.JOptionPane.showConfirmDialog(view,
                "Would you like to play again?",
                "Play Again?",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (playAgain == javax.swing.JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                view.setRollDiceEnabled(false);
            }
            return;
        } else if (game.players.isEmpty()) {
            // Edge case: all players went bankrupt
            view.log("All players have gone bankrupt! The game ends with no winner.");
            javax.swing.JOptionPane.showMessageDialog(view,
                "All players have gone bankrupt!\nThe game ends with no winner.",
                "Game Over - No Winner!", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            int playAgain = javax.swing.JOptionPane.showConfirmDialog(view,
                "Would you like to play again?",
                "Play Again?",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (playAgain == javax.swing.JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                view.setRollDiceEnabled(false);
            }
            return;
        }

        // Advance to next player
        if (!game.players.isEmpty()) {
            currentPlayerIndex++;
            if (currentPlayerIndex >= game.players.size()) currentPlayerIndex = 0;
        }
        nextTurn();
    }

    public void nextTurn() {
        if (game.players.isEmpty()) return;
        if (currentPlayerIndex >= game.players.size()) currentPlayerIndex = 0;
        Player player = game.players.get(currentPlayerIndex);
        view.log("It's " + player.getName() + "'s turn!");
    }

    public void restartGame() {
        gameStarted = false;
        currentPlayerIndex = 0;
        view.resetView();
        // Prompt for new game after reset
        showStartDialog();
    }

    public Game getGame() {
        return game;
    }

    // Helper method to check and handle player level updates
    private void checkAndHandleLevelUp(Player player) {
        int oldLevel = player.getPlayerLvl();
        player.updatePlayerLvl();
        int newLevel = player.getPlayerLvl();
        
        // Notify if player leveled up
        if (newLevel > oldLevel) {
            String levelUpMsg = player.getName() + " leveled up to Level " + newLevel + "! ";
            switch (newLevel) {
                case 1:
                    levelUpMsg += "You now get a 5% discount on property purchases!";
                    break;
                case 2:
                    levelUpMsg += "You now get a 10% discount on property purchases!";
                    break;
                case 3:
                    levelUpMsg += "You now get a 25% discount on property purchases!";
                    break;
            }
            view.log(levelUpMsg);
            javax.swing.JOptionPane.showMessageDialog(view, levelUpMsg, "Level Up!", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Helper method to handle player bankruptcy and return properties to market
    private void handlePlayerBankruptcy(Player bankruptPlayer) {
        view.log("=== BANKRUPTCY PROCEEDINGS FOR " + bankruptPlayer.getName().toUpperCase() + " ===");
        
        // Return all properties owned by bankrupt player to the market
        java.util.List<PropertyBlock> ownedProperties = new java.util.ArrayList<>(bankruptPlayer.getOwnedProperties());
        if (ownedProperties.isEmpty()) {
            view.log(bankruptPlayer.getName() + " owned no properties to return to the market.");
        } else {
            view.log("Returning " + ownedProperties.size() + " properties to the market:");
            for (PropertyBlock property : ownedProperties) {
                if (property != null) {
                    property.setOwner(null); // Return property to market
                    view.log("  • " + property.getName() + " (₱" + String.format("%.2f", property.price) + ") is now available for purchase");
                }
            }
        }
        
        bankruptPlayer.getOwnedProperties().clear(); // Clear the player's property list
        bankruptPlayer.updateNetWorth(); // Update net worth to reflect loss of properties
        view.log(bankruptPlayer.getName() + " is BANKRUPT! Final cash: ₱" + String.format("%.2f", bankruptPlayer.getCash()));
        view.log("=== END BANKRUPTCY PROCEEDINGS ===");
    }

    // Helper method to immediately check and handle bankruptcy during gameplay
    private boolean checkAndHandleImmediateBankruptcy(Player player) {
        if (player.getCash() < 0) {
            handlePlayerBankruptcy(player);
            
            // Immediately remove bankrupt player from the game
            int bankruptPlayerIndex = game.players.indexOf(player);
            if (bankruptPlayerIndex != -1) {
                game.players.remove(bankruptPlayerIndex);
                view.log(player.getName() + " has been removed from the game due to bankruptcy.");
                
                // Adjust current player index if necessary
                if (bankruptPlayerIndex < currentPlayerIndex) {
                    currentPlayerIndex--;
                } else if (bankruptPlayerIndex == currentPlayerIndex) {
                    // Current player went bankrupt, adjust index
                    if (currentPlayerIndex >= game.players.size()) {
                        currentPlayerIndex = 0;
                    }
                }
                
                // Update UI immediately
                view.updatePlayerPanel(game.players);
                view.updateBoardPanel(game.board, game.players);
                
                // Check if game should end
                if (game.players.size() <= 1) {
                    return true; // Signal that game should end
                }
            }
        }
        return false; // No bankruptcy or game continues
    }
}


import java.util.List;

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
            int d1 = game.rollDice();
            int d2 = game.rollDice();
            int move = d1 + d2;
            view.log(player.getName() + " rolled " + d1 + " and " + d2 + " (move: " + move + ")");
            int newPos = (player.getPositionBlock() + move) % game.board.size();
            if (newPos == 0) newPos = game.board.size();
            player.updatePositionBlock(newPos);
            Block block = game.board.get(newPos - 1);
            view.log(player.getName() + " landed on block " + newPos + ": " + block.getName());

            // Property and special block handling
            if (block instanceof PropertyBlock property) {
                if (property.getOwner() == null) {
                    int option = javax.swing.JOptionPane.showConfirmDialog(view,
                            player.getName() + ", do you want to buy this property?\n\n" +
                            property.getName() + "\nPrice: Php " + String.format("%.2f", property.price),
                            "Buy Property", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                    if (option == javax.swing.JOptionPane.YES_OPTION) {
                        if (player.getCash() >= property.price) {
                            player.updateCash(-property.price);
                            property.setOwner(player);
                            player.updateOwnedProperties(property);
                            view.log(player.getName() + " bought " + property.getName() + " for Php " + String.format("%.2f", property.price));
                        } else {
                            javax.swing.JOptionPane.showMessageDialog(view, "Not enough cash to buy this property!", "Insufficient Funds", javax.swing.JOptionPane.WARNING_MESSAGE);
                            view.log(player.getName() + " could not afford " + property.getName());
                        }
                    } else {
                        view.log(player.getName() + " chose not to buy " + property.getName());
                    }
                } else if (property.getOwner() != player) {
                    // Optionally, handle rent payment here
                    view.log("This property is owned by " + property.getOwner().getName() + ".");
                }
            } else if (block instanceof SpecialBlock special) {
                String type = special.type;
                switch (type) {
                    case "GO":
                        view.log(player.getName() + " has passed the GO block! + Php 2,500");
                        player.updateCash(2500);
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has passed the GO block! + Php 2,500", "GO", javax.swing.JOptionPane.INFORMATION_MESSAGE);
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
                        }
                        break;
                    case "Meralco":
                        view.log(player.getName() + " has landed on Meralco. You have to pay Php 2,500 for your electric bill.");
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Meralco. You have to pay Php 2,500 for your electric bill.", "Meralco", javax.swing.JOptionPane.WARNING_MESSAGE);
                        player.updateCash(-2500);
                        break;
                    case "Maynilad":
                        view.log(player.getName() + " has landed on Maynilad. You have to pay Php 1,000 for your water bill.");
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Maynilad. You have to pay Php 1,000 for your water bill.", "Maynilad", javax.swing.JOptionPane.WARNING_MESSAGE);
                        player.updateCash(-1000);
                        break;
                    case "Income Tax":
                        view.log(player.getName() + " has landed on Income Tax. You have to pay Php 7,500 for your income tax.");
                        javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " has landed on Income Tax. You have to pay Php 7,500 for your income tax.", "Income Tax", javax.swing.JOptionPane.WARNING_MESSAGE);
                        player.updateCash(-7500);
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
                doubleCount++;
                if (doubleCount == 3) {
                    view.log(player.getName() + " rolled a double three times in a row! Go to Jail for Speeding!");
                    javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " rolled a double three times in a row! Go to Jail for Speeding!", "Speeding", javax.swing.JOptionPane.WARNING_MESSAGE);
                    player.updatePositionBlock(11); // Jail
                    turnOver = true;
                } else {
                    view.log(player.getName() + " rolled a double! Roll again.");
                    javax.swing.JOptionPane.showMessageDialog(view, player.getName() + " rolled a double! Roll again.", "Double!", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                turnOver = true;
            }
        } while (!turnOver);

        view.updatePlayerPanel(game.players);
        view.updateBoardPanel(game.board, game.players);

        // Remove bankrupt players (cash < 0)
        java.util.List<Player> toRemove = new java.util.ArrayList<>();
        for (Player p : game.players) {
            if (p.getCash() < 0) {
                view.log(p.getName() + " is bankrupt! You are out of the game.");
                toRemove.add(p);
            }
        }
        for (Player p : toRemove) {
            int idx = game.players.indexOf(p);
            if (idx != -1) {
                game.players.remove(idx);
                if (idx < currentPlayerIndex) {
                    currentPlayerIndex--;
                } else if (idx == currentPlayerIndex) {
                    // If current player goes bankrupt, don't increment index
                }
            }
        }

        // Win condition: only one player left
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
}

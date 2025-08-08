import java.awt.*;
import javax.swing.*;

public class MonopolyManilaGUI extends JFrame {
    // Store a fixed color for each player name
    private java.util.Map<String, Color> playerColorMap = new java.util.LinkedHashMap<>();
    private static final Color[] PLAYER_COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE};
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenuItem restartItem;
    private JTextArea gameLog;
    private JButton rollDiceButton;
    private JPanel playerPanel;
    private JPanel boardPanel;
    private JLabel[] blockLabels;
    private JPanel startMenuPanel;
    private CardLayout mainLayout;
    private JPanel mainPanel;
    private JPanel leaderboardPanel;
    private MonopolyManilaController controller;

    public MonopolyManilaGUI() {
        // Use CardLayout to switch between start menu and main game
        mainLayout = new CardLayout();
        mainPanel = new JPanel(mainLayout);

        // --- Start Menu Panel ---
        startMenuPanel = new JPanel();
        startMenuPanel.setLayout(new BoxLayout(startMenuPanel, BoxLayout.Y_AXIS));
        startMenuPanel.setBackground(new Color(245, 245, 220));
        JLabel title = new JLabel("Monopoly Manila"); // TItle
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setAlignmentX(CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("A Filipino Monopoly Experience"); // Subtitle
        subtitle.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        JButton startButton = new JButton("Start Game"); // Button to start the game
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setBackground(new Color(34, 139, 34));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setAlignmentX(CENTER_ALIGNMENT);
        startButton.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        startButton.addActionListener(e -> {
            mainLayout.show(mainPanel, "game");
            controller.showStartDialog();
        });
        startMenuPanel.add(Box.createVerticalGlue()); // automatically adjusts the start menu to center when resized
        startMenuPanel.add(title);
        startMenuPanel.add(Box.createVerticalStrut(10));
        startMenuPanel.add(subtitle);
        startMenuPanel.add(Box.createVerticalStrut(40));
        startMenuPanel.add(startButton);
        startMenuPanel.add(Box.createVerticalGlue());

        // --- Main Game Panel ---
        JPanel gamePanel = new JPanel(new BorderLayout(10, 10)); /// allocates space allowance in the gui, makes the sections of the gui not appear crowded
        gamePanel.setBackground(new Color(245, 245, 220));

        // Menu bar for restart
        menuBar = new JMenuBar();
        gameMenu = new JMenu("Menu");
        restartItem = new JMenuItem("Restart");
        restartItem.addActionListener(e -> controller.restartGame());
        gameMenu.add(restartItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
        setTitle("Monopoly Manila");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);

        // Log for game events that may be useful for the player
        gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        gameLog.setBackground(new Color(255, 255, 240));

        // Button for Dice rollinng mechanic
        rollDiceButton = new JButton("Roll Dice");
        rollDiceButton.setEnabled(false);
        rollDiceButton.setFont(new Font("Arial", Font.BOLD, 18));
        rollDiceButton.setBackground(new Color(34, 139, 34));
        rollDiceButton.setForeground(Color.WHITE);
        rollDiceButton.setFocusPainted(false);
        rollDiceButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 100, 0), 2),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)));
        rollDiceButton.addActionListener(e -> controller.handleRollDice());
        gamePanel.add(rollDiceButton, BorderLayout.SOUTH);

        playerPanel = new JPanel();
        playerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        playerPanel.setBackground(new Color(255, 248, 220));
        playerPanel.setBorder(BorderFactory.createTitledBorder("Players"));
        gamePanel.add(playerPanel, BorderLayout.NORTH);

        // --- Monopoly Board Layout ---
        boardPanel = new JPanel(new GridBagLayout()); // creates the Grid that will serve as the template of the blocks GUI
        boardPanel.setBackground(new Color(222, 184, 135));
        boardPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 3), "Manila Board", 0, 0, new Font("Arial", Font.BOLD, 18), new Color(139, 69, 19)));
        // Set a fixed preferred size for the board panel to prevent clipping
        boardPanel.setPreferredSize(new Dimension(600, 600));
        blockLabels = new JLabel[40]; // array to handle the blocks that make up the board
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1); // sets the spacing in between blocks
        // Place 40 blocks around a square (11 per side, corners shared)
        int[][] positions = new int[40][2];
        for (int i = 0; i < 11; i++) positions[i] = new int[]{0, i}; // Top row
        for (int i = 11; i < 20; i++) positions[i] = new int[]{i - 10, 10}; // Right col
        for (int i = 20; i < 31; i++) positions[i] = new int[]{10, 30 - i}; // Bottom row
        for (int i = 31; i < 40; i++) positions[i] = new int[]{40 - i, 0}; // Left col

        // Define property group colors (example: classic Monopoly)
        Color[] groupColors = {
            new Color(148, 0, 211), // Purple
            new Color(135, 206, 235), // Light Blue
            new Color(255, 105, 180), // Pink
            new Color(255, 165, 0), // Orange
            new Color(255, 255, 0), // Yellow
            new Color(0, 128, 0), // Green
            new Color(0, 191, 255), // Blue
            new Color(255, 0, 0), // Red
            new Color(210, 180, 140), // Brown
            new Color(169, 169, 169) // Gray (utilities, railroads, etc)
        };
        // Example mapping: block index to group color (customize as needed)
        int[] groupMap = { // array that assigns color to blocks, starts from GO then proceed counter-clockwise
            8,8,0,0,9,1,1,9,1,1,9, // 0-10
            2,9,2,2,9,2,2,9,3,9,   // 11-20
            3,3,9,4,4,9,4,4,9,5,   // 21-30
            5,5,9,6,6,9,7,7,9,8    // 31-39
        };
        int[] cornerIdx = {0, 10, 20, 30};
        for (int i = 0; i < 40; i++) {
            blockLabels[i] = new JLabel("", SwingConstants.CENTER);
            blockLabels[i].setOpaque(true);
            // Reduce font size for better fit
            blockLabels[i].setFont(new Font("Arial", Font.PLAIN, 9));
            blockLabels[i].setPreferredSize(new Dimension(48, 48));
            // Corners: bold border, special color
            boolean isCorner = false;
            for (int c : cornerIdx) if (i == c) isCorner = true;
            if (isCorner) {
                blockLabels[i].setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 4));
                blockLabels[i].setBackground(new Color(255, 250, 205));
            } else {
                blockLabels[i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                blockLabels[i].setBackground(groupColors[groupMap[i]]);
            }
            blockLabels[i].setForeground(Color.BLACK);
            gbc.gridx = positions[i][1];
            gbc.gridy = positions[i][0];
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            boardPanel.add(blockLabels[i], gbc);
        }
        // Fill center with a styled label (logo)
        JLabel center = new JLabel("<html><center><b>Monopoly Manila</b></center></html>", SwingConstants.CENTER);
        center.setFont(new Font("Arial", Font.BOLD, 16));
        center.setForeground(new Color(139, 69, 19));
        center.setBackground(new Color(245, 245, 220));
        center.setOpaque(true);
        center.setPreferredSize(new Dimension(432, 432));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 9;
        gbc.gridheight = 9;
        boardPanel.add(center, gbc);
        gamePanel.add(boardPanel, BorderLayout.CENTER);

        // Leaderboard panel setup
        leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setBackground(new Color(240, 248, 255));
        leaderboardPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2), "Net Worth Leaderboard", 0, 0, new Font("Arial", Font.BOLD, 14), new Color(70, 130, 180)));

        // Move game log to the right
        JScrollPane logScrollPane = new JScrollPane(gameLog);
        logScrollPane.setPreferredSize(new Dimension(300, 600));
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Game Log"));
        // Create a right panel that contains both leaderboard and game log
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.add(leaderboardPanel, BorderLayout.NORTH);
        rightPanel.add(logScrollPane, BorderLayout.CENTER);
        gamePanel.add(rightPanel, BorderLayout.EAST);

        // Add both panels to mainPanel
        mainPanel.add(startMenuPanel, "start");
        mainPanel.add(gamePanel, "game");
        setContentPane(mainPanel);
        mainLayout.show(mainPanel, "start");

        // Initialize controller
        controller = new MonopolyManilaController(this);
    }

    // Called by controller to reset the view
    public void resetView() {
        gameLog.setText("");
        for (JLabel label : blockLabels) {
            label.setText("");
            label.setBackground(new Color(255, 255, 255));
        }
        playerPanel.removeAll();
        playerPanel.revalidate();
        playerPanel.repaint();
        leaderboardPanel.removeAll();
        leaderboardPanel.revalidate();
        leaderboardPanel.repaint();
        rollDiceButton.setEnabled(false);
        // Optionally, return to start menu on reset
        mainLayout.show(mainPanel, "start");
    }

    // Called by controller to show the start dialog (player input)
    public void showStartDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField[] playerFields = new JTextField[4];
        panel.add(new JLabel("Enter number of players (2-4):"));
        SpinnerNumberModel model = new SpinnerNumberModel(2, 2, 4, 1);
        JSpinner spinner = new JSpinner(model);
        panel.add(spinner);
        for (int i = 0; i < 4; i++) {
            playerFields[i] = new JTextField();
            panel.add(new JLabel("Player " + (i + 1) + " name:"));
            panel.add(playerFields[i]);
        }
        boolean valid = false;
        while (!valid) {
            int result = JOptionPane.showConfirmDialog(this, panel, "Start Game", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                int numPlayers = (int) spinner.getValue();
                java.util.List<String> names = new java.util.ArrayList<>();
                int filled = 0;
                for (int i = 0; i < numPlayers; i++) {
                    String name = playerFields[i].getText().trim();
                    if (!name.isEmpty()) {
                        names.add(name);
                        filled++;
                    }
                }
                // Check if all required fields are filled and no extra fields are filled
                boolean extraFilled = false;
                for (int i = numPlayers; i < playerFields.length; i++) {
                    if (!playerFields[i].getText().trim().isEmpty()) {
                        extraFilled = true;
                        break;
                    }
                }
                if (filled != numPlayers || extraFilled) {
                    JOptionPane.showMessageDialog(this, "Please enter exactly " + numPlayers + " player name(s) and leave the rest blank.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Assign fixed colors to player names
                    playerColorMap.clear();
                    for (int i = 0; i < names.size(); i++) {
                        playerColorMap.put(names.get(i), PLAYER_COLORS[i % PLAYER_COLORS.length]);
                    }
                    valid = true;
                    // Switch to game panel after player input
                    mainLayout.show(mainPanel, "game");
                    controller.startGame(names);
                }
            } else {
                System.exit(0);
            }
        }
    }

    // Called by controller to enable/disable roll button
    public void setRollDiceEnabled(boolean enabled) {
        rollDiceButton.setEnabled(enabled);
    }

    // Called by controller to update the leaderboard
    public void updateLeaderboard(java.util.List<Player> players) {
        // Update net worth for all players before displaying
        for (Player player : players) {
            player.updateNetWorth();
        }
        
        // Sort players by net worth in descending order
        java.util.List<Player> sortedPlayers = new java.util.ArrayList<>(players);
        sortedPlayers.sort((p1, p2) -> Double.compare(p2.getNetWorth(), p1.getNetWorth()));
        
        leaderboardPanel.removeAll();
        
        // Add title
        JLabel titleLabel = new JLabel("🏆 Net Worth Rankings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        leaderboardPanel.add(titleLabel);
        
        // Add players in order
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player player = sortedPlayers.get(i);
            String rank;
            switch (i + 1) {
                case 1: rank = "1st"; break;
                case 2: rank = "2nd"; break;
                case 3: rank = "3rd"; break;
                case 4: rank = "4th"; break;
                default: rank = (i + 1) + "th";
            }
            JLabel playerLabel = new JLabel("<html><center>" + rank + " " + player.getName() + 
                "<br>Net Worth: ₱" + String.format("%.2f", player.getNetWorth()) + "</center></html>");
            Color bgColor = playerColorMap.getOrDefault(player.getName(), Color.LIGHT_GRAY);
            playerLabel.setBackground(bgColor);
            playerLabel.setOpaque(true);
            playerLabel.setForeground(Color.WHITE);
            playerLabel.setFont(new Font("Arial", Font.BOLD, 12));
            playerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            leaderboardPanel.add(playerLabel);
            leaderboardPanel.add(Box.createVerticalStrut(5)); // Add spacing between entries
        }
        
        leaderboardPanel.revalidate();
        leaderboardPanel.repaint();
    }

    // Called by controller to update player info panel
    public void updatePlayerPanel(java.util.List<Player> players) {
        updatePlayerPanel(players, java.util.Collections.emptyList());
        updateLeaderboard(players); // Update leaderboard whenever player panel updates
    }

    // Overloaded to support showing bankrupt players
    public void updatePlayerPanel(java.util.List<Player> activePlayers, java.util.List<Player> bankruptPlayers) {
        playerPanel.removeAll();
        // Show all players in original order, using fixed color for each name
        java.util.Set<String> bankruptNames = new java.util.HashSet<>();
        for (Player p : bankruptPlayers) bankruptNames.add(p.getName());
        java.util.List<Player> allPlayers = new java.util.ArrayList<>();
        allPlayers.addAll(activePlayers);
        for (Player p : bankruptPlayers) {
            if (!allPlayers.contains(p)) allPlayers.add(p);
        }
        for (Player p : allPlayers) {
            String discountInfo = "";
            try {
                java.lang.reflect.Method m = p.getClass().getMethod("getDiscounts");
                Object val = m.invoke(p);
                if (val != null && !val.toString().isEmpty()) {
                    discountInfo = " | Discounts: " + val.toString();
                }
            } catch (Exception e) {}
            JLabel label;
            if (bankruptNames.contains(p.getName())) {
                label = new JLabel(p.getName() + " | BANKRUPT");
                label.setBackground(Color.GRAY);
            } else {
                label = new JLabel(p.getName() + " | Cash: Php " + String.format("%.2f", p.getCash()) + " | Level: " + p.getPlayerLvl() + discountInfo);
                Color c = playerColorMap.getOrDefault(p.getName(), Color.LIGHT_GRAY);
                label.setBackground(c);
            }
            label.setOpaque(true);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            playerPanel.add(label);
        }
        playerPanel.revalidate();
        playerPanel.repaint();
        
        // Update leaderboard with active players
        updateLeaderboard(activePlayers);
    }

    // Called by controller to log messages
    public void log(String msg) {
        gameLog.append(msg + "\n");
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }

    // Called by controller to update the board panel
    public void updateBoardPanel(java.util.List<Block> board, java.util.List<Player> players) {
        if (board == null) return;
        // Set block names, backgrounds, and tooltips
        int[] cornerIdx = {0, 10, 20, 30};
        for (int i = 0; i < 40; i++) {
            Block block = board.get(i);
            String blockType = block.getClass().getSimpleName();
            String name = block.getName();
            // Use only the first word or truncate to fit
            String shortName = name.split(" ")[0];
            if (shortName.length() > 8) shortName = shortName.substring(0, 8) + "..";
            String labelHtml = "<html><center>" + (i+1) + ".<br>" + shortName + "</center></html>";
            blockLabels[i].setText(labelHtml);
            blockLabels[i].setToolTipText(blockType + ": " + name);
            // Corners: keep special color
            boolean isCorner = false;
            for (int c : cornerIdx) if (i == c) isCorner = true;
            if (isCorner) {
                blockLabels[i].setBackground(new Color(255, 250, 205));
                blockLabels[i].setForeground(new Color(139, 69, 19));
                blockLabels[i].setFont(new Font("Arial", Font.BOLD, 11));
            } else if ("SpecialBlock".equals(blockType)) {
                // Assign unique color for each special block by name
                Color specialColor;
                switch (name.toLowerCase()) {
                    case "go":
                        specialColor = new Color(255, 255, 255); // White
                        break;
                    case "jail":
                        specialColor = new Color(255, 140, 0); // Orange
                        break;
                    case "free":
                        specialColor = new Color(144, 238, 144); // Light green
                        break;
                    case "go to jail":
                        specialColor = new Color(220, 20, 60); // Crimson
                        break;
                    case "chance":
                        specialColor = new Color(30, 144, 255); // Dodger blue
                        break;
                    case "community":
                        specialColor = new Color(255, 215, 0); // Gold
                        break;
                    case "tax":
                        specialColor = new Color(205, 92, 92); // Indian red
                        break;
                    case "utility":
                        specialColor = new Color(169, 169, 169); // Dark gray
                        break;
                    case "train":
                        specialColor = new Color(0, 0, 0); // Black
                        break;
                    default:
                        specialColor = new Color(200, 200, 200); // Default gray
                }
                blockLabels[i].setBackground(specialColor);
                blockLabels[i].setForeground(Color.BLACK);
                blockLabels[i].setFont(new Font("Arial", Font.BOLD, 9));
            } else {
                // Keep color from initial setup
                blockLabels[i].setForeground(Color.BLACK);
                blockLabels[i].setFont(new Font("Arial", Font.PLAIN, 9));
            }
        }
        // Show player positions
        for (int p = 0; p < players.size(); p++) {
            Player player = players.get(p);
            int pos = player.getPositionBlock();
            if (pos == 0) pos = 1;
            int idx = pos - 1;
            Color c = playerColorMap.getOrDefault(player.getName(), PLAYER_COLORS[p % PLAYER_COLORS.length]);
            blockLabels[idx].setBackground(c.darker());
            blockLabels[idx].setForeground(Color.WHITE);
            // Show only the first letter of the player's name for fit
            String labelText = blockLabels[idx].getText().replace("</center></html>", "<br><b>" + player.getName().charAt(0) + "</b></center></html>");
            blockLabels[idx].setText(labelText);
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }
    // Removed unused updateBoardPanel() method (was referencing removed 'game' field)

    // Called by controller to show whose turn it is
    public void showTurn(String playerName) {
        log("It's " + playerName + "'s turn!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MonopolyManilaGUI().setVisible(true);
        });
    }
}

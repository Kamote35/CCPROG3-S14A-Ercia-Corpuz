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
    private MonopolyManilaController controller;

    public MonopolyManilaGUI() {
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
        setSize(1100, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 220)); // Light beige background
        setLayout(new BorderLayout(10, 10));

        gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        gameLog.setBackground(new Color(255, 255, 240));

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
        add(rollDiceButton, BorderLayout.SOUTH);

        playerPanel = new JPanel();
        playerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        playerPanel.setBackground(new Color(255, 248, 220));
        playerPanel.setBorder(BorderFactory.createTitledBorder("Players"));
        add(playerPanel, BorderLayout.NORTH);

        // Board panel setup
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(4, 10, 4, 4)); // 40 blocks, 4x10 grid, with gaps
        boardPanel.setBackground(new Color(222, 184, 135));
        boardPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 3), "Manila Board", 0, 0, new Font("Arial", Font.BOLD, 18), new Color(139, 69, 19)));
        blockLabels = new JLabel[40];
        for (int i = 0; i < 40; i++) {
            blockLabels[i] = new JLabel("", SwingConstants.CENTER);
            blockLabels[i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            blockLabels[i].setOpaque(true);
            blockLabels[i].setBackground(new Color(255, 255, 255));
            blockLabels[i].setFont(new Font("Arial", Font.BOLD, 12));
            boardPanel.add(blockLabels[i]);
        }
        add(boardPanel, BorderLayout.CENTER);

        // Move game log to the right
        JScrollPane logScrollPane = new JScrollPane(gameLog);
        logScrollPane.setPreferredSize(new Dimension(300, 600));
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Game Log"));
        add(logScrollPane, BorderLayout.EAST);

        // Initialize controller and show start dialog
        controller = new MonopolyManilaController(this);
        controller.showStartDialog();
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
        rollDiceButton.setEnabled(false);
    }

    // Called by controller to show the start dialog
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

    // Called by controller to update player info panel
    public void updatePlayerPanel(java.util.List<Player> players) {
        updatePlayerPanel(players, java.util.Collections.emptyList());
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
    }

    // Called by controller to log messages
    public void log(String msg) {
        gameLog.append(msg + "\n");
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }

    // Called by controller to update the board panel
    public void updateBoardPanel(java.util.List<Block> board, java.util.List<Player> players) {
        if (board == null) return;
        // Set block names and clear backgrounds
        for (int i = 0; i < 40; i++) {
            Block block = board.get(i);
            String blockType = block.getClass().getSimpleName();
            String color = "#FFFFFF";
            if ("PropertyBlock".equals(blockType)) color = "#F0E68C"; // Khaki for property
            else if ("SpecialBlock".equals(blockType)) color = "#ADD8E6"; // Light blue for special
            blockLabels[i].setText("<html><center>" + (i+1) + ".<br>" + block.getName() + "</center></html>");
            blockLabels[i].setBackground(Color.decode(color));
            blockLabels[i].setForeground(Color.DARK_GRAY);
            blockLabels[i].setFont(new Font("Arial", Font.BOLD, 12));
        }
        // Show player positions
        for (int p = 0; p < players.size(); p++) {
            Player player = players.get(p);
            int pos = player.getPositionBlock();
            if (pos == 0) pos = 1;
            int idx = pos - 1;
            Color c = playerColorMap.getOrDefault(player.getName(), PLAYER_COLORS[p % PLAYER_COLORS.length]);
            blockLabels[idx].setBackground(c);
            blockLabels[idx].setForeground(Color.WHITE);
            String labelText = blockLabels[idx].getText().replace("</center></html>", "<br><b>" + player.getName() + "</b></center></html>");
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

import java.util.ArrayList;

// Player class that represents a player in the Monopoly Manila game
public class Player {
    public boolean isBankrupt;

    // Constants
    final public int max = 22;

    // Properties
    final private String name;
    private double cash;
    private int playerLvl;
    private int positionBlock;
    final private ArrayList<PropertyBlock> ownedProperties;
    private double netWorth;
    

    // Constructors
    public Player(String name){
        this.isBankrupt = false;
        this.name = name;
        this.cash = 50000; // Starting cash of Php 50,000
        this.playerLvl = 0; 
        this.positionBlock = 1; // starting position is block 1 (GO Block)
        this.ownedProperties = new ArrayList<>();
        this.netWorth = this.cash; 
    }

    /** 
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /** 
     * @return double
     */
    public double getCash() {
        return this.cash;
    }

    /** 
     * @return int
     */
    public int getPlayerLvl() {
        return playerLvl;
    }

    /** 
     * @return int
     */
    public int getPositionBlock() {
        return positionBlock;
    }
    
    /** 
     * @return int
     */
    public int getNumberOwnedProperties() {
        return this.ownedProperties.size();
    }
    
    
    /** 
     * @return ArrayList<PropertyBlock>
     */
    public ArrayList<PropertyBlock> getOwnedProperties() {
        return this.ownedProperties;
    }
    
    /** 
     * @return double
     */
    public double getNetWorth() {
        return this.netWorth;
    }

    /** 
     * Returns the discount rate based on the player's level.
     * @return double
     */
    public double getDiscountRate() {
        switch (playerLvl) {
            case 1: return 0.05; 
            case 2: return 0.10; 
            case 3: return 0.25; 
            default: return 0.0;
        }  

    }

    /** 
     * Returns the discount percentage as a string.
     * @return String
     */
    public String getDiscounts() {
        if (playerLvl > 0) {
            return (int)(getDiscountRate() * 100) + "%";
        }
        return "";
    }

    /** 
     * @param amount
     */
    public void updateCash(double amount) {
        this.cash += amount;
    }
    
    /** 
     * @param positionBlock
     */
    public void updatePositionBlock(int positionBlock) {
        this.positionBlock = positionBlock;
    }

    /** 
     * @param property
     */
    public void updateOwnedProperties(PropertyBlock property) {

        if (ownedProperties.isEmpty()) {
            ownedProperties.add(property);
        }
        
        else if (ownedProperties.size() < max) {
            ownedProperties.add(property);
        }
        else {
            System.out.println(this.name + " already owns the maximum number of properties!");
        }
    }


    public void updatePlayerLvl() {
        updateNetWorth(); // always update first
        double worth = this.netWorth;
        // Only increase level, never decrease
        if (worth >= 125000.00 && this.playerLvl < 3) {
            this.playerLvl = 3;
            System.out.println(this.name + " is now Level 3! Enjoy a 25% discount on property purchases.");
        } else if (worth >= 100000.00 && this.playerLvl < 2) {
            this.playerLvl = 2;
            System.out.println(this.name + " is now Level 2! Enjoy a 10% discount on property purchases.");
        } else if (worth >= 75000.00 && this.playerLvl < 1) {
            this.playerLvl = 1;
            System.out.println(this.name + " is now Level 1! Enjoy a 5% discount on property purchases.");
        }
        // If worth drops, do not decrease level
    }
    public void updateNetWorth() {
        this.netWorth = this.cash;
        for (PropertyBlock property : ownedProperties) {
            if (property != null) {
                this.netWorth += property.price;
            }
        }
    }


}
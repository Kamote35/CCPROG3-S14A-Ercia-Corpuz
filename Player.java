import java.util.ArrayList;

// Player class that represents a player in the Monopoly Manila game
public class Player {

    // Constants
    final public int max = 22;

    // Properties
    final private String name;
    private double cash;
    private int playerLvl;
    private int positionBlock;
    final private ArrayList<PropertyBlock> ownedProperties;
    final private ArrayList<PropertyBlock> rentedProperties;
    private double netWorth;
    

    // Constructors
    public Player(String name){
        this.name = name;
        this.cash = 50000; // Starting cash of Php 50,000
        this.playerLvl = 1; 
        this.positionBlock = 1; // starting position is block 1 (GO Block)
        this.ownedProperties = new ArrayList<>();
        this.rentedProperties = new ArrayList<>();
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
     * @return int
     */
    public int getNumberRentedProperties() {
        return this.rentedProperties.size();
    }

    /** 
     * @return ArrayList<PropertyBlock>
     */
    public ArrayList<PropertyBlock> getOwnedProperties() {
        return this.ownedProperties;
    }

    /** 
     * @return ArrayList<PropertyBlock>
     */
    public ArrayList<PropertyBlock> getRentedProperties() {
        return this.rentedProperties;
    }

    /** 
     * @return double
     */
    public double getNetWorth() {
        return this.netWorth;
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

    /** 
     * @param property
     */
    public void updateRentedProperties(PropertyBlock property) {
        for (int i = 0; i < max; i++) {
            if (rentedProperties.get(i) == null) {
                rentedProperties.set(i, property);
                break;
            }
        }
    }

    public void updatePlayerLvl() {
    updateNetWorth(); // always update first
    double worth = this.netWorth;

    if (worth >= 1250000) {
        this.playerLvl = 3;
    } else if (worth >= 600000) {
        this.playerLvl = 2;
    } else if (worth >= 250000) {
        this.playerLvl = 1;
    } else {
        this.playerLvl = 0;
    }
}
    public void updateNetWorth() {
        this.netWorth = this.cash;
        for (PropertyBlock property : ownedProperties) {
            if (property != null) {
                this.netWorth += property.price;
            }
        }
    }

    public double getDiscountRate() {
    switch (playerLvl) {
        case 2: return 0.05;
        case 3: return 0.10;
        case 4: return 0.25;
        default: return 0.0;
    }

}

}
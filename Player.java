import java.util.ArrayList;

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

    // Getters
    public String getName() {
        return this.name;
    }

    public double getCash() {
        return this.cash;
    }

    public int getPlayerLvl() {
        return playerLvl;
    }

    public int getPositionBlock() {
        return positionBlock;
    }

    public int getNumberOwnedProperties() {
        return this.ownedProperties.size();
    }

    public int getNumberRentedProperties() {
        return this.rentedProperties.size();
    }

    public ArrayList<PropertyBlock> getOwnedProperties() {
        return this.ownedProperties;
    }

    public ArrayList<PropertyBlock> getRentedProperties() {
        return this.rentedProperties;
    }

    public double getNetWorth() {
        return this.netWorth;
    }

    // Setters
    public void updateCash(double amount) {
        this.cash += amount;
    }

    public void updatePositionBlock(int positionBlock) {
        this.positionBlock = positionBlock;
    }

    public void updateOwnedProperties(PropertyBlock property) {
        int i;
        for (i = 0; i < max; i++) {
            if (ownedProperties.get(i) == null) {
                ownedProperties.set(i, property);
                break;
            }
        }
        if (i == max) {
            System.out.println(this.name + " already owns the maximum number of properties!");
        }
    }

    public void updateRentedProperties(PropertyBlock property) {
        for (int i = 0; i < max; i++) {
            if (rentedProperties.get(i) == null) {
                rentedProperties.set(i, property);
                break;
            }
        }
    }

    public void updatePlayerLvl() {
        this.playerLvl++;
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
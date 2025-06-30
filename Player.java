import java.util.ArrayList;


public class Player {

    // properties
    final private String name;
    private double cash;
    private int playerLvl;
    private int positionBlock;
    final int max = 22;
    ArrayList<PropertyBlock> ownedProperties;
    ArrayList <PropertyBlock> rentedProperties;

    // constructor
    public Player(String name){
        this.name = name;
        this.cash = 50000;
        this.playerLvl = 1; // default level
        this.positionBlock = 0; // starting position is block 0 (GO Block)
        this.ownedProperties = new ArrayList<>();
        this.rentedProperties = new ArrayList<>();
    }

    // methods: getters
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

    public PropertyBlock[] getOwnedProperties() {
        return this.ownedProperties.size();

    public PropertyBlock[] getRentedProperties() {
        return this.rentedProperties;
    }

    // methods: setters
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
    

}
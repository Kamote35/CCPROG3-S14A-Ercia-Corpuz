public class Player {

    // properties
    private String name;
    private double cash;
    private int playerLvl;
    private int positionBlock;
    private PropertyBlock[] ownedProperties;
    private PropertyBlock[] rentedProperties; // we can use the length property of the array to determine the number of properties
    final int max = 22;
    
    // constructor
    public Player(String name){
        this.name = name;
        this.cash = 50000;
        this.ownedHouses = 0;
        this.rentedHouses = 0;
        this.playerLvl = 1; // default level
        this.positionBlock = 0; // starting position is block 0 (GO Block)
        this.ownedProperties = new PropertyBlock[max]; // set the size to 22
        this.rentedProperties = new PropertyBlock[max]; // since that is the maximum number of properties in the game board
    }

    // methods: getters
    public String getName() {
        return name;
    }

    public double getCash() {
        return cash;
    }

    public int getPlayerLvl() {
        return playerLvl;
    }

    public int getPositionBlock() {
        return positionBlock;
    }

    public int getOwnedHouses() {
        return this.ownedProperties.length;
    }

    public int getRentedHouses() {
        return this.rentedProperties.length;
    }

    public PropertyBlock[] getOwnedProperties() {
        return ownedProperties;
    }

    public PropertyBlock[] getRentedProperties() {
        return rentedProperties;
    }

    // methods: setters
    public void updateCash(double amount) {
        this.cash += amount;
    }
    public void updatePositionBlock(int positionBlock) {
        this.positionBlock = positionBlock;
    }
    public void updateOwnedProperties(PropertyBlock property) {
        for (int i = 0; i < max; i++) {
            if (ownedProperties[i] == null) {
                ownedProperties[i] = property;
                break;
            }
        }
    }
    public void updateRentedProperties(PropertyBlock property) {
        for (int i = 0; i < max; i++) {
            if (rentedProperties[i] == null) {
                rentedProperties[i] = property;
                break;
            }
        }
    }
    public void updatePlayerLvl() {
        this.playerLvl++;
    }
    

}
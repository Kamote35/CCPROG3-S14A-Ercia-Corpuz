import java.util.Scanner;
// Block superclass for SpecialBlock subclass and PropertyBlock subclass
public abstract class Block {

    // Properties
    protected String name;
    protected int blockNumber;


    // Constructors
    public Block(String name, int blockNumber) {
        this.name = name;
        this.blockNumber = blockNumber;
    }


    /** 
     * Abstract method to be implemented by subclasses.
     * @param player
     * @param game
     * @param getName(
     */
    public abstract void landedOn (Player player, Game game, Scanner scanner);

    /** 
     * Returns the name of the block.
     * @return String
     */
    public String getName() {
        return this.name;
    }
    
    /** 
     * Returns the block number.
     * @return int
     */
    public int getBlockNumber() {
        return this.blockNumber;
    }
}

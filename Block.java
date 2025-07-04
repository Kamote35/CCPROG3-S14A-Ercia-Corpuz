import java.util.Scanner;

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
     * @param player
     * @param game
     * @param getName(
     */
    public abstract void landedOn (Player player, Game game, Scanner scanner);

    /** 
     * @return String
     */
    public String getName() {
        return this.name;
    }
}

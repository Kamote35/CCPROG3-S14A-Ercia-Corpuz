public abstract class Block {

    // Properties
    protected String name;
    protected int blockNumber;


    // Constructors
    public Block(String name, int blockNumber) {
        this.name = name;
        this.blockNumber = blockNumber;
    }


    // methods
    public abstract void landedOn (Player player, Game game);
}

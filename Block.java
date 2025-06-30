public abstract class Block {

    // properties
    protected String name;
    protected int blockNumber;


    // constructor
    public Block(String name, int blockNumber) {
        this.name = name;
        this.blockNumber = blockNumber;
    }


    // methods
    public abstract void landedOn (Player player, Game game);
}

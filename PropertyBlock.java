import java.util.Scanner;

public class PropertyBlock extends Block{
    // properties
    private String street;
    private double price;
    private double rentprice;
    private Player Owner;


    // constructor 
    public PropertyBlock(String name, int blockNumber, String street, double price, double rentprice) {
        super(name, blockNumber);
        this.street = street;
        this.price = price;
        this.rentprice = rentprice;
        this.Owner = null; // instantiates the owner to null
    }

    // methods
    public boolean isOwned() {
        if (this.Owner != null) {
            return true;
        } else {
            return false;
        }
    }

    public void setOwner(Player owner) {
        this.Owner = owner;
    }





}
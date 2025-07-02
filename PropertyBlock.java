import java.util.Scanner;


public class PropertyBlock extends Block{
    // properties
    final public String street;
    final public double price;
    final public double rentprice;
    public Player Owner;


    // Constructors
    public PropertyBlock(String name, String street, int blockNumber, double price, double rentprice) {
        super(name, blockNumber);
        this.street = street;
        this.price = price;
        this.rentprice = rentprice;
        this.Owner = null; // instantiates the owner to null
    }

    // Override methods
    @Override
    public void landedOn(Player player, Game game) {

        String playername = player.getName();

        if (this.Owner == null) {
            System.out.println(playername + " has arrived in " + this.name + ". Would you like to buy the property " + this.street + "? (Y/N)");
            System.out.println("Proerty Price: Php" + this.price);
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine().trim().toUpperCase();

            if (this.Owner != player) {
                    System.out.println("This property has already been bought by " + this.Owner);
                    System.out.println("Rent Price: ₱" + this.rentprice);
                    player.updateCash(this.rentprice*-1);
                    System.out.println(playername + " remaining cash: Php" + player.getCash());   
            }
            else if (this.Owner == null){
                if (input.equals("Y")) {
                    if (player.getCash() >= price) {
                        player.updateCash(this.price*-1);
                        this.Owner = player;
                        player.updateOwnedProperties(this);
                        System.out.println(playername + " has bought " + this.street + " in " + this.name + "!");
                        System.out.println(playername + " remaining cash: Php" + player.getCash());
                        } else{
                            System.out.println(playername + "does not have enough cash!");
                        }
                } else if (input.equals("N")) {
                    System.out.println(playername + " has decided not to buy " + this.street + ".");
                 } else {
                System.out.println("Invalid input!");
                }
            }
            
                
            }

        }



    // Setters
    public void setOwner(Player owner) {
        this.Owner = owner;
    }

    // methods
    public boolean isOwned() {
        if (this.Owner != null) {
            return true;
        } else {
            return false;
        }
    }






}
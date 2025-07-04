import java.util.Scanner;


public class PropertyBlock extends Block{
    // Properties
    final public String street;
    final public double price;
    final public double rentprice;
    private Player Owner;


    // Constructors
    public PropertyBlock(String name, String street, int blockNumber, double price, double rentprice) {
        super(name, blockNumber);
        this.street = street;
        this.price = price;
        this.rentprice = rentprice;
        this.Owner = null; // instantiates the owner to null
    }

    /** 
     * @param player
     * @param game
     * @param scanner
     */
    @Override
    public void landedOn(Player player, Game game, Scanner scanner) {

        String playername = player.getName();

        if (this.Owner != null && this.Owner != player) {
            System.out.println(playername + " has arrived in " + this.name + ". This property is owned by " + this.Owner.getName() + ".");
            System.out.println("Rent Price: Php " + this.rentprice);
            player.updateCash(this.rentprice * -1);
            System.out.println(playername + " remaining cash: Php " + player.getCash());
            this.Owner.updateCash(this.rentprice);
            System.out.println(this.Owner.getName() + " has received rent of Php " + this.rentprice + " from " + playername);
            
        }

        else if (this.Owner == player) {
            System.out.println(playername + " has arrived in " + this.name + ". You already own this property, " + this.street + ".");
            System.out.println("You can collect rent from other players who land on this property.");
        }

        else if (this.Owner == null) {
            boolean flag;

            do {
                flag = false;
                System.out.println(playername + " has arrived in " + this.name + ". Would you like to buy the property " + this.street + "? (Y/N)");
                System.out.println("Property Price: Php " + this.price);
                String input;
                Scanner sc = new Scanner(System.in);
                input = sc.nextLine().trim().toUpperCase();
                System.out.println(playername + " entered: " + input);

                switch (input) {
                    case "Y":
                        if (player.getCash() >= price) {
                            player.updateCash(this.price * -1);
                            this.Owner = player;
                            player.updateOwnedProperties(this);
                            System.out.println(playername + " has bought " + this.street + " in " + this.name + "!");
                            System.out.println(playername + " remaining cash: Php" + player.getCash());
                        } else {
                            System.out.println(playername + " does not have enough cash!");
                        }
                        break;
                    case "N":
                        System.out.println(playername + " has decided not to buy " + this.street + ".");
                        break;
                    default:
                        System.out.println("Invalid input! Please try again.\n");
                        flag = true;
                        break;
                } 
            } while (flag);
        }
            
                
    }




    /** 
     * @param owner
     */
    public void setOwner(Player owner) {
        this.Owner = owner;
    }

    /** 
     * @return Player
     */
    public Player getOwner () {
        return this.Owner;
    }





}
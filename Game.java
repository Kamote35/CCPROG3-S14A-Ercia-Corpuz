import java.util.ArrayList;
import java.util.Random; 

public class Game {
    final public ArrayList<Player> players;
    final public ArrayList<Block> board;
    Random rand;

    // constructors
    public Game() {
        players = new ArrayList<>();
        board = new ArrayList<>();
        rand = new Random();
        initializeBoard();
    }

    public void initializeBoard() {
        
    }
}
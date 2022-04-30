import java.util.*;
import cards.*;

public class GameBoard {
    
    private Queue<Card> deck;
    private List<Card> discardPile;

    public GameBoard() {
        deck = new Deck();
        discardPile = new LinkedList<Card>();
    }
}

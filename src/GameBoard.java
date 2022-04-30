import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import cards.Card;

public class GameBoard {
    
    private Queue<Card> deck;
    private List<Card> discardPile;

    public GameBoard() {
        deck = new Deck();
        discardPile = new LinkedList<Card>();
    }
}

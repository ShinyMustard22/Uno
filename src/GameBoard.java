import java.util.*;
import cards.*;

public class GameBoard {
    
    private Queue<Card> deck;
    private Stack<Card> discardPile;

    public GameBoard() {
        deck = new Deck();
        discardPile = new Stack<Card>();
    }
}

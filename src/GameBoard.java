import java.util.Queue;
import cards.Card;

public class GameBoard {
    
    private Queue<Card> deck;

    public GameBoard() {
        deck = new Deck();
    }
}

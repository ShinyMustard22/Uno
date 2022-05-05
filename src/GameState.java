import java.util.*;
import cards.*;

public class GameState {
    
    private Queue<Card> deck;
    private Stack<Card> discardPile;
    private LinkedHashMap<String, Player> players;
    private boolean gameStarted;

    public GameState() {
        deck = new Deck();
        discardPile = new Stack<Card>();
        players = new LinkedHashMap<String, Player>();
        gameStarted = false;
    }

    public void addPlayer(String username) {
        if (gameStarted || username == null) {
            return;
        }
        
        List<Card> hand = new LinkedList<Card>();
        for (int count = 0; count < 7; count++) {
            hand.add(deck.remove());
        }

        players.put(username, new Player(username, hand));

        if (players.size() >= 10) {
            gameStarted = true;
        }
    }
}

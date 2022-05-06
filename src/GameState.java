import java.util.*;
import cards.*;

public class GameState {
    
    private Queue<Card> deck;
    private Stack<Card> discardPile;
    private LinkedList<Player> players;
    private boolean gameStarted;

    public GameState() {
        deck = new Deck();
        discardPile = new Stack<Card>();
        players = new LinkedList<Player>();
        gameStarted = false;
    }

    public Player addPlayer(String username) {
        if (gameStarted || username == null) {
            return null;
        }
        
        List<Card> hand = new LinkedList<Card>();
        for (int count = 0; count < 7; count++) {
            hand.add(deck.remove());
        }

        Player player = new Player(username, hand);
        players.add(player);

        if (players.size() >= 10) {
            gameStarted = true;
        }

        return player;
    }
}

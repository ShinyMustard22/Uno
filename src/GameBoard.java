import java.util.*;
import cards.*;

public class GameBoard {
    
    private Queue<Card> deck;
    private Stack<Card> discardPile;
    private ArrayList<Player> players;
    private boolean gameStarted;

    public GameBoard() {
        deck = new Deck();
        discardPile = new Stack<Card>();
        players = new ArrayList<Player>();
        gameStarted = false;
    }

    public void addPlayer(String username) {
        if (gameStarted) {
            return;
        }
        
        List<Card> hand = new LinkedList<Card>();
        for (int count = 0; count < 7; count++) {
            hand.add(deck.poll());
        }

        players.add(new Player(username, hand));

        if (players.size() >= 10) {
            gameStarted = true;
        }
    
    }

    // for (Player player : players){
    //     if (player.getDeck().size() <= 1){
    //         player.announceUno();
    //         endGame();
    //     }
    // }
    
    // public void endGame(){
    //     return;
    // }
}


import java.util.*;
import cards.*;
import cards.Card.Type;

public class GameState {
    
    private Queue<Card> deck;
    private Stack<Card> discardPile;
    private LinkedHashMap<String, Player> players;
    private Iterator<Player> turn;
    private Player currentPlayer;
    private boolean gameStarted;

    public GameState() {
        deck = new Deck();
        discardPile = new Stack<Card>();

        while (deck.peek().getType() == Type.wild) {
            deck.add(deck.remove());
        }
        discardPile.push(deck.remove());
        
        players = new LinkedHashMap<String, Player>();
        gameStarted = false;
    }

    public boolean addPlayer(String username) {  
        List<Card> hand = new LinkedList<Card>();
        for (int count = 0; count < Player.STARTING_HAND_SIZE; count++) {
            hand.add(deck.remove());
        }

        Player player = new Player(username, hand);
        players.put(username, player);

        if (players.size() >= 10) {
            startGame();
        }

        return true;
    }

    public Player getPlayer(String username) {
        return players.get(username);
    }

    public void removePlayer(String username) {
        players.remove(username);
    }

    public boolean startGame() {
        if (players.size() >= 2) {
            gameStarted = true;
            turn = players.values().iterator();
            currentPlayer = turn.next();
        }

        return gameStarted;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean gameHasStarted() {
        return gameStarted;
    }

    public String getPlayerList() {
        Iterator<String> iter = players.keySet().iterator();
        String playerList = "";
        while(iter.hasNext()) {
            playerList += iter.next();
            if (iter.hasNext()) {
                playerList += " ";
            }
        }
        return playerList;
    }

    public Card getLastPlayedCard() {
        return discardPile.peek();
    }

    public boolean play(String username, Card card) {
        return false;
    }
}

import java.util.*;
import cards.*;

public class GameState {
    
    private Queue<Card> deck;
    private Stack<Card> discardPile;
    private LinkedList<Player> players;
    private ListIterator<Player> turn;
    private Player currentPlayer;
    private boolean gameStarted;

    public GameState() {
        deck = new Deck();
        discardPile = new Stack<Card>();
        discardPile.push(deck.remove());
        players = new LinkedList<Player>();
        gameStarted = false;
    }

    public Player addPlayer(String username) {
        if (gameStarted || username == null) {
            return null;
        }
        
        List<Card> hand = new LinkedList<Card>();
        for (int count = 0; count < Player.STARTING_HAND_SIZE; count++) {
            hand.add(deck.remove());
        }

        Player player = new Player(username, hand);
        players.add(player);

        if (players.size() >= 10) {
            startGame();
        }

        return player;
    }

    public boolean startGame() {
        if (players.size() >= 2 && !gameStarted) {
            gameStarted = true;
            turn = players.listIterator();
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
        ListIterator<Player> iter = players.listIterator();
        String playerList = "-playerList:";
        while(iter.hasNext()) {
            playerList += " " + iter.next().getUsername();
        }
        return playerList;
    }

    public void play(String input) {
        // decode the message & update the game state
    }
}

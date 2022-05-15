import java.util.*;
import cards.*;
import cards.Card.Type;

public class GameState {
    
    private Queue<Card> deck;
    private Stack<Card> discardPile;
    private List<Player> players;
    private ListIterator<Player> turn;
    private Player currentPlayer;
    private boolean gameStarted;

    public GameState() {
        deck = new Deck();
        discardPile = new Stack<Card>();

        while (deck.peek().getType() == Type.wild) {
            deck.add(deck.remove());
        }
        discardPile.push(deck.remove());
        
        players = new LinkedList<Player>();
        gameStarted = false;
    }

    public boolean addPlayer(String username) {
        if (gameStarted) {
            return false;
        }  

        List<Card> hand = new LinkedList<Card>();
        for (int count = 0; count < Player.STARTING_HAND_SIZE; count++) {
            hand.add(deck.remove());
        }

        Player player = new Player(username, hand);
        players.add(player);

        return true;
    }

    public Player getPlayer(String username) {
        for (Player player : players) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }

        return null;
    }

    public void removePlayer(String username) {
        for (Player player : players) {
            if (player.getUsername().equals(username)) {
                players.remove(getPlayer(username));
                break;
            }
        }

        fixIterator();
    }

    private void fixIterator() {
        if (turn == null) {
            return;
        }

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (!player.equals(currentPlayer)) {
                players.add(players.remove(i));
                i--;
            }

            else {
                turn = players.listIterator();
                turn.next();
                return;
            }
        }
    }

    private void advanceTurn() {
        if (turn.hasNext()) {
            currentPlayer = turn.next();
        }

        else {
            turn = players.listIterator();
            currentPlayer = turn.next();
        }
    }

    public boolean startGame() {
        gameStarted = true;
        turn = players.listIterator();
        currentPlayer = turn.next();

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
        String playerList = "";
        while(iter.hasNext()) {
            playerList += iter.next().getUsername();
            if (iter.hasNext()) {
                playerList += " ";
            }
        }

        return playerList;
    }

    public Card getLastPlayedCard() {
        return discardPile.peek();
    }

    public boolean playable(String username, Card card) {
        return card.playable(discardPile.peek());
    }

    public boolean play(Card card, int index) {
        if (playable(currentPlayer.getUsername(), card))  {
            currentPlayer.play(index);
            discardPile.push(card);

            advanceTurn();
            return true;
        }

        return false;
    }

    public Card draw(String username) {
        List<Card> hand = getPlayer(username).getHand();
        for (Card card : hand) {
            if (playable(username, card)) {
                return null;
            }
        }

        Card card = deck.remove();
        getPlayer(username).addCard(card);
        advanceTurn();
        return card;
    }
}

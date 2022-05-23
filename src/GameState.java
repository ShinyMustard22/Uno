import java.util.*;
import cards.*;

public class GameState {
    private Queue<Card> deck;
    private Stack<Card> discardPile;

    private List<Player> players;
    private List<Player> wonPlayers;
    private List<Player> unoDangerPlayers;
    private ListIterator<Player> turn;

    private Player currentPlayer;
    private boolean gameStarted;
    private int currentPlace;

    public GameState() {
        deck = new Deck();
        discardPile = new Stack<Card>();

        while (deck.peek() instanceof WildCard) {
            deck.add(deck.remove());
        }
        discardPile.push(deck.remove());
        
        players = new LinkedList<Player>();
        wonPlayers = new LinkedList<Player>();
        unoDangerPlayers = new LinkedList<Player>();
        gameStarted = false;
        currentPlace = 0;
    }

    public boolean addPlayer(String username) {
        if (gameStarted || players.size() >= 10) {
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

    public void playerWon(String username) {
        currentPlace++;
        Player player = getPlayer(username);
        player.setPlace(currentPlace);
        wonPlayers.add(player);
        removePlayer(username);
    }

    public int getPlaceOfPlayer(String username) {
        for (Player player : wonPlayers) {
            if (player.getUsername().equals(username)) {
                return player.getPlace();
            }
        }

        return -1;
    }

    private void fixIterator() {
        if (turn == null || players.size() == 1) {
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

    private void reverseList() {
        List<Player> beforeList = new LinkedList<Player>();
        List<Player> afterList = new LinkedList<Player>();
        ListIterator<Player> iter = players.listIterator();
        int currentPlayerIndex = players.indexOf(currentPlayer);

        while (iter.hasNext()) {
            if (iter.nextIndex() < currentPlayerIndex) {
                beforeList.add(iter.next());
            }

            else if (iter.previousIndex() >= currentPlayerIndex) {
                afterList.add(iter.next());
            }

            else {
                iter.next();
            }
        }

        Collections.reverse(beforeList);
        Collections.reverse(afterList);

        players.clear();
        players.addAll(beforeList);
        players.addAll(afterList);
        players.add(currentPlayer);
    }

    public void advanceTurn(Card card) {
        if (currentPlayer.getHandSize() == 0 && players.size() == 2) {
            return;
        }

        if (card instanceof ReverseCard) {
            if (players.size() > 2) {
                reverseList();
                turn = players.listIterator();

                if (turn.hasNext()) {
                    currentPlayer = turn.next();
                }

                else {
                    turn = players.listIterator();
                    currentPlayer = turn.next();
                }
            }
        }

        else if (card instanceof SkipCard) {
            skipTurn();
        }

        else if (card instanceof DrawTwoCard) {
            Player nextPlayer;

            if (turn.hasNext()) {
                nextPlayer = players.get(turn.nextIndex());
            }

            else {
                turn = players.listIterator();
                nextPlayer = players.get(turn.nextIndex());
            }

            List<Card> newCards = drawFromDeck(2);
            nextPlayer.forcedDraw(newCards);
            ClientHandler.sendCards(nextPlayer.getUsername(), newCards);

            skipTurn();
        }

        else if (card instanceof DrawFourCard) {
            Player nextPlayer;

            if (turn.hasNext()) {
                nextPlayer = players.get(turn.nextIndex());
            }

            else {
                turn = players.listIterator();
                nextPlayer = players.get(turn.nextIndex());
            }

            List<Card> newCards = drawFromDeck(4);
            nextPlayer.forcedDraw(newCards);
            ClientHandler.sendCards(nextPlayer.getUsername(), newCards);

            skipTurn();
        }

        else {
            pass();
        }

    }

    private void skipTurn() {
        for (int i = 0; i < 2; i++) {
            pass();
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

    public boolean play(Card card, int index) {
        if (currentPlayer.play(discardPile.peek(), index)) {
            discardPile.push(card);

            if (currentPlayer.getHandSize() == 1) {
                unoDangerPlayers.add(currentPlayer);
            }

            advanceTurn(card);
            return true;
        }

        return false;
    }

    public Card draw() {
        if (currentPlayer.addCard(deck.peek(), discardPile.peek())) {
            if (deck.size() == 0) {
                restock();
            }

            Card card = deck.remove();

            if (!card.playable(discardPile.peek())) {
                pass();
            }
            
            return card;
        }

        return null;
    }

    private void restock() {
        deck.addAll(discardPile);
        discardPile.clear();
        
        while (deck.peek() instanceof WildCard) {
            deck.add(deck.remove());
        }
        discardPile.push(deck.remove());

        ClientHandler.newDiscardPile(discardPile.peek());
    }

    private List<Card> drawFromDeck(int numberOfCards) {
        List<Card> cardsToDraw = new LinkedList<Card>();
        for (int i = 0; i < numberOfCards; i++) {
            if (deck.isEmpty()) {
                restock();
            }

            cardsToDraw.add(deck.remove());
        }

        return cardsToDraw;
    }

    public int getTotalPlayers() {
        return players.size();
    }

    private void pass() {
        if (turn.hasNext()) {
            currentPlayer = turn.next();
        }

        else {
            turn = players.listIterator();
            currentPlayer = turn.next();
        }
    }

    public void saidUno(String username) {
        Player thisPlayer = getPlayer(username);
        for (Player player : unoDangerPlayers) {
            if (!player.equals(thisPlayer)) {
                List<Card> newCards = drawFromDeck(4);
                player.forcedDraw(newCards);
                ClientHandler.sendCards(player.getUsername(), newCards);
            }
        }

        unoDangerPlayers.clear();
    }

    public void endGame() {
        Player lastPlayer = players.get(0);
        currentPlace++;
        lastPlayer.setPlace(currentPlace);
        wonPlayers.add(lastPlayer);
    }
}
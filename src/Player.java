import java.util.List;
import cards.Card;

/**
 * A class the represents a player in the game.
 * 
 * @author Ritam Chakraborty
 * @version May 23, 2022
 */
public class Player {
    /**
     * The starting hand size of a player's hand in Uno.
     */
    public static final int STARTING_HAND_SIZE = 7;

    private String username;
    private int place;
    private List<Card> hand;

    /**
     * Constructs a player object with a specific username and starting hand
     * @param username username of player
     * @param hand starting hand of player
     */
    public Player(String username, List<Card> hand) {
        this.username = username;
        this.hand = hand;
    }

    /**
     * Adds a specified new card to the hand of the player if there are no
     * cards the player can play on top of the last card played
     * @param newCard new card to add to hand
     * @param lastCard last card that was played
     * @return true if card was added, false if not
     */
    public boolean addCard(Card newCard, Card lastCard) {
        for (Card card : hand) {
            if (card.playable(lastCard)) {
                return false;
            }
        }

        hand.add(newCard);
        return true;
    }

    /**
     * Forces the played to add a list of cards to their hand
     * @param newCards cards to add to the hand
     */
    public void forcedDraw(List<Card> newCards) {
        hand.addAll(newCards);
    }

    /**
     * Plays the card at the given index
     * @param lastCard
     * @param index
     * @return
     */
    public boolean play(Card lastCard, int index) {
        if (hand.get(index).playable(lastCard)) {
            hand.remove(index);
            return true;
        }

        return false;
    }

    /**
     * Gets the username of the player.
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }

    @Override
    /**
     * Overriden equals method from Object, returns true if players have the
     * same name, false is they don't.
     * @param other other player object
     * @return true if players have the same name, false otherwise
     */
    public boolean equals(Object other) {
        Player player = (Player) other;
        return player.username.equals(username);
    }

    /**
     * Gets the list of cards from this players hands in string format.
     * @return list of cards from hand as string
     */
    public String getCardList() {
        return Card.listToString(hand);
    }

    /**
     * Gets the size of the players hand.
     * @return size of the players hand
     */
    public int getHandSize() {
        return hand.size();
    }

    /**
     * Sets the place of the player.
     * @param place the place of the player
     */
    public void setPlace(int place) {
        this.place = place;
    }

    /**
     * Gets the "place" of the player in the standings.
     * @return the place of the player
     */
    public int getPlace() {
        return place;
    }
}
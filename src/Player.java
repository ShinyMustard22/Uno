import java.util.List;
import cards.Card;

public class Player {
    public static final int STARTING_HAND_SIZE = 7;

    private String username;
    private List<Card> hand;

    public Player(String username, List<Card> hand) {
        this.username = username;
        this.hand = hand;
    }

    public boolean addCard(Card newCard, Card lastCard) {
        for (Card card : hand) {
            if (card.playable(lastCard)) {
                return false;
            }
        }

        hand.add(newCard);
        return true;
    }

    public void forcedDraw(List<Card> newCards) {
        hand.addAll(newCards);
    }

    public boolean play(Card lastCard, int index) {
        if (hand.get(index).playable(lastCard)) {
            hand.remove(index);
            return true;
        }

        return false;
    }

    public String getUsername() {
        return username;
    }

    public List<Card> getHand() {
        return hand;
    }

    public boolean equals(Object other) {
        Player player = (Player) other;
        return player.username.equals(username);
    }

    public String getCardList() {
        return Card.listToString(hand);
    }
}

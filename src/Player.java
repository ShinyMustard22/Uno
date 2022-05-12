import java.util.List;
import java.util.ListIterator;
import cards.Card;

public class Player {
    public static final int STARTING_HAND_SIZE = 7;

    private String username;
    private List<Card> hand;
    private boolean canPlay = true;

    public Player(String username, List<Card> hand) {
        this.username = username;
        this.hand = hand;
    }

    public void play(Card card){
        hand.remove(card);
        canPlay = false;
    }

    public boolean canPlay(){
        return canPlay;
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
        String cardList = "";
        ListIterator<Card> iter = hand.listIterator();
        while (iter.hasNext()) {
            cardList += iter.next().toString();
            if (iter.hasNext()) {
                cardList += " ";
            }
        }

        return cardList;
    }
}

import java.util.List;
import cards.Card;

public class Player {

    private String username;
    private List<Card> hand;

    public Player(String username, List<Card> hand) {
        this.username = username;
        this.hand = hand;
    }

    public Card play(Card prev, int index){
        if (hand.get(index).playable(prev)){
            return hand.remove(index);
        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    public int getHandSize() {
        return hand.size();
    }

    public boolean equals(Object other) {
        Player player = (Player) other;
        return player.username.equals(username);
    }

}

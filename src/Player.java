import java.util.LinkedList;
import java.util.List;
import cards.Card;

public class Player {

    private String username;
    private List<Card> hand;

    public Player(String username, List<Card> hand) {
        this.username = username;
        this.hand = hand;
    }

}

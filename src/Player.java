import java.util.LinkedList;
import java.util.List;
import cards.Card;

public class Player {

    private String username;
    private List<Card> hand;
    private boolean isTurn;

    public Player(String username, List<Card> hand) {
        this.username = username;
        this.hand = hand;
    }

    public Card play(int ind){
        return hand.get(ind);
    }
    
    public List<Card> getDeck(){
        return hand;
    }

    public String announceUno(){
        return "Uno";
    }

}

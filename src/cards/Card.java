package cards;

public abstract class Card {

    public enum Type {
        number, drawFour, drawTwo, reverse, skip, wild
    }

    public String toString() {
        return "card";
    }

    public abstract boolean playable(Card card);
    
    public static Card decode(String cardAsString) {
        return null; // decode the string and turn into card
    }

    public abstract Type getType();
}

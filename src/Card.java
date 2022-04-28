public abstract class Card {
    private Object color;
    public Card(Object c){
        color = c;
    }

    
    public abstract String toString();
    
    public static Card decode(String cardAsString) {
        return null; // decode the string and turn into card
    }
}

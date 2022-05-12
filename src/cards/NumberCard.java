package cards;

public class NumberCard extends ColorCard {

    private int number;

    public NumberCard(String color, int n) {
        super(color);

        if (n < 0 || n > 9) {
            throw new IllegalArgumentException(
                "Number of the card must be between 0 and 9, but was " + n + ".");
        }
        number = n;
    }


    public boolean playable(Card card) {
        if (card instanceof NumberCard) {
            NumberCard numberCard = (NumberCard) card;
            return super.playable(card) || numberCard.number == number;
        }

        return super.playable(card);
    }


    public boolean equals(Object other) {
        if (!(other instanceof NumberCard)) {
            return false;
        }
        
        NumberCard otherNumberCard = (NumberCard) other;
        return super.equals(other) && otherNumberCard.number == number;
    }


    public String toString() {
        return super.toString() + "_" + number;
    }


    public int getNumber() {
        return number;
    }


    @Override
    public Type getType() {
        return Type.number;
    }
}

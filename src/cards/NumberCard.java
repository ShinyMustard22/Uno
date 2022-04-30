package cards;

public class NumberCard extends ColorCard {

    private int number;

    public NumberCard(String color, int n) {
        super(color);
        if (n < 0 || n > 9) {
            throw new IllegalArgumentException("Number of the card must be between 0 and 9, but was " + n + ".");
        }
        number = n;
    }

    public int getNumber() {
        return number;
    }

    public boolean equals(Object other) {
        NumberCard otherNumberCard = (NumberCard) other;
        return super.equals(other) && otherNumberCard.number == number;
    } 
    
    public String toString() {
        return super.toString() + "_" + number;
    }
}

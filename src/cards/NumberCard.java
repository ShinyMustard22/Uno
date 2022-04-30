package cards;

public class NumberCard extends ColorCard {

    private int number;

    public NumberCard(String color, int n) {
        super(color);
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

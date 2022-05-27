package cards;

/**
 * A class that represents a card in the game of Uno.
 * 
 * @author Ritam Chakraborty, Angela Chung
 * @version May 26, 2022
 */
public class NumberCard extends ColorCard {

    private int number;

    /**
     * Creates a NumberCard object
     * directly extends ColorCard but contains a new field for its number
     * @param color String color of Card
     * @param n int number of Card
     */
    public NumberCard(String color, int n) {
        super(color);

        if (n < 0 || n > 9) {
            throw new IllegalArgumentException(
                "Number of the card must be between 0 and 9, but was " + n + ".");
        }
        number = n;
    }

    /**
     * Determines if Card is playable based on previously played Card
     * Card is playable if color or number matches
     * @param card previous played Card
     * @return true if the card is playable, false otherwise
     */
    public boolean playable(Card card) {
        if (card instanceof NumberCard) {
            NumberCard numberCard = (NumberCard) card;
            return super.playable(card) || numberCard.number == number;
        }

        return super.playable(card);
    }

    /**
     * Determines if Card is equal to another Card
     * Cards are equal if their numbers and colors match
     * @param other Card
     * @return true if equals, false otherwise
     */
    public boolean equals(Object other) {
        if (!(other instanceof NumberCard)) {
            return false;
        }
        
        NumberCard otherNumberCard = (NumberCard) other;
        return super.equals(other) && otherNumberCard.number == number;
    }


    /**
     * Expresses NumberCard as a String
     * @return String representation of NumberCard
     */
    public String toString() {
        return super.toString() + "_" + number;
    }


    /**
     * Gets number of NumberCard for Client Classes
     * @return number of Cards
     */
    public int getNumber() {
        return number;
    }
}
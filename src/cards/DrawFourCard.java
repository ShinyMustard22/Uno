package cards;

/**
 * A class that represents a draw four card in the game of Uno.
 * 
 * @author Ritam Chakraborty, Angela Chung
 * @version May 26, 2022
 */
public class DrawFourCard extends WildCard {

    /**
     * Expresses DrawFour Card as a String
     * @return String expression of DrawFourCard
     */
    public String toString() {
        return super.toString() + "_" + "draw4";
    }
}
package cards;

/**
 * A Card that can be played at any time and sets color to Player's choice after played
 *
 * @author Angela Chung, Ritam Chakraborty
 * @version May 23, 2022
 */

public class WildCard extends Card {

    private String color;

    /**
     * Determines whether or not WildCard is playable based on previous Card
     * @param card previous played Card
     * @return true
     */
    public boolean playable(Card card) {
        return true;
    }

    /**
     * Sets Color of WildCard to Player's choice
     * @param color New color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets Color of WildCard
     * @return String color of WildCard
     */
    public String getColor() {
        return color;
    }

    /**
     * Expresses WildCard as a String
     * @return String representation of WildCard
     */
    public String toString() {
        return super.toString() + "_wild";
    }

    /**
     * Gets Type of WildCard for Client Classes
     * @return Type WildCard
     */
    @Override
    public Type getType() {
        return Type.wild;
    }
}
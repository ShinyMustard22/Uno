package cards;

/**
 * A concrete class that simulates a Card with a color in the form of a String
 * Contains method playable to determine playability of Card and equals to
 * determine whether two ColorCards are the same
 *
 * @author Angela chung, Ritam Chakraborty
 * @version May 23, 2022
 */
public abstract class ColorCard extends Card {

    public static final String RED = "red";
    public static final String BLUE = "blue";
    public static final String GREEN = "green";
    public static final String YELLOW = "yellow";
    
    private String color;

    /**
     * Creates a ColorCard object
     * Extends Card and overrides playable(Card c) and equals(Object o)
     * @param c String color of Card
     */
    public ColorCard(String c) {
        if (!c.equals(RED) && !c.equals(BLUE) && !c.equals(GREEN) && !c.equals(YELLOW)) {
            throw new IllegalArgumentException("Color must be blue, green, red or yellow, but was \"" + c + "\".");
        }
        color = c;
    }

    /**
     * Determines whether or not Card is playable based on previously played Card
     * @param card previous played Card
     * @return true if playable, false otherwise
     */
    public boolean playable(Card card) {
        if (card instanceof ColorCard) {
            ColorCard colorCard = (ColorCard) card;
            return colorCard.color.equals(color);
        }

        WildCard wildCard = (WildCard) card;
        return wildCard.getColor().equals(color);
    }

    /**
     * Accesses Color of Card from Client Class
     * @return String color of Card
     */
    public String getColor() {
        return color;
    }

    /**
     * Compares Card with another Card to determine if they are "equal"
     * Cards are equal if their Colrs match
     * @param other Card
     * @return true if equal, false otherwise
     */
    public boolean equals(Object other) {
        if (!(other instanceof ColorCard)) {
            return false;
        }
        
        ColorCard otherColorCard = (ColorCard) other;
        return color.equals(otherColorCard.color);
    }

    /**
     * Expresses Card as a String
     * @return String representation of Card
     */
    public String toString() {
        return super.toString() + "_" + color;
    }
}
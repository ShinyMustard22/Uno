package cards;

/**
 * A class that represents a card in the game of Uno.
 * 
 * @author Ritam Chakraborty, Angela Chung
 * @version May 26, 2022
 */
public class DrawTwoCard extends SpecialColorCard {

    static final String EFFECT = "draw2";

    /**
     * constructs a drawTwo Card
     * Forces next Player to draw two cards
     * @param c String effect
     */
    public DrawTwoCard(String c) {
        super(c, EFFECT);
    }
}
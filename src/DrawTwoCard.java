/**
 * A card that forces the next player to draw two cards and skips their turn
 * Playable if color or effect matches previous card
 *
 * @author Angela Chung, Ritam Chakraborty
 * @version May 23, 2022
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
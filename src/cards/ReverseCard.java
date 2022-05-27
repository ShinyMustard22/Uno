package cards;
/**
 * A Card that reverses the current order in which Players play
 * Playable if color or effect matches previous Card
 *
 * @author Angela Chung, Ritam Chakraborty
 * @version May 24, 2022
 */
public class ReverseCard extends SpecialColorCard {

    static final String EFFECT = "reverse";

    /**
     * Creates a ReverseCard Object
     * ReverseCard forces the current rotation of Players to reverse
     * @param c String representation of effect
     */
    public ReverseCard(String c) {
        super(c, EFFECT);
    }
}
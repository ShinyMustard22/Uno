package cards;

/**
 * A Card that forces the next Player to skip their turn
 * Playable when color or effect matches previous card
 *
 * @author Angela Chung, Ritam Chakraborty
 * @version May 23, 2022
 */

public class SkipCard extends SpecialColorCard {

    static final String EFFECT = "skip";

    /**
     * Creates a SkipCard Object
     * SkipCard forces the nextPlayer to skip a Turn
     * @param c
     */
    public SkipCard(String c){
        super(c, EFFECT);
    }

    /**
     * Gets Type of SkipCard
     * @return Type of SkipCard
     */
    @Override
    public Type getType () {
        return Type.skip;
    }
}
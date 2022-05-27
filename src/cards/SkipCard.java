package cards;

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
}
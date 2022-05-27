package cards;
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

    /**
     * Gets type of ReverseCard for Client Classes
     * @return Type of ReverseCard
     */
    @Override
    public Type getType() {
        return Type.reverse;
    }
}
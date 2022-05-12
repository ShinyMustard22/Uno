package cards;
public class ReverseCard extends SpecialColorCard {

    static final String EFFECT = "reverse";

    public ReverseCard(String c) {
        super(c, EFFECT);
    }

    @Override
    public Type getType() {
        return Type.reverse;
    }
}

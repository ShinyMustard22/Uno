package cards;

public class DrawTwoCard extends SpecialColorCard {

    static final String EFFECT = "draw2";

    public DrawTwoCard(String c) {
        super(c, EFFECT);
    }

    @Override
    public Type getType() {
        return Type.drawTwo;
    }
}

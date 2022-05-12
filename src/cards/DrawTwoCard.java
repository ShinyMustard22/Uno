package cards;

public class DrawTwoCard extends SpecialColorCard {

    static final String EFFECT = "draw2";

    @Override
    public Type getType() {
        return Type.drawTwo;
    }

    public DrawTwoCard(String c) {
        super(c, EFFECT);
    }
}

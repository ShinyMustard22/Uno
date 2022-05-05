package cards;

public class DrawTwo extends SpecialColorCard {

    static final String EFFECT = "draw2";

    @Override
    public Type getType(){
        return Type.drawTwo;
    }

    public DrawTwo(String c) {
        super(c, EFFECT);
    }
}

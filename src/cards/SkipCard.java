package cards;

public class SkipCard extends SpecialColorCard {

    static final String EFFECT = "skip";

    public SkipCard(String c){
        super(c, EFFECT);
    }

    @Override
    public Type getType () {
        return Type.skip;
    }
}
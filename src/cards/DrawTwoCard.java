package cards;

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

    /**
     * Gets type of DrawTwo Card for Client Classes
     * @return Type of drawTwo
     */
    @Override
    public Type getType() {
        return Type.drawTwo;
    }
}
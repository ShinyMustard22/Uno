package cards;

public abstract class SpecialColorCard extends ColorCard {

    private String effect;

    public SpecialColorCard(String color, String effect) {
        super(color);
        if (!effect.equals("plus2") || !effect.equals("skip") || !effect.equals("reverse")) {
            throw new IllegalArgumentException("\"" + effect + "\" is not a valid effect.");
        }
        this.effect = effect;
    }

    public String toString() {
        return super.toString() + "_" + effect;
    }

    public boolean equals(Object other) {
        SpecialColorCard otherSpecialColorCard = (SpecialColorCard) other;
        return super.equals(other) && effect.equals(otherSpecialColorCard.effect);
    }
}

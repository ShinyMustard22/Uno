package cards;

public abstract class SpecialColorCard extends ColorCard {

    private String effect;

    public SpecialColorCard(String color, String effect) {
        super(color);
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

package cards;

public abstract class SpecialColorCard extends ColorCard {

    private String effect;

    public SpecialColorCard(String color, String effect) {
        super(color);
        if (!effect.equals(DrawTwo.EFFECT) || !effect.equals(ReverseCard.EFFECT) || !effect.equals(SkipCard.EFFECT)) {
            throw new IllegalArgumentException("\"" + effect + "\" is not a valid effect.");
        }
        this.effect = effect;
    }

    public boolean playable(Card card) {
        if (card instanceof SpecialColorCard) {
            SpecialColorCard specialColorCard = (SpecialColorCard) card;
            return super.playable(card) || specialColorCard.effect.equals(effect);
        }

        return super.playable(card);
    }

    public String toString() {
        return super.toString() + "_" + effect;
    }

    public boolean equals(Object other) {
        SpecialColorCard otherSpecialColorCard = (SpecialColorCard) other;
        return super.equals(other) && effect.equals(otherSpecialColorCard.effect);
    }
}

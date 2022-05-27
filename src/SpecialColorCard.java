/**
 * A class that extends ColorCard with an additional field for the effect
 * in order to simulate Cards that have special effects when played
 * Overrides playable and equals to accomodate for said effect
 *
 * @author Angela Chung, Ritam Chakraborty
 * @version May 23, 2022
 */

public abstract class SpecialColorCard extends ColorCard {

    private String effect;

    /**
     * Creates a SpecialColorCard
     * SpecialColorCard directly extends ColorCard
     * Used to simulate Cards with special effects
     * @param color
     * @param effect
     * @throws IllegalArgumentException when the effect is invlaid
     */
    public SpecialColorCard(String color, String effect) {
        super(color);
        if (!effect.equals(DrawTwoCard.EFFECT) && !effect.equals(ReverseCard.EFFECT) && !effect.equals(SkipCard.EFFECT)) {
            throw new IllegalArgumentException("\"" + effect + "\" is not a valid effect.");
        }
        this.effect = effect;
    }

    /**
     * Determines if SpecialColorCard is playable based on previously played Card
     * @param card previous played Card
     * @return true if playable, false otherwise
     */
    public boolean playable(Card card) {
        if (card instanceof SpecialColorCard) {
            SpecialColorCard specialColorCard = (SpecialColorCard) card;
            return super.playable(card) || specialColorCard.effect.equals(effect);
        }

        return super.playable(card);
    }

    /**
     * Expresses SpecialColorCard as a String
     * @return String representation of SpecialColorCard
     */
    public String toString() {
        return super.toString() + "_" + effect;
    }

    /**
     * Determines if SpecialColorCard is equal to another Card
     * Returns true  if color and effect are equal
     * @param other Card
     * @return
     */
    public boolean equals(Object other) {
        if (!(other instanceof SpecialColorCard)) {
            return false;
        }
        
        SpecialColorCard otherSpecialColorCard = (SpecialColorCard) other;
        return super.equals(other) && effect.equals(otherSpecialColorCard.effect);
    }
}
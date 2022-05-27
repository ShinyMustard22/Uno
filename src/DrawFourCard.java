/**
 * A Card that forces the next Player to draw four Cards when played and skips their turn
 * Playable regardless of previous Card
 * Sets color after played
 *
 * @author Angela Chung, Ritam Chakraborty
 * @version May 23, 2022
 */
public class DrawFourCard extends WildCard {

    /**
     * Expresses DrawFour Card as a String
     * @return String expression of DrawFourCard
     */
    public String toString() {
        return super.toString() + "_" + "draw4";
    }
}
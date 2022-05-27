package cards;

import java.util.List;
import java.util.ListIterator;

/**
 * A Card class that simulates the very basic form of a Card
 * Contains abstract methods such as playable and equals that will be used / overriden in all Child Classes
 * Collection of types corresponding to all possible Cards
 *
 * @author Angela Chung, Ritam Chakraborty
 * @version May 23, 2022
 */

public abstract class Card {

    public enum Type {
        number, drawFour, drawTwo, reverse, skip, wild
    }

    /**
     * Expresses the Card as a string
     * @return expression of Card as a String
     */
    public String toString() {
        return "card";
    }

    /**
     * abstract Class playable - determines if Card is playble based on previously played Card
     * @param card previous played Card
     * @return true if playable, false otherwise
     */
    public abstract boolean playable(Card card);

    /**
     * prevents bugs during running
     * @param cardAsString String expression of previous Card
     * @return Card if bug occurs
     */
    public static Card decode(String cardAsString) {
        if (cardAsString.contains(ColorCard.RED)) {
            if (cardAsString.contains(SkipCard.EFFECT)) {
                return new SkipCard(ColorCard.RED);
            }

            else if (cardAsString.contains(DrawTwoCard.EFFECT)) {
                return new DrawTwoCard(ColorCard.RED);
            }

            else if (cardAsString.contains(ReverseCard.EFFECT)) {
                return new ReverseCard(ColorCard.RED);
            }

            else {
                int num = Integer.valueOf(cardAsString.substring(cardAsString.length() - 1));
                return new NumberCard(ColorCard.RED, num);
            }
        }

        else if (cardAsString.contains(ColorCard.BLUE)) {
            if (cardAsString.contains(SkipCard.EFFECT)) {
                return new SkipCard(ColorCard.BLUE);
            }

            else if (cardAsString.contains(DrawTwoCard.EFFECT)) {
                return new DrawTwoCard(ColorCard.BLUE);
            }

            else if (cardAsString.contains(ReverseCard.EFFECT)) {
                return new ReverseCard(ColorCard.BLUE);
            }

            else {
                int num = Integer.valueOf(cardAsString.substring(cardAsString.length() - 1));
                return new NumberCard(ColorCard.BLUE, num);
            }
        }

        else if (cardAsString.contains(ColorCard.GREEN)) {
            if (cardAsString.contains(SkipCard.EFFECT)) {
                return new SkipCard(ColorCard.GREEN);
            }

            else if (cardAsString.contains(DrawTwoCard.EFFECT)) {
                return new DrawTwoCard(ColorCard.GREEN);
            }

            else if (cardAsString.contains(ReverseCard.EFFECT)) {
                return new ReverseCard(ColorCard.GREEN);
            }

            else {
                int num = Integer.valueOf(cardAsString.substring(cardAsString.length() - 1));
                return new NumberCard(ColorCard.GREEN, num);
            }
        }

        if (cardAsString.contains(ColorCard.YELLOW)) {
            if (cardAsString.contains(SkipCard.EFFECT)) {
                return new SkipCard(ColorCard.YELLOW);
            }

            else if (cardAsString.contains(DrawTwoCard.EFFECT)) {
                return new DrawTwoCard(ColorCard.YELLOW);
            }

            else if (cardAsString.contains(ReverseCard.EFFECT)) {
                return new ReverseCard(ColorCard.YELLOW);
            }

            else {
                int num = Integer.valueOf(cardAsString.substring(cardAsString.length() - 1));
                return new NumberCard(ColorCard.YELLOW, num);
            }
        }

        WildCard wild = new WildCard();
        DrawFourCard draw4 = new DrawFourCard();

        if (cardAsString.equals(wild.toString())) {
            return wild;
        }

        else if (cardAsString.equals(draw4.toString())) {
            return draw4;
        }

        return null;
    }

    /**
     * Expresses a list of Cards as a String - calls each Card's toString() method to format them all
     * @param cards list of Cards
     * @return String representation of list of Cards
     */
    public static String listToString(List<Card> cards) {
        StringBuffer strList = new StringBuffer();

        ListIterator<Card> iter = cards.listIterator();
        while (iter.hasNext()) {
            strList.append(iter.next());
            if (iter.hasNext()) {
                strList.append(" ");
            }
        }

        return strList.toString();
    }

    /**
     * Gets type of Card for Client Classes
     * @return type of Card
     */
    public abstract Type getType();
}
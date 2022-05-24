package cards;

import java.util.List;
import java.util.ListIterator;

public abstract class Card {

    public enum Type {
        number, drawFour, drawTwo, reverse, skip, wild
    }

    public String toString() {
        return "card";
    }

    public abstract boolean playable(Card card);
    
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

    public abstract Type getType();
}
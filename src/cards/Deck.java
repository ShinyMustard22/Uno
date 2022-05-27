package cards;
import java.util.*;

/**
 * A deck of cards that extends LinkedList and is intended to be
 * implemented as queue which returns cards in a random order,
 * giving the illusion that the deck is shuffled.
 * 
 * @author Ritam Chakraborty
 * @version May 23, 2022
 */
public class Deck extends LinkedList<Card> {

    private int nextCardPos;
    
    /**
     * Adds all the cards in a standard deck of Uno to this list
     */
    public Deck() {
        // Creating the "0" number cards
        addLast(new NumberCard(ColorCard.RED, 0));
        addLast(new NumberCard(ColorCard.BLUE, 0));
        addLast(new NumberCard(ColorCard.GREEN, 0));
        addLast(new NumberCard(ColorCard.YELLOW, 0));

        // Creating the "1" through "9" number cards
        for (int num = 1; num <= 9; num++) {
            for (int count = 0; count < 2; count ++) {
                addLast(new NumberCard(ColorCard.RED, num));
                addLast(new NumberCard(ColorCard.BLUE, num));
                addLast(new NumberCard(ColorCard.GREEN, num));
                addLast(new NumberCard(ColorCard.YELLOW, num));
            }
        }

        // Creating the "Plus 2", "Reverse", and "Skip" cards
        for (int count = 0; count < 2; count ++) {
            addLast(new DrawTwoCard(ColorCard.RED));
            addLast(new DrawTwoCard(ColorCard.BLUE));
            addLast(new DrawTwoCard(ColorCard.GREEN));
            addLast(new DrawTwoCard(ColorCard.YELLOW));

            addLast(new ReverseCard(ColorCard.RED));
            addLast(new ReverseCard(ColorCard.BLUE));
            addLast(new ReverseCard(ColorCard.GREEN));
            addLast(new ReverseCard(ColorCard.YELLOW));

            addLast(new SkipCard(ColorCard.RED));
            addLast(new SkipCard(ColorCard.BLUE));
            addLast(new SkipCard(ColorCard.GREEN));
            addLast(new SkipCard(ColorCard.YELLOW));
        }

        // Creating the "Wild Cards" and the "Plus 4" cards
        for (int count = 0; count < 4; count ++) {
            addLast(new WildCard());
            addLast(new DrawFourCard());
        }

        nextCardPos = (int) (Math.random() * size());
    }

    @Override
    /**
     * Removes a random card from the list.
     * @throws NoSuchElementException is list is empty
     * @return random card from the list
     */
    public Card remove() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        return poll();
    }

    @Override
    /**
     * Removes a random card from the list.
     * @return random card from the list, if list is empty, it returns null
     */
    public Card poll() {
        if (isEmpty()) {
            return null;
        }

        Card shuffledCard = super.remove(nextCardPos);
        nextCardPos = (int) (Math.random() * size());
        return shuffledCard;
    }

    @Override
    /**
     * Returns the top of the deck.
     * @throws NoSuchElementException is list is empty
     * @return the top of the deck
     */
    public Card element() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        return peek();
    }

    /**
     * Returns the top of the deck.
     * @return the top of the deck, if list is empty, it returns null
     */
    @Override
    public Card peek() {
        if (isEmpty()) {
            return null;
        }

        Card shuffledCard = super.get(nextCardPos);
        return shuffledCard;
    }
}
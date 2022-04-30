import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import cards.*;

public class Deck implements Queue<Card> {

    private int nextCard;
    
    public Deck(){
        // Creating the "0" number cards
        add(new NumberCard(ColorCard.RED, 0));
        add(new NumberCard(ColorCard.BLUE, 0));
        add(new NumberCard(ColorCard.GREEN, 0));
        add(new NumberCard(ColorCard.YELLOW, 0));

        // Creating the "1" through "9" number cards
        for (int num = 1; num <= 9; num++) {
            for (int count = 0; count < 2; count ++) {
                add(new NumberCard(ColorCard.RED, num));
                add(new NumberCard(ColorCard.BLUE, num));
                add(new NumberCard(ColorCard.GREEN, num));
                add(new NumberCard(ColorCard.YELLOW, num));
            }
        }

        // Creating the "Plus 2", "Reverse", and "Skip" cards
        for (int count = 0; count < 2; count ++) {
            add(new PlusTwoCard(ColorCard.RED));
            add(new PlusTwoCard(ColorCard.BLUE));
            add(new PlusTwoCard(ColorCard.GREEN));
            add(new PlusTwoCard(ColorCard.YELLOW));

            add(new ReverseCard(ColorCard.RED));
            add(new ReverseCard(ColorCard.BLUE));
            add(new ReverseCard(ColorCard.GREEN));
            add(new ReverseCard(ColorCard.YELLOW));

            add(new SkipCard(ColorCard.RED));
            add(new SkipCard(ColorCard.BLUE));
            add(new SkipCard(ColorCard.GREEN));
            add(new SkipCard(ColorCard.YELLOW));
        }

        // Creating the "Wild Cards" and the "Plus 4" cards
        for (int count = 0; count < 4; count ++) {
            add(new WildCard());
            add(new PlusFourCard());
        }

        nextCard = (int) (Math.random() * size());
    }

    @Override
    public int size()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(Object o)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<Card> iterator()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[] toArray()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean remove(Object o)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Card> c)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean add(Card e)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean offer(Card e)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Card remove()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Card poll()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Card element()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Card peek()
    {
        // TODO Auto-generated method stub
        return null;
    }
}

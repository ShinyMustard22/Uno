import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import cards.*;

public class Deck implements Queue<Card> {

    private int nextCard;
    
    public Deck(){
        // Creating the "0" number cards
        add(new NumberCard("red", 0));
        add(new NumberCard("blue", 0));
        add(new NumberCard("green", 0));
        add(new NumberCard("yellow", 0));

        // Creating the "1" through "9" number cards
        for (int num = 1; num <= 9; num++) {
            for (int count = 0; count < 2; count ++) {
                add(new NumberCard("red", num));
                add(new NumberCard("blue", num));
                add(new NumberCard("green", num));
                add(new NumberCard("yellow", num));
            }
        }

        // Creating the "Plus 2", "Reverse", and "Skip" cards
        for (int count = 0; count < 2; count ++) {
            add(new PlusTwoCard("red"));
            add(new PlusTwoCard("blue"));
            add(new PlusTwoCard("green"));
            add(new PlusTwoCard("yellow"));

            add(new ReverseCard("red"));
            add(new ReverseCard("blue"));
            add(new ReverseCard("green"));
            add(new ReverseCard("yellow"));

            add(new SkipCard("red"));
            add(new SkipCard("blue"));
            add(new SkipCard("green"));
            add(new SkipCard("yellow"));
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

import java.util.ArrayList;

public class DeckOfCards
{
    private ArrayList<Card> deck = new ArrayList<Card>();
    public DeckOfCards(){
        for (int i = 1; i <= 9; i ++ ){
            for (int a = 0; a < 3; a ++ ){
                for (int r = 0; r < 2; r ++ ){
                    NumberCard card = new NumberCard(i, a);
                    deck.add(card);
                }
                
            }
        }

        for (int i = 0; i < 3; i ++ ){
            for (int a = 0; a < 2; a ++ ){
                NumberCard zeroCard = new NumberCard(0, i);
                deck.add(zeroCard);
            }
        }

        for (int i = 0; i < 3; i ++ ){
            for (int a = 0; a < 2; a ++ ){
                ReverseCard revCard = new ReverseCard(i);
                StopCard stopCard = new StopCard(i);
                PlusCard p2card = new PlusCard(i, 2);
                deck.add(revCard); 
                deck.add(stopCard);
                deck.add(p2card);
            }
        }

        for (int i = 0; i < 3; i ++ ){
            WildCard wild = new WildCard();
            PlusCard p4card = new PlusCard(null, 2);
            deck.add(wild);
            deck.add(p4card);
        }
    }

    public void shuffle(){
        //ur mom <;D
        ArrayList<Integer> shuffler = new ArrayList<Integer>();
        for (int i = 0 ; i < 108; i ++ ){
            shuffler.add(i);
        }

        for (int a = 0; a < 108/2; a ++){
            int ind1 = (int)(Math.random()*108);
            int ind2 = (int)(Math.random()*108);
            Card c1 = deck.get(ind1);
            deck.set(ind1, deck.get(ind2));
            deck.set(ind2, c1);
        }
    }

    public void display(){
        for (Card c : deck){
            System.out.print(c.toString() + " ");
        }
    }
}

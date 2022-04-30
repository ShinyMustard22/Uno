import java.util.ArrayList;
import cards.*;

public class Deck extends ArrayList<Card> {
    
    public Deck(){
        for (int i = 1; i <= 9; i ++ ){
            for (int a = 0; a < 3; a ++ ){
                for (int r = 0; r < 2; r ++ ){
                    NumberCard card = new NumberCard(i, a);
                    add(card);
                }
                
            }
        }

        for (int i = 0; i < 3; i ++ ){
            for (int a = 0; a < 2; a ++ ){
                NumberCard zeroCard = new NumberCard(0, i);
                add(zeroCard);
            }
        }

        for (int i = 0; i < 3; i ++ ){
            for (int a = 0; a < 2; a ++ ){
                ReverseCard revCard = new ReverseCard(i);
                SkipCard stopCard = new SkipCard(i);
                PlusTwoCard p2card = new PlusTwoCard(i, 2);
                add(revCard); 
                add(stopCard);
                add(p2card);
            }
        }

        for (int i = 0; i < 3; i ++ ){
            WildCard wild = new WildCard();
            PlusTwoCard p4card = new PlusTwoCard(null, 2);
            add(wild);
            add(p4card);
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
            Card c1 = get(ind1);
            set(ind1, get(ind2));
            set(ind2, c1);
        }
    }
}

public class NumberCard extends ColorCard {
    private int num;
    private String color;
    public NumberCard(int n, int c){
        super(c);
        //color = c;
        num = n;
    }

    public int getNum(){
        return num;
    }

    public String getColor(){
        return color;
    }

    public boolean isEqual (NumberCard other){
        return (other.getColor().equals(color) && other.getNum() == num);
    } 
    
    public String toString(){
        return num + color;
    }
}

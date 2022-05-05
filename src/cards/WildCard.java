package cards;

public class WildCard extends Card {

    private String color;

    public boolean playable(Card card) {
        return true;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public String toString() {
        return super.toString() + ":wild";
    }
    
    @Override
    public Type getType(){
        return Type.wild;
    }
}

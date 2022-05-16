package cards;

public abstract class ColorCard extends Card {

    public static final String RED = "red";
    public static final String BLUE = "blue";
    public static final String GREEN = "green";
    public static final String YELLOW = "yellow";
    
    private String color;

    public ColorCard(String c) {
        if (!c.equals(RED) && !c.equals(BLUE) && !c.equals(GREEN) && !c.equals(YELLOW)) {
            throw new IllegalArgumentException("Color must be blue, green, red or yellow, but was \"" + c + "\".");
        }
        color = c;
    }

    public boolean playable(Card card) {
        if (card instanceof ColorCard) {
            ColorCard colorCard = (ColorCard) card;
            return colorCard.color.equals(color);
        }

        WildCard wildCard = (WildCard) card;
        return false; //TODO implement wild card color selection
    }

    public boolean equals(Object other) {
        if (!(other instanceof ColorCard)) {
            return false;
        }
        
        ColorCard otherColorCard = (ColorCard) other;
        return color.equals(otherColorCard.color);
    }

    public String toString() {
        return super.toString() + "_" + color;
    }
}

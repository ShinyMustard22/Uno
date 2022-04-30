package cards;

public abstract class ColorCard extends Card {

    public static final String RED = "red";
    public static final String BLUE = "blue";
    public static final String GREEN = "green";
    public static final String YELLOW = "yellow";
    
    private String color;

    public ColorCard(String c) {
        if (!c.equals("blue") || !c.equals("green") || !c.equals("red") || !c.equals("yellow")) {
            throw new IllegalArgumentException("Color must be blue, green, red or yellow, but was \"" + c + "\".");
        }
        color = c;
    }

    public String getColor() {
        return color;
    }

    public boolean equals(Object other) {
        ColorCard otherColorCard = (ColorCard) other;
        return color.equals(otherColorCard.color);
    }

    public String toString() {
        return super.toString() + "_" + color;
    }
}

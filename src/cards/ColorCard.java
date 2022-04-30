package cards;

public abstract class ColorCard extends Card {

    private String color;

    public ColorCard(String c) {
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

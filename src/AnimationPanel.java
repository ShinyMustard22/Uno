import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AnimationPanel extends JPanel  {
    private BufferedImage bg;
    private int yOffset = 0;
    private int xOffset = 0;
    private int yDelta = 4;
    private int xDelta = 4;

    public AnimationPanel(LayoutManager layout) {
        super(layout);

    }

    private int fromX, fromY, toX, toY;
    private boolean movingCard = false;

    public boolean isMovingCard() {
        return movingCard;
    }

    public void moveCard(String cardImage, int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        yOffset = fromY;
        xOffset = fromX;
        yDelta = (toY - fromY) / 10;
        xDelta = (toX - fromX) / 10;
        final ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yOffset += yDelta;
                xOffset += xDelta;
                boolean xLimit = false;
                boolean yLimit = false;
                if (yDelta > 0 && yOffset > toY || yDelta < 0 && yOffset < toY) {
                    //  yOffset = 0;
                    //  movingCard = false;
                    yLimit = true;
                    yDelta = 0;
                    yOffset = toY;
                }
                if (xDelta > 0 && xOffset > toX || xDelta < 0 && xOffset < toX) {
                    //  xOffset = 0;
                    //  movingCard = false;
                    xLimit = true;
                    xDelta = 0;
                    xOffset = toX;
                }
                if (yLimit && xLimit) {
                    movingCard = false;

                }
                repaint();
            }
        };
        Timer timer = new Timer(20, listener);
        timer.start();
        try {
            bg = ImageIO.read(getClass().getResource("/assets/images/" + cardImage + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        movingCard = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (movingCard) {
            Graphics2D g2d = (Graphics2D) g.create();
            int xPos = xOffset;
            int yPos = yOffset;

            g2d.drawImage(bg, xPos, yPos, this);

            g2d.dispose();
        }

//        while (yPos < getHeight() && xPos < getWidth()) {
//            xPos += yDelta;
//            yPos += yDelta;
//            g2d.drawImage(bg, xPos, yPos, this);
//
//            g2d.dispose();
//        }
    }
}
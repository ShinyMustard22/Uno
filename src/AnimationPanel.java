import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class AnimationPanel extends JPanel {
    private BufferedImage bg;
    private int yOffset = 0;
    private int xOffset = 0;
    private int yStep = 4;
    private int xStep = 4;
    private boolean movingCard = false;

    public AnimationPanel(LayoutManager layout) {
        super(layout);
    }

    public void moveCard(String cardImage, int fromX, int fromY, int toX, int toY) {
        try {
            bg = ImageIO.read(getClass().getResource("/assets/images/" + cardImage + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Point> fromList = new ArrayList<>();
        ArrayList<Point> toList = new ArrayList<>();
        fromList.add(new Point(fromX, fromY));
        toList.add(new Point(toX, toY));
        moveCard(cardImage, fromList, toList, null, 10, 10);
    }

    /**
     * moving image cardImage around to generate animation
     *
     * @param cardImage:  image
     * @param fromList:   list of from positions
     * @param toList:     list of to positions
     * @param components: mmake visible after animation
     */
    public void moveCard(String cardImage, ArrayList<Point> fromList, ArrayList<Point> toList,
                         java.util.List<Component> components,
                         int stepInterval, int totalStep) {
        if (null == fromList || null == toList || fromList.size() != toList.size()) {
            throw new IllegalArgumentException("Illegal fromList or toList");
        }

        try {
            bg = ImageIO.read(getClass().getResource("/assets/images/" + cardImage + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AnimationActionListener actionListener =
                new AnimationActionListener(fromList, toList, components, totalStep);
        Timer timer = new Timer(stepInterval, actionListener);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (movingCard && bg != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.drawImage(bg, xOffset, yOffset, this);
            g2d.dispose();
        }

    }

    public class AnimationActionListener implements ActionListener {
        private final ArrayList<Point> fromList;
        private final ArrayList<Point> toList;
        private java.util.List<Component> components;

        private int finishedRound = 0;
        private boolean picked = false;
        private int totalStep = 10;
        private int total = 0;
        private int fromX;
        private int fromY;
        private int toX;
        private int toY;

        public AnimationActionListener(ArrayList<Point> fromList, ArrayList<Point> toList, java.util.List<Component> components,
                                       int totalStep) {
            this.fromList = fromList;
            this.toList = toList;
            this.components = components;
            this.totalStep = totalStep;
            this.fromX = fromList.get(finishedRound).x;
            this.fromY = fromList.get(finishedRound).y;
            this.toX = toList.get(finishedRound).x;
            this.toY = toList.get(finishedRound).y;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (finishedRound == fromList.size()) {
                if (components != null) {
                    for (Component component : components) {
                        component.setVisible(true);
                    }
                    if (e.getSource() instanceof Timer) {
                        ((Timer) e.getSource()).stop();
                    }
                }
                return;
            }
            if (!picked) {
                yOffset = fromY;
                xOffset = fromX;
                yStep = (toY - fromY) / totalStep;
                xStep = (toX - fromX) / totalStep;
                picked = true;
                movingCard = true;
            }
            yOffset += yStep;
            xOffset += xStep;
            boolean xLimit = false;
            boolean yLimit = false;
            if (yStep > 0 && yOffset >= toY || yStep < 0 && yOffset <= toY) {
                yLimit = true;
                yStep = 0;
                yOffset = toY;
            }
            if (xStep > 0 && xOffset >= toX || xStep < 0 && xOffset <= toX) {
                xLimit = true;
                xStep = 0;
                xOffset = toX;
            }
            if ((yLimit && xLimit) || total == totalStep) {
                movingCard = false;
                if (components != null && finishedRound < components.size()) {
                    components.get(finishedRound).setVisible(true);
                }
                finishedRound++;
                total = 0;
                picked = false;
            }
            total++;
            repaint();

        }
    }
}

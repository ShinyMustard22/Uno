import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JPanel with support of animation of moving specified image from
 * one point to another
 *
 * @author Angela Chung
 * @version May 23, 2022
 */

public class AnimationPanel extends JPanel {
    private BufferedImage bg;
    private int yOffset = 0;
    private int xOffset = 0;
    private int yStep = 4;
    private int xStep = 4;
    private boolean movingCard = false;

    /**
     * Creates a AnimationPanel with specified LayoutManager
     * @param layout
     */
    public AnimationPanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * Move image cardImage from point (fromX, fromY) to (toX, toY) using totalStep steps and stepInterval
     * @param cardImage image of Card
     * @param fromX initial X position
     * @param fromY initial Y position
     * @param toX final X position
     * @param toY final Y position
     * @param stepInterval time in milliseconds
     * @param totalStep number of steps between points
     */
    public void moveCard(String cardImage, int fromX, int fromY, int toX, int toY, int stepInterval, int totalStep) {
        try {
            bg = ImageIO.read(getClass().getResource("/assets/images/" + cardImage + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Point> fromList = new ArrayList<>();
        ArrayList<Point> toList = new ArrayList<>();
        fromList.add(new Point(fromX, fromY));
        toList.add(new Point(toX, toY));
        moveCard(cardImage, fromList, toList, null, stepInterval, totalStep);
    }

    /**
     * Move image cardImage from points fromList to toList using totalStep steps and stepInterval
     * first from fromList.get(0) to toList.get(0), then fromList.get(1) to toList.get(1)...
     * @param cardImage: image
     * @param fromList: list of from positions
     * @param toList: list of to positions
     * @param components: make visible after animation
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

    /**
     * Inner class of ActionListener to control moving position update
     */
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

        /**
         * Create a AnimationActionListener with necessary information of moving an image
         * step by step
         * @param fromList list of initial Positions
         * @param toList list of final Positions
         * @param components list of Components to be visible after moving
         * @param totalStep total amount of steps from one point to another
         */
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

        /**
         * Using timer triggered events to periodically update moving positions of an image
         * @param e ActionEvent trigger
         */
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

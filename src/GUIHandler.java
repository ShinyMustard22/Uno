import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import cards.ColorCard;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Class the represents the user interface for a client.
 * 
 * @author Ritam Chakraborty, Srushti Chaudhari
 * @version May 25, 2022
 */
public class GUIHandler extends JFrame implements ActionListener {

    private static final int startingWidth  = 1000;
    private static final int startingHeight = 400;
    private static final int unoButtonWidth = 50;
    private static final int unoButtonHeight = 50;
    private static int margin = 5;

    private JMenuBar menuBar;
    private JMenu help, options;
    private JMenuItem rules, soundEffects, bgMusic;
    private JTextField nameField;
    private JLabel errorMessage1, errorMessage2;
    private JLabel invalidName, enterNamePrompt, waiting;
    private JButton startGame;
    private JButton deck;
    private JLabel discardPile;

    private JPanel mainPanel, playerInfo, playerHand;
    private AnimationPanel board;
    private JScrollPane playerHandScroll;
    private GridBagConstraints gbc;
    private JTable playerTable;
    private LinkedList<JButton> hand;
    private LinkedList<String> strHand;

    private JLayeredPane unoLayeredPane;
    private JButton unoButton;

    private JButton red, blue, green, yellow, cancelWild;
    private String currentColor;

    private JLabel congratulations, spectateLabel;

    private File bgSoundFile;
    private AudioInputStream bgSoundInput;
    private Clip bgClip;

    private static boolean soundOn = true;
    private static final String cardFlippedSound = "cardFlipping";
    private static final String playerJoinsOrLeaves = "playerInOrOut";
    private static final String errorSound = "wrong";
    private static final String gameStartedSound = "gameStart";

    private static final Color lightBlue = new Color(176,196,222);
    private static final Color unoRed = new Color(215, 38, 0);
    private static final Color unoBlue = new Color(9, 86, 191);
    private static final Color unoGreen = new Color(55, 151, 17);
    private static final Color unoYellow = new Color(236, 212, 7);

    private static final Font appFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    Font invalidfont = new Font("Arial", Font.PLAIN, 24);

    private LinkedHashMap<String, Integer> players;
    private StringBuffer rulesString;

    private DataOutputStream out;
    private String myName;
    private boolean gameIsOver;

    /**
     * Constructs the frame, plays the background music, and initializes object
     * to write data.
     * @param out output stream used to write data
     */
    public GUIHandler(DataOutputStream out) {
        super("Uno");
        setBounds(0, 0, startingWidth, startingHeight);

        this.out = out;

        // Create the Icon Image for this application
        ImageIcon unoLogo = new ImageIcon(getClass().getResource("/assets/images/uno_logo.png"));
        setIconImage(unoLogo.getImage());

        mainPanel = new JPanel(new BorderLayout());

        board = new AnimationPanel(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(margin, margin, margin, margin);

        enterNamePrompt = new JLabel("Please enter your name:");
        nameField = new JTextField(10);
        invalidName = new JLabel();
        invalidName.setFont(invalidfont);
        invalidName.setPreferredSize(getTextFieldDimension(invalidfont, "This username contains illegal characters..."));

        nameField.addActionListener(this);
        board.add(enterNamePrompt);
        board.add(nameField);

        playerInfo = new JPanel(new BorderLayout());
        playerInfo.setOpaque(false);
        playerInfo.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));

        playerHand = new JPanel(new FlowLayout());
        playerHand.setOpaque(false);
        playerHand.add(invalidName);

        playerHandScroll = new JScrollPane(playerHand);
        playerHandScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        playerHandScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        playerHandScroll.setBorder(null);

        mainPanel.add(playerInfo, BorderLayout.NORTH);
        mainPanel.add(board, BorderLayout.CENTER);
        mainPanel.add(playerHandScroll, BorderLayout.SOUTH);

        try {
            bgSoundFile = new File ("src/assets/audio/bgMusic.wav");
            try {
                bgSoundInput = AudioSystem.getAudioInputStream(bgSoundFile.getAbsoluteFile());
                bgClip = AudioSystem.getClip();
                bgClip.open(bgSoundInput);
                bgClip.start();
                bgClip.loop(Clip.LOOP_CONTINUOUSLY);

                FloatControl volume = (FloatControl) bgClip.getControl(FloatControl.Type.MASTER_GAIN);
                volume.setValue(-10.0f);
            } catch(Exception e) {

            }
        } catch (Exception e) {
            System.out.println("audio file not found");
        }

        add(mainPanel);

        unoLayeredPane = getLayeredPane();
        unoButton = new JButton("Uno!");
        unoButton.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);

        players = new LinkedHashMap<String, Integer>();
        gameIsOver = false;

        if (out == null) {
            errorScreen();
        }
    }

    /**
     * Displays an error message if communicated with the server breaks
     */
    public void errorScreen() {
        mainPanel.removeAll();
        mainPanel.setLayout(new GridBagLayout());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        errorMessage1 = new JLabel("ERROR: Communication with the server was interrupted...");
        errorMessage1.setFont(new Font(errorMessage1.getFont().getName(), Font.PLAIN, 32));
        errorMessage1.setForeground(Color.RED);
        errorMessage1.setOpaque(false);
        mainPanel.add(errorMessage1, gbc);

        gbc.gridy = 1;

        errorMessage2 = new JLabel("Please try again at another time...");
        errorMessage2.setFont(new Font(errorMessage2.getFont().getName(), Font.PLAIN, 32));
        errorMessage2.setForeground(Color.RED);
        errorMessage2.setOpaque(false);
        mainPanel.add(errorMessage2, gbc);

        mainPanel.revalidate();
        mainPanel.repaint();

        revalidate();
        repaint();
    }

    private Dimension getTextFieldDimension(Font font, String text) {
        if (null == font || null == text) {
            throw new IllegalArgumentException("Font or text is null");
        }

        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
        final Rectangle2D stringBounds = font.getStringBounds(text, frc);
        return new Dimension((int)stringBounds.getWidth(), (int)stringBounds.getHeight());
    }


    private void waitingScreen() {
        board.removeAll();
        playerHand.removeAll();

        menuBar = new JMenuBar();
        help = new JMenu("Help");
        options = new JMenu("Options");
        rules = new JMenuItem("Rules");
        rules.addActionListener(this);

        rulesString = new StringBuffer();
        Path rulesPath = Paths.get("src/assets/rules.txt");

        try {
            java.util.List<String> lines = Files.readAllLines(rulesPath, StandardCharsets.UTF_8);
            for (String line : lines) {
                rulesString.append(line);
                rulesString.append("\n");
            }
        } catch (IOException ex) {
            System.out.println("rules.txt cannot be located.");
        }

        help.add(rules);

        soundEffects = new JMenuItem("SFX ON");
        soundEffects.addActionListener(this);
        options.add(soundEffects);

        bgMusic = new JMenuItem("Music ON");
        bgMusic.addActionListener(this);
        options.add(bgMusic);

        menuBar.add(help);
        menuBar.add(options);

        updateTable();

        waiting = new JLabel("Waiting for the game to start...");
        waiting.setFont(new Font(waiting.getFont().getName(), Font.PLAIN, 32));
        board.add(waiting, gbc);

        board.revalidate();
        board.repaint();

        playerHand.revalidate();
        playerHand.repaint();

        revalidate();
        repaint();

        setJMenuBar(menuBar);
    }

    private void updateTable() {
        playerInfo.removeAll();

        String[] columnNames = new String[players.size()];
        Object[][] data = new Object[1][players.size()];
        Iterator<String> iter = players.keySet().iterator();
        int index = 0;

        int myIndex = index;
        while (iter.hasNext()) {
            String playerName = iter.next();
            columnNames[index] = playerName;
            data[0][index] = players.get(playerName);
            if (columnNames[index].equals(myName)) {
                myIndex = index;
            }
            index++;
        }

        playerTable = new JTable(data, columnNames);
        playerTable.setPreferredScrollableViewportSize(new Dimension(getWidth() - margin * 4, playerTable.getMinimumSize().height));
        playerTable.setFillsViewportHeight(true);
        playerTable.setOpaque(false);
        playerTable.getTableHeader().setReorderingAllowed(false);
        playerTable.setEnabled(false);
        playerTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        playerTable.setFont(appFont);
        playerTable.getTableHeader().setFont(appFont);
        playerTable.getTableHeader().getColumnModel().getColumn(myIndex)
                .setHeaderRenderer(new LeaderRenderer(lightBlue, Color.BLUE));

        playerInfo.add(new JScrollPane(playerTable), BorderLayout.CENTER);
        playerInfo.revalidate();
        playerInfo.repaint();

        revalidate();
        repaint();
    }

    private void setTurn(int index) {
        playerTable.getColumnModel().getColumn(index).setCellRenderer(new PlayerRenderer());



        playerInfo.revalidate();
        playerInfo.repaint();

        revalidate();
        repaint();
    }

    private class LeaderRenderer extends DefaultTableCellRenderer {
        Color bg;
        Color fg;

        private LeaderRenderer(Color bg, Color fg) {
            this.bg = bg;
            this.fg = fg;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            cell.setFont(appFont);
            cell.setBackground(bg);
            cell.setForeground(fg);
            return cell;
        }
    }

    private class PlayerRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            cell.setBackground(Color.GREEN.brighter());
            return cell;
        }
    }

    private void createBoard(String firstCard) {
        board.removeAll();

        ImageIcon faceDown = new ImageIcon(getClass().getResource("/assets/images/card_face_down.png"));
        deck = new JButton(faceDown);
        board.add(deck, gbc);
        deck.setSize(faceDown.getIconWidth(), faceDown.getIconHeight());
        deck.addActionListener(this);

        ImageIcon lastCard = new ImageIcon(getClass().getResource("/assets/images/" + firstCard + ".png"));
        discardPile = new JLabel(lastCard);
        board.add(discardPile, gbc);

        board.revalidate();
        board.repaint();

        revalidate();
        repaint();
    }

    private void makeLeader() {
        board.removeAll();

        startGame = new JButton("Start Game");
        startGame.setFont(new Font(startGame.getFont().getName(), Font.PLAIN, 32));
        startGame.addActionListener(this);
        board.add(startGame, gbc);

        board.revalidate();
        board.repaint();

        revalidate();
        repaint();
    }

    private void createHand(java.util.List<JButton> newCards) {
        ArrayList<Point> fromList = new ArrayList<>();
        ArrayList<Point> toList = new ArrayList<>();
        ArrayList<Component> components = new ArrayList<>();
        for (JButton card : newCards) {
            playerHand.add(card);
            card.setSize(card.getIcon().getIconWidth(), card.getIcon().getIconHeight());
            card.addActionListener(this);
            components.add(card);
            fromList.add(new Point(deck.getX(), deck.getY()));
            toList.add(new Point(deck.getX(), board.getHeight()));
        }
        revalidate();
        playerHand.setPreferredSize(new Dimension(playerHand.getWidth(), playerHand.getHeight()));
        for (JButton card : newCards) {
            card.setVisible(false);
        }

        board.moveCard("card_face_down", fromList, toList, components, 10, 10);
        playerHand.revalidate();
        playerHand.repaint();

        revalidate();
        repaint();
    }


    private void updateDiscardPile(String card) {
        discardPile.setIcon(new ImageIcon(getClass().getResource("/assets/images/" + card + ".png")));
        playSound(cardFlippedSound);

        board.revalidate();
        board.repaint();

        revalidate();
        repaint();
    }

    private void removeCard(int index) {
        int x = hand.get(index).getX();
        String card = strHand.get(index);
        playerHand.remove(hand.get(index));

        strHand.remove(index);
        hand.remove(index);

        playerHand.revalidate();
        playerHand.repaint();

        revalidate();
        repaint();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                board.moveCard(card, x, board.getHeight(), discardPile.getX(), discardPile.getY(), 10, 10);
                return null;
            }
        }.execute();
    }

    private void chooseColorScreen() {
        board.removeAll();
        board.setOpaque(true);

        for (JButton card : hand) {
            card.removeActionListener(this);
        }

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e){
            e.printStackTrace();
        }

        red = new JButton("Red");
        red.setBackground(unoRed);
        red.setOpaque(true);
        red.addActionListener(this);

        blue = new JButton("Blue");
        blue.setBackground(unoBlue);
        blue.setOpaque(true);
        blue.addActionListener(this);

        green = new JButton("Green");
        green.setBackground(unoGreen);
        green.setOpaque(true);
        green.addActionListener(this);

        yellow = new JButton("Yellow");
        yellow.setBackground(unoYellow);
        yellow.setOpaque(true);
        yellow.addActionListener(this);

        cancelWild = new JButton("Never mind...");
        cancelWild.addActionListener(this);

        board.add(red, gbc);
        board.add(blue, gbc);
        board.add(green, gbc);
        board.add(yellow, gbc);
        board.add(cancelWild, gbc);

        board.revalidate();
        board.repaint();

        revalidate();
        repaint();
    }

    private void returnToBoard() {
        board.removeAll();
        setColor();

        red.removeActionListener(this);
        blue.removeActionListener(this);
        green.removeActionListener(this);
        yellow.removeActionListener(this);
        cancelWild.removeActionListener(this);

        for (JButton card : hand) {
            card.addActionListener(this);
        }

        board.add(deck);
        board.add(discardPile);

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e){
            e.printStackTrace();
        }

        board.revalidate();
        board.repaint();

        revalidate();
        repaint();
    }

    private void enterSpectateMode(int place) {
        playerHand.removeAll();
        unoLayeredPane.remove(unoButton);

        deck.removeActionListener(this);

        congratulations = new JLabel("Congratulations! Your placing: " + place);
        congratulations.setHorizontalAlignment(JLabel.CENTER);
        congratulations.setForeground(Color.GREEN.darker());
        congratulations.setFont(new Font(congratulations.getFont().getName(), Font.PLAIN, 32));

        spectateLabel = new JLabel("You are now spectating...");
        spectateLabel.setHorizontalAlignment(JLabel.CENTER);
        spectateLabel.setFont(new Font(spectateLabel.getFont().getName(), Font.PLAIN, 32));

        playerHand.setLayout(new GridLayout(2, 1));
        playerHand.add(congratulations);
        playerHand.add(spectateLabel);
        playerHand.setBorder(new EmptyBorder(gbc.insets));

        playerHand.revalidate();
        playerHand.repaint();

        revalidate();
        repaint();
    }

    private void finalScreen(String place) {
        unoLayeredPane.remove(unoButton);

        JLabel finalLabel1 = new JLabel("The Game is Over! Your place: " + place);
        finalLabel1.setHorizontalAlignment(JLabel.CENTER);
        finalLabel1.setForeground(Color.GREEN.darker());
        finalLabel1.setFont(new Font(finalLabel1.getFont().getName(), Font.PLAIN, 32));

        JLabel finalLabel2 = new JLabel("Be sure to play again!");
        finalLabel2.setHorizontalAlignment(JLabel.CENTER);
        finalLabel2.setFont(new Font(finalLabel2.getFont().getName(), Font.PLAIN, 32));

        mainPanel.removeAll();
        mainPanel.setLayout(new GridLayout(2, 1));
        mainPanel.add(finalLabel1);
        mainPanel.add(finalLabel2);
        mainPanel.setBorder(new EmptyBorder(gbc.insets));

        mainPanel.revalidate();
        mainPanel.repaint();

        revalidate();
        repaint();
    }

    private void spawnUno() {
        int x = (int) (Math.random() * (getWidth() - unoButtonWidth));
        int y = (int) (Math.random() * (getHeight() - unoButtonHeight));

        unoButton.setBounds(x, y, unoButtonWidth, unoButtonHeight);

        unoLayeredPane.add(unoButton, JLayeredPane.POPUP_LAYER);

        unoLayeredPane.revalidate();
        unoLayeredPane.repaint();

        revalidate();
        repaint();
    }

    private void deleteUno() {
        unoLayeredPane.remove(unoButton);

        unoLayeredPane.revalidate();
        unoLayeredPane.repaint();

        revalidate();
        repaint();
    }

    private void setColor() {
        Color color;

        if (currentColor.equals(ColorCard.RED)) {
            color = unoRed;
        }

        else if (currentColor.equals(ColorCard.BLUE)) {
            color = unoBlue;
        }

        else if (currentColor.equals(ColorCard.GREEN)) {
            color = unoGreen;
        }

        else if (currentColor.equals(ColorCard.YELLOW)) {
            color = unoYellow;
        }

        else {
            throw new IllegalArgumentException();
        }

        board.setBackground(color);
    }

    private void toggleSound() {
        if (soundOn) {
            soundOn = false;
            soundEffects.setText("SFX OFF");
        }
        else {
            soundOn = true;
            soundEffects.setText("SFX ON");

        }
    }

    private void playSound(String soundType) {
        if (soundOn) {
            File soundFile;
            AudioInputStream input;
            Clip sound;

            try {
                soundFile = new File ("src/assets/audio/"+ soundType +".wav");
                try {
                    input = AudioSystem.getAudioInputStream(soundFile.getAbsoluteFile());
                    sound = AudioSystem.getClip();
                    sound.open(input);
                    sound.start();

                } catch(Exception e) {

                }
            } catch (Exception e) {
                System.out.println("audio file not found");
            }
        }
    }

    private void toggleBackgroundMusic() {
        if (bgClip.isActive() && (bgClip != null)) {
            bgClip.stop();
            bgMusic.setText("Music OFF");
        }
        else {
            bgClip.start();
            bgClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Decodes the data recieved from the server
     * @param allStrData all the data recieved from server
     */
    public void decode(String allStrData) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (allStrData == null) {
                    return;
                }

                String[] data = allStrData.split("\n");

                for (String strData : data) {
                    if (strData.contains(Server.INIT_PLAYER_LIST)) {
                        String[] playerList = strData.substring(Server.INIT_PLAYER_LIST.length()).split(" ");
                        for (String player : playerList) {
                            players.put(player, Player.STARTING_HAND_SIZE);
                        }
                        waitingScreen();
                    }

                    else if (strData.contains(Server.ADD_PLAYER)) {
                        players.put(strData.substring(Server.ADD_PLAYER.length()), Player.STARTING_HAND_SIZE);
                        playSound(playerJoinsOrLeaves);
                        updateTable();
                    }

                    else if (strData.contains(Server.REMOVE_PLAYER)) {
                        players.remove(strData.substring(Server.REMOVE_PLAYER.length()));
                        if (!gameIsOver) {
                            updateTable();
                        }
                    }

                    else if (strData.contains(Server.INVALID_USERNAME)) {
                        String message = "This username contains illegal characters...";
                        invalidName.setForeground(Color.RED);
                        invalidName.setPreferredSize(getTextFieldDimension(invalidfont, message));
                        invalidName.setText(message);
                    }

                    else if (strData.contains(Server.TAKEN_USERNAME)) {
                        String message = "This username has been taken...";
                        invalidName.setForeground(Color.RED);
                        invalidName.setPreferredSize(getTextFieldDimension(invalidfont, message));
                        invalidName.setText(message);
                    }

                    else if (strData.contains(Server.GAME_ALREADY_BEGAN)) {
                        String message = "A game is currently going on right now. Join later...";
                        invalidName.setForeground(Color.BLUE);
                        invalidName.setPreferredSize(getTextFieldDimension(invalidfont, message));
                        invalidName.setText(message);
                    }

                    else if (strData.contains(Server.FIRST_CARD)) {
                        createBoard(strData.substring(Server.FIRST_CARD.length()));
                        playSound(gameStartedSound);
                    }

                    else if (strData.contains(Server.INIT_PLAYER_HAND)) {
                        hand = new LinkedList<JButton>();
                        strHand = new LinkedList<String>();
                        java.util.List<JButton> newCards = new LinkedList<JButton>();
                        String[] strPlayerHand = strData.substring(Server.INIT_PLAYER_HAND.length()).split(" ");
                        for (String card : strPlayerHand) {
                            ImageIcon icon = new ImageIcon(getClass().getResource("/assets/images/" + card.toString() + ".png"));
                            newCards.add(new JButton(icon));
                            strHand.add(card.toString());
                        }

                        hand.addAll(newCards);
                        createHand(newCards);
                    }

                    else if (strData.contains(Server.AM_LEADER)) {
                        if (strData.contains("true")) {
                            makeLeader();
                        }
                    }

                    else if (strData.contains(Server.SET_LEADER)) {
                        makeLeader();
                    }

                    else if (strData.contains(Server.PLAY_CARD)) {
                        int cardIndex = Integer.valueOf(strData.substring(Server.PLAY_CARD.length()));
                        removeCard(cardIndex);
                    }


                    else if (strData.contains(Server.SOMEBODY_PLAYED_CARD)) {
                        String[] card = strData.substring(Server.SOMEBODY_PLAYED_CARD.length()).split(" ");
                        updateDiscardPile(card[1]);
                        players.replace(card[0], players.get(card[0]) - 1);
                        updateTable();
                    }

                    else if (strData.contains(Server.DRAW_CARDS)) {
                        String[] cardsToAdd = strData.substring(Server.DRAW_CARDS.length()).split(" ");
                        java.util.List<JButton> newCards = new LinkedList<JButton>();

                        for (String strCard : cardsToAdd) {
                            playSound(cardFlippedSound);

                            ImageIcon icon = new ImageIcon(getClass().getResource("/assets/images/" + strCard + ".png"));
                            newCards.add(new JButton(icon));
                            strHand.add(strCard);
                        }

                        hand.addAll(newCards);
                        createHand(newCards);
                    }

                    else if (strData.contains(Server.CHOOSE_COLOR)) {
                        chooseColorScreen();
                    }

                    else if (strData.contains(Server.ERROR)) {
                        playSound(errorSound);
                    }

                    else if (strData.contains(Server.WON)) {
                        int place = Integer.valueOf(strData.substring(Server.WON.length()));
                        enterSpectateMode(place);
                    }

                    else if (strData.contains(Server.PLAYER_WON)) {
                        // Play some sfx
                    }

                    else if (strData.contains(Server.SET_TURN)) {
                        int index = Integer.valueOf(strData.substring(Server.SET_TURN.length()));
                        setTurn(index);
                    }

                    else if (strData.contains(Server.DREW_CARDS)) {
                        String[] playerHandSize = strData.substring(Server.DREW_CARDS.length()).split(" ");
                        players.replace(playerHandSize[0], Integer.valueOf(playerHandSize[1]));
                        updateTable();
                    }

                    else if (strData.contains(Server.UNO_TIME)) {
                        spawnUno();
                    }

                    else if (strData.contains(Server.END_GAME)) {
                        gameIsOver = true;
                        String place = strData.substring(Server.END_GAME.length());
                        finalScreen(place);
                    }

                    else if (strData.contains(Server.END_UNO_TIME)) {
                        deleteUno();
                    }

                    else if (strData.contains(Server.SET_COLOR)) {
                        currentColor = strData.substring(Server.SET_COLOR.length());
                        setColor();
                    }

                    else if (strData.contains(Server.NEW_DISCARD_PILE)) {
                        String card = strData.substring(Server.NEW_DISCARD_PILE.length());
                        discardPile.setIcon(new ImageIcon("/assets/images/" + card + ".png"));

                        board.revalidate();
                        board.repaint();

                        revalidate();
                        repaint();
                    }
                }
            }
        });
    }

    @Override
    /**
     * Executes a process depending on the action that took place on the frame
     * @param e the action even that took place
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nameField) {
            myName = nameField.getText();
            write(Server.NAME + nameField.getText());
        }

        else if (e.getSource() == startGame) {
            write(Server.GAME_STARTED);
        }

        else if (e.getSource() == deck) {
            write(Server.ASK_TO_DRAW);
        }

        else if (hand != null && hand.contains(e.getSource())) {
            int i = hand.indexOf(e.getSource());
            write(Server.PLAY_CARD + strHand.get(i) + " " + i);
        }

        else if (e.getSource() == rules) {
            JOptionPane.showMessageDialog(this, rulesString.toString(), "Uno Rules", JOptionPane.PLAIN_MESSAGE);
        }

        else if (e.getSource() == soundEffects) {
            toggleSound();
        }

        else if (e.getSource() == bgMusic) {
            toggleBackgroundMusic();
        }

        else if (e.getSource() == red) {
            write(ColorCard.RED);
            returnToBoard();
        }

        else if (e.getSource() == blue) {
            write(ColorCard.BLUE);
            returnToBoard();
        }

        else if (e.getSource() == green) {
            write(ColorCard.GREEN);
            returnToBoard();
        }

        else if (e.getSource() == yellow) {
            write(ColorCard.YELLOW);
            returnToBoard();

        }

        else if (e.getSource() == cancelWild) {
            write(Server.CANCEL_OPERATION);
            returnToBoard();
        }

        else if (e.getSource() == unoButton) {
            write(Server.UNO_TIME);
        }
    }

    private void write(String data) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    out.writeUTF(data);
                    out.flush();
                } catch (IOException ex) {
                    killOutput();
                }

                return null;
            }
        }.execute();
    }

    private void killOutput() {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ex) {

        }
    }
}
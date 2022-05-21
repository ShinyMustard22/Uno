import java.awt.*;
import javax.swing.*;
// import javax.swing.plaf.DimensionUIResource;
// import javax.swing.plaf.InsetsUIResource;
import javax.swing.border.EmptyBorder;
import cards.ColorCard;
import java.awt.event.*;
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

public class GUIHandler extends JFrame implements ActionListener, ComponentListener {

    private static final int startingWidth  = 1000;
    private static final int startingHeight = 400;
    private static int margin = 5;

    private JMenuBar menuBar;
    private JMenu help;
    private JMenuItem rules;
    private JMenuItem options;
    private JButton soundIcon;
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

    private JDialog optionWindow;

    // private JLayeredPane unoLayers; 
    // private JPanel unoPanel; 

    private JButton unoButton;
    private JButton red, blue, green, yellow, cancelWild;

    private JLabel congratulations, spectateLabel;

    private static boolean soundOn = true;
    private static final String cardFlippedSound = "cardFlipping";
    private static final String playerJoinsOrLeaves = "playerInOrOut";
//<<<<<<< HEAD
//    private static final String unoSound = "unoVerbal";
//    private static final String errorSound = "wrong";
//    public static final String gameStartedSound = "gameStart";
//=======
    // private static final String unoSound = "unoVerbal"; 
    private static final String errorSound = "wrong"; 
    public static final String gameStartedSound = "gameStart";

    private static final Color lightBlue = new Color(176,196,222);


    private LinkedHashMap<String, Integer> players;
    private StringBuffer rulesString;

    private DataOutputStream out;
    private String myName;

    private static final Font appFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);

    public GUIHandler(DataOutputStream out) {
        super("Uno");
        setBounds(0, 0, startingWidth, startingHeight);

        this.out = out;

        // Create the Icon Image for this application
        ImageIcon unoLogo = new ImageIcon(getClass().getResource("/assets/images/uno_logo.png"));

        setIconImage(unoLogo.getImage());

        optionWindow = new JDialog(this);
        optionWindow.setLayout(new FlowLayout());
        optionWindow.setResizable(false);
        optionWindow.setBounds(0, 0, startingWidth, startingHeight);
        optionWindow.setVisible(false);
        optionWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ImageIcon soundImageIcon = new ImageIcon(getClass().getResource("/assets/images/soundOnIcon.png"));
        soundIcon = new JButton(soundImageIcon);
        soundIcon.setSize(soundImageIcon.getIconWidth(), soundImageIcon.getIconHeight());
        soundIcon.addActionListener(this);
        optionWindow.add(soundIcon);

        mainPanel = new JPanel(new BorderLayout());

        board = new AnimationPanel(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(margin, margin, margin, margin);

        enterNamePrompt = new JLabel("Please enter your name:");
        nameField = new JTextField(10);
        invalidName = new JLabel();
        invalidName.setForeground(Color.RED);
        invalidName.setFont(new Font(invalidName.getFont().getName(), Font.PLAIN, 24));

        nameField.addActionListener(this);
        board.add(enterNamePrompt);
        board.add(nameField);

        playerInfo = new JPanel(new BorderLayout());
        playerInfo.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));

        playerHand = new JPanel(new FlowLayout());
        playerHandScroll = new JScrollPane(playerHand);
        playerHandScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        playerHandScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        playerHand.add(invalidName);

        mainPanel.add(playerInfo, BorderLayout.NORTH);
        mainPanel.add(board, BorderLayout.CENTER);
        mainPanel.add(playerHandScroll, BorderLayout.SOUTH);

        // unoLayers = new JLayeredPane();
        // unoLayers = getLayeredPane();

        // UnoLayoutManager unoLayout = new UnoLayoutManager();
        // unoLayers.setLayout(unoLayout);

        // unoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        // unoPanel.setBackground(Color.CYAN);
        // // unoPanel.setPreferredSize(new DimensionUIResource(15, 15));
        // // unoLayers.setBounds(0, 0, startingWidth + 10, startingHeight + 10);

        // unoLayers.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        // unoLayout.setBounds(unoPanel, new Rectangle(5, 5, 30, 30)); //change to 0,0 pos

        // // Insets insets = unoLayers.getInsets();
        // // unoPanel.setBounds(insets.left + 2, insets.top + 2, unoPanel.getPreferredSize().width, unoPanel.getPreferredSize().height);
        // // unoPanel.setVisible(false);

        // // add(unoLayers);
        // pack();
        // setVisible(true);

        add(mainPanel);

        players = new LinkedHashMap<String, Integer>();

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        if (out == null) {
            errorScreen();
        }
    }

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

    private void waitingScreen() {
        board.removeAll();
        playerHand.removeAll();

        menuBar = new JMenuBar();
        help = new JMenu("Help");
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

        options = new JMenuItem("Options");
        options.addActionListener(this);
        help.add(options);

        menuBar.add(help);

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

        int myIndex = 0;
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
        playerTable.setPreferredScrollableViewportSize(new Dimension(getWidth() - 10 * 2, playerTable.getMinimumSize().height));
        playerTable.setFillsViewportHeight(true);
        playerTable.setOpaque(false);
        playerTable.getTableHeader().setReorderingAllowed(false);
        playerTable.setEnabled(false);
        playerTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        playerTable.setFont(appFont);
        playerTable.getTableHeader().setFont(appFont);
        playerTable.getTableHeader().getColumnModel().getColumn(myIndex)
            .setHeaderRenderer(new LeaderRenderer(lightBlue, Color.BLUE));
        playerTable.getColumnModel().getColumn(myIndex).setCellRenderer(new LeaderRenderer(
            lightBlue.brighter(), Color.black));

        playerInfo.add(new JScrollPane(playerTable), BorderLayout.CENTER);
        playerInfo.revalidate();
        playerInfo.repaint();

        revalidate();
        repaint();
    }

    private static class LeaderRenderer extends DefaultTableCellRenderer {
        Color bg, fg;

        public LeaderRenderer(Color bg, Color fg) {
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

    private static class PlayerRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
            boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
            cell.setFont(boldFont);
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
        for (JButton card : newCards) {
            playerHand.add(card);
            card.setSize(card.getIcon().getIconWidth(), card.getIcon().getIconHeight());
            card.addActionListener(this);
        }

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
        board.moveCard(card, x, board.getHeight(), discardPile.getX(), discardPile.getY());
    }

    private void chooseColorScreen() {
        board.removeAll();

        for (JButton card : hand) {
            card.removeActionListener(this);
        }

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e){
           e.printStackTrace();
        }

        red = new JButton("Red");
        red.setBackground(Color.RED);
        red.setOpaque(true);
        red.addActionListener(this);

        blue = new JButton("Blue");
        blue.setBackground(Color.CYAN);
        blue.setOpaque(true);
        blue.addActionListener(this);

        green = new JButton("Green");
        green.setBackground(Color.GREEN);
        green.setOpaque(true);
        green.addActionListener(this);

        yellow = new JButton("Yellow");
        yellow.setBackground(Color.YELLOW);
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

    private void setTurn(int index) {
        updateTable();
        playerTable.getTableHeader().getColumnModel().getColumn(index).setCellRenderer(new PlayerRenderer());
    }

    private void enterSpectateMode(int place) {
        playerHand.removeAll();

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

    private void finalScreen(int place) {
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
        // int x = (int)(Math.random() * getWidth()); 
        // int y = (int)(Math.random() * getHeight());
        
        // unoButton = new JButton("UNO!");
        // unoButton.setSize(10,10);


        // unoPanel.add(unoButton);
        // unoPanel.setLocation(x, y);
        // unoPanel.setVisible(true);

        // unoPanel.revalidate();
        // unoPanel.repaint();

        // unoLayers.revalidate();
        // unoLayers.repaint();

        // revalidate();
        // repaint();
    }
    
    private void toggleSound() {
        if (soundOn) {
            soundOn = false;
            ImageIcon soundImageIcon = new ImageIcon(getClass().getResource("/assets/images/soundOffIcon.png"));
            soundIcon.setIcon(soundImageIcon);
            soundIcon.setSize(soundImageIcon.getIconWidth(), soundImageIcon.getIconHeight());
        }

        else {
            soundOn = true;
            ImageIcon soundImageIcon = new ImageIcon(getClass().getResource("/assets/images/soundOnIcon.png"));
            soundIcon.setIcon(soundImageIcon);
            soundIcon.setSize(soundImageIcon.getIconWidth(), soundImageIcon.getIconHeight());
        }
    }

    public static void playSound(String soundType) {
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
                        updateTable();
                    }

                    else if (strData.contains(Server.INVALID_USERNAME)) {
                        invalidName.setText("This username contains illegal characters...");
                    }

                    else if (strData.contains(Server.TAKEN_USERNAME)) {
                        invalidName.setText("This username has been taken...");
                    }

                    else if (strData.contains(Server.FIRST_CARD)) {
                        createBoard(strData.substring(Server.FIRST_CARD.length()));
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
//<<<<<<< HEAD
//                        String card = strData.substring(Server.PLAY_CARD.length());
//                        removeCard(card);
//                    }
//=======
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

                    else if (strData.contains(Server.UNO_TIME)) {
                        spawnUno();
                    }

                    else if (strData.contains(Server.WON)) {
                        int place = Integer.valueOf(strData.substring(Server.WON.length()));
                        enterSpectateMode(place);
                    }

                    else if (strData.contains(Server.PLAYER_WON)) {
                        // Do something
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

                    else if (strData.contains(Server.END_GAME)) {
                        int place = Integer.valueOf(strData.substring(Server.END_GAME.length()));
                        finalScreen(place);
                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nameField) {
            myName = nameField.getText();
            write(Server.NAME + nameField.getText());
        }

        else if (e.getSource() == startGame) {
            write(Server.GAME_STARTED);
            playSound(gameStartedSound);
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

        else if (e.getSource() == options) {
           optionWindow.setVisible(true);
        }

        else if (e.getSource() == soundIcon) {
            toggleSound();
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

    @Override
    public void componentResized(ComponentEvent e) {
       if (e.getSource() == this) {

       }
    }

    @Override
    public void componentMoved(ComponentEvent e) {


    }

    @Override
    public void componentShown(ComponentEvent e) {


    }

    @Override
    public void componentHidden(ComponentEvent e) {


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

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

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
    private JTextArea errorMessage;
    private JLabel invalidName, enterNamePrompt, waiting;
    private JButton startGame;
    private JButton deck;
    private JLabel discardPile;

    private JPanel mainPanel, playerInfo, board, playerHand;
    private GridBagConstraints gbc;
    private JTable playerTable;
    private LinkedList<JButton> hand;
    private LinkedList<String> strHand;

    private JDialog optionWindow; 

    private JLayeredPane unoLayers; 
    private JPanel unoPanel; 
    private JButton unoButton;

    private JLabel congratulations, spectateLabel;

    private static boolean soundOn = true; 
    private static final String cardFlippedSound = "cardFlipping"; 
    private static final String playerJoinsOrLeaves = "playerInOrOut";
    private static final String unoSound = "unoVerbal"; 
    private static final String errorSound = "wrong"; 
    public static final String gameStartedSound = "gameStart"; 

    private JButton red, blue, green, yellow;

    private LinkedHashMap<String, Integer> players;

    private StringBuffer rulesString;
    private int place;
    
    private DataOutputStream out;

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

        board = new JPanel(new GridBagLayout());
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

        playerHand.add(invalidName);

        mainPanel.add(playerInfo, BorderLayout.NORTH);
        mainPanel.add(board, BorderLayout.CENTER);
        mainPanel.add(playerHand, BorderLayout.SOUTH);

        // unoPanel = new JPanel(null); 
        // unoPanel.setBackground(Color.CYAN);
        // unoPanel.setPreferredSize(new DimensionUIResource(15, 15));;

        // unoLayers = getLayeredPane();
        // unoLayers.setLayout(null);
        // unoLayers.setBounds(0, 0, startingWidth + 10, startingHeight + 10); 
        
        // Insets insets = unoLayers.getInsets(); 
        // unoPanel.setBounds(insets.left + 2, insets.top + 2, unoPanel.getPreferredSize().width, unoPanel.getPreferredSize().height);
        // unoPanel.setVisible(false);

        // unoLayers.add(mainPanel, JLayeredPane.DEFAULT_LAYER); 
        // setVisible(true);

        // add(unoLayers); 
        // unoLayers.setVisible(true);

        add(mainPanel);
    
        players = new LinkedHashMap<String, Integer>();

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
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

        while (iter.hasNext()) {
            String playerName = iter.next();
            columnNames[index] = playerName;
            data[0][index] = players.get(playerName);
            index++;
        }
        
        playerTable = new JTable(data, columnNames);
        playerTable.setPreferredScrollableViewportSize(new Dimension(getWidth() - 10 * 2, playerTable.getMinimumSize().height));
        playerTable.setFillsViewportHeight(true);
        playerTable.setOpaque(false);
        playerTable.getTableHeader().setReorderingAllowed(false);
        playerTable.setEnabled(false);
        playerTable.setGridColor(Color.BLACK);
        playerTable.getTableHeader().setBackground(Color.LIGHT_GRAY);

        playerInfo.add(new JScrollPane(playerTable), BorderLayout.CENTER);
        playerInfo.revalidate();
        playerInfo.repaint();

        revalidate();
        repaint();
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

    private void removeCard(String card) {
        int i = strHand.indexOf(card);
        playerHand.remove(hand.get(i)); 

        strHand.remove(i); 
        hand.remove(i);

        playerHand.revalidate();
        playerHand.repaint();

        revalidate();
        repaint(); 
    }

    private void chooseColorScreen() {
        board.removeAll();

        try{
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e){
           e.printStackTrace(); 
        }

        red = new JButton("Red");
        red.setPreferredSize(red.getMaximumSize());
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

        board.add(red, gbc);
        board.add(blue, gbc);
        board.add(green, gbc);
        board.add(yellow, gbc);

        board.revalidate();
        board.repaint();

        revalidate();
        repaint();
    }

    private void returnToBoard() {
        board.removeAll();

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

    private void enterSpectateMode() {
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

    private void finalScreen() {
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
        // unoPanel.setVisible(true);
        // unoPanel.setLocation(x, y);

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
                        String card = strData.substring(Server.PLAY_CARD.length());
                        removeCard(card);
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
                        place = Integer.valueOf(strData.substring(Server.WON.length()));
                        enterSpectateMode();
                    }

                    else if (strData.contains(Server.PLAYER_WON)) {
                        // Do something
                    }

                    else if (strData.contains(Server.DREW_CARDS)) {
                        String[] playerHandSize = strData.substring(Server.DREW_CARDS.length()).split(" ");
                        players.replace(playerHandSize[0], Integer.valueOf(playerHandSize[1]));
                        updateTable();
                    }

                    else if (strData.contains(Server.END_GAME)) {
                        finalScreen();
                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nameField) {
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

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class GUIHandler extends JFrame implements ActionListener {

    private static final int width  = 800;
    private static final int height = 400;

    private Taskbar taskbar;

    private JMenuBar menuBar;
    private JMenu help;
    private JMenuItem rules;
    private JTextField nameField;
    private JTextArea errorMessage;
    private JLabel invalidName, enterNamePrompt, waiting;
    private JButton startGame;
    private JButton deck;
    private JLabel discardPile;

    private JPanel mainPanel, playerInfo, board, playerHand;
    private JTable playerTable;
    private LinkedList<JButton> hand;
    private LinkedList<String> strHand;

    private LinkedHashMap<String, Integer> players;
    
    private DataOutputStream out;

    public GUIHandler(DataOutputStream out) {
        super("Uno");
        setBounds(0, 0, width, height);

        this.out = out;

        taskbar = Taskbar.getTaskbar();

        // Create the Icon Image for this application
        ImageIcon unoLogo = new ImageIcon(getClass().getResource("/images/uno_logo.png"));
        setIconImage(unoLogo.getImage());

        mainPanel = new JPanel(new BorderLayout());

        board = new JPanel(new GridBagLayout());
        enterNamePrompt = new JLabel("Please enter your name:");
        nameField = new JTextField(10);
        invalidName = new JLabel();
        invalidName.setForeground(Color.RED);
        invalidName.setFont(new Font(invalidName.getFont().getName(), Font.PLAIN, 24));

        nameField.addActionListener(this);
        board.add(enterNamePrompt);
        board.add(nameField);

        playerInfo = new JPanel(new FlowLayout());
        playerHand = new JPanel(new FlowLayout());

        playerHand.add(invalidName);

        mainPanel.add(playerInfo, BorderLayout.NORTH);
        mainPanel.add(board, BorderLayout.CENTER);
        mainPanel.add(playerHand, BorderLayout.SOUTH);
        add(mainPanel);

        players = new LinkedHashMap<String, Integer>();

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void setIconImage(Image image) {
        super.setIconImage(image);

        try {
            taskbar.setIconImage(image);
        } catch (UnsupportedOperationException e) {
            System.out.println("The os does not support: 'taskbar.setIconImage'");
        } catch (SecurityException e) {
            System.out.println("There was a security exception for: 'taskbar.setIconImage'");
        }
    }
    

    private void waitingScreen() {
        board.removeAll();
        playerHand.removeAll();

        menuBar = new JMenuBar();
        help = new JMenu("Help");
        rules = new JMenuItem("Rules");
        rules.addActionListener(this);

        help.add(rules);
        menuBar.add(help);

        updateTable();

        waiting = new JLabel("Waiting for the game to start...");
        waiting.setFont(new Font(waiting.getFont().getName(), Font.PLAIN, 32));
        board.add(waiting);
        
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
        playerTable.setPreferredScrollableViewportSize(new Dimension(width - 10 * 2, playerTable.getMinimumSize().height));
        playerTable.setFillsViewportHeight(true);
        playerTable.setOpaque(false);
        playerTable.setEnabled(false);
        playerTable.setGridColor(Color.BLACK);
        playerTable.getTableHeader().setBackground(Color.LIGHT_GRAY);

        playerInfo.add(new JScrollPane(playerTable));
        playerInfo.revalidate();
        playerInfo.repaint();

        revalidate();
        repaint();
    }

    private void createBoard(String firstCard) {
        board.removeAll();

        ImageIcon faceDown = new ImageIcon(getClass().getResource("/images/card_face_down.png"));
        deck = new JButton(faceDown);
        board.add(deck);  
        deck.addActionListener(this);
        
        ImageIcon lastCard = new ImageIcon(getClass().getResource("/images/" + firstCard + ".png"));
        discardPile = new JLabel(lastCard);
        board.add(discardPile);

        board.revalidate();
        board.repaint();

        revalidate();
        repaint();
    }

    private void makeLeader() {
        board.removeAll();

        startGame = new JButton("Start Game");
        startGame.addActionListener(this);
        board.add(startGame);

        board.revalidate();
        board.repaint();
    }
    
    private void createHand() {
        playerHand.removeAll();

        for (JButton card : hand) {
            playerHand.add(card);
            card.addActionListener(this);
        }

        playerHand.revalidate();
        playerHand.repaint();

        revalidate();
        repaint();
    } 

    private void updateDiscardPile(String card) {
        discardPile.setIcon(new ImageIcon(getClass().getResource("/images/" + card + ".png")));

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

    public void decode(String allStrData) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
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
                        String[] strPlayerHand = strData.substring(Server.INIT_PLAYER_HAND.length()).split(" ");
                        for (String card : strPlayerHand) {
                            ImageIcon icon = new ImageIcon(getClass().getResource("/images/" + card.toString() + ".png"));
                            hand.add(new JButton(icon));
                            strHand.add(card.toString()); 
                        }
                        createHand();
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
                        String card = strData.substring(Server.SOMEBODY_PLAYED_CARD.length());
                        updateDiscardPile(card);

                    } 

                    else if (strData.contains(Server.DRAW_CARDS)) {
                        String[] cardsToAdd = strData.substring(Server.DRAW_CARDS.length()).split(" ");
                        for (String strCard : cardsToAdd) {
                            ImageIcon icon = new ImageIcon(getClass().getResource("/images/" + strCard.toString() + ".png"));
                            hand.add(new JButton(icon));
                            strHand.add(strCard.toString()); 
                        }

                        createHand();
                    }
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nameField) {
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
            write(Server.PLAY_CARD + strHand.get(i));
        }

        else if (e.getSource() == rules) {
            JOptionPane.showMessageDialog(this, "message", "title", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void write(String data) {
        try {
            out.writeUTF(data);
            out.flush();
        } catch (IOException ex) {
            killOutput();
        }
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

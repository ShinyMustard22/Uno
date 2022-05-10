import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GUIHandler extends JFrame implements ActionListener {

    private static final int width  = 800;
    private static final int height = 600;

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

    private LinkedHashMap<String, Integer> players;
    
    private DataOutputStream out;

    public GUIHandler(DataOutputStream out) {
        super("Uno");
        setBounds(0, 0, width, height);

        this.out = out;

        taskbar = Taskbar.getTaskbar();

        // Create the Icon Image for this application
        ImageIcon unoLogo = new ImageIcon(getClass().getResource("/images/logo.png"));
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

        help.add(rules);
        menuBar.add(help);

        updateTable();

        // waiting = new JLabel("Waiting for the game to start...");
        // waiting.setFont(new Font(waiting.getFont().getName(), Font.PLAIN, 32));
        // board.add(waiting);

        startGame = new JButton("Start Game");
        startGame.addActionListener(this);
        board.add(startGame);

        // ImageIcon redOne = new ImageIcon(getClass().getResource("/images/logo.png"));
        // board.add(new JLabel(redOne));

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

    private void createBoard() {
        board.removeAll();

        ImageIcon back = new ImageIcon(getClass().getResource("/images/back.png"));
        deck = new JButton(back);
        board.add(deck);  
        deck.addActionListener(this);
        
        ImageIcon lastCard = new ImageIcon(getClass().getResource("/images/back.png"));
        discardPile = new JLabel(lastCard);
        board.add(discardPile);

        board.revalidate();
        board.repaint();

        revalidate();
        repaint();
        
        write(Server.PLAYER_HAND);
    }
    
    private void createHand()  {
        String[] cards = new String[108];
        
    //     try {
    //         File file = new File("allCardsFilePaths.txt"); 
    //         Scanner scanner = new Scanner(file);
            
    //         for (String c: cards) {
    //             c = scanner.nextLine(); 
    //         }
    //     } catch (Exception e) {
    //         System.out.println("could not find"); 
    //     }
        
    //    //System.out.println("hi");
    //     playerHand.revalidate();
    //     playerHand.repaint();

    //     for (int i = 0; i < 7; i++) {
    //         hand.set(i, new JButton(new ImageIcon(cards[i])));
    //         playerHand.add(hand.get(i)); 
    //         hand.get(i).addActionListener(this);
    //     }
        ImageIcon test = new ImageIcon(getClass().getResource("/images/uno_card_blue0.png"));
        JButton b = new JButton(test);
        playerHand.add(b);  
        b.addActionListener(this);

        revalidate();
        repaint();
    }

    public void decode(String data) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (data.contains(Server.INIT_PLAYER_LIST)) {
                    String[] playerList = data.substring(Server.INIT_PLAYER_LIST.length()).split(" ");
                    for (String player : playerList) {
                        players.put(player, Player.STARTING_HAND_SIZE);
                    }
                    waitingScreen();
                }
        
                else if (data.contains(Server.ADD_PLAYER)) {
                    players.put(data.substring(Server.ADD_PLAYER.length()), Player.STARTING_HAND_SIZE);
                    updateTable();
                }

                else if (data.contains(Server.REMOVE_PLAYER)) {
                    players.remove(data.substring(Server.REMOVE_PLAYER.length()));
                    updateTable();
                }

                else if (data.contains(Server.INVALID_USERNAME)) {
                    invalidName.setText("This username is invalid...");
                }

                else if (data.contains(Server.TAKEN_USERNAME)) {
                    invalidName.setText("This username has been taken...");
                }

                else if (data.contains(Server.GAME_STARTED)) {
                    createBoard();
                }

                else if (data.contains(Server.PLAYER_HAND)) {
                    hand = new LinkedList<JButton>();
                    String[] strPlayerHand = data.substring(Server.PLAYER_HAND.length()).split(" ");
                    for (String card : strPlayerHand) {
                        // Replace path with "/images/card.toString()" + ".png"
                        ImageIcon icon = new ImageIcon(getClass().getResource("/images/back.png"));
                        hand.add(new JButton(icon));
                    }
                    createHand();
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

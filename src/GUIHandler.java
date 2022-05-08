import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.DataOutputStream;
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
    private JLabel enterNamePrompt, waiting;
    private JButton startGame;

    private JPanel playerInfo, board, playerHand;
    private JTable playerTable;

    private LinkedHashMap<String, Integer> players;

    private DataOutputStream out;

    public GUIHandler() {

    }

    public GUIHandler(DataOutputStream out) {
        super("Uno");
        setBounds(0, 0, width, height);

        this.out = out;

        taskbar = Taskbar.getTaskbar();

        // Create the Icon Image for this application
        ImageIcon unoLogo = new ImageIcon(getClass().getResource("/images/unologo.png"));
        setIconImage(unoLogo.getImage());

        board = new JPanel(new GridBagLayout());
        enterNamePrompt = new JLabel("Please enter your name:");
        nameField = new JTextField(10);
        nameField.addActionListener(this);
        board.add(enterNamePrompt);
        board.add(nameField);
        add(board);

        players = new LinkedHashMap<String, Integer>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
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

    private void createBoard() {
        board.removeAll();

        menuBar = new JMenuBar();
        help = new JMenu("Help");
        rules = new JMenuItem("Rules");

        help.add(rules);
        menuBar.add(help);

        playerInfo = new JPanel(new FlowLayout());
        playerHand = new JPanel(new FlowLayout());

        if (players.size() > 1) {
            waiting = new JLabel("Waiting for the game to start...");
            waiting.setFont(new Font(waiting.getFont().getName(), Font.PLAIN, 32));
            board.add(waiting);
        }

        else {
            startGame = new JButton("Start the game...");
            startGame.setFont(new Font(startGame.getFont().getName(), Font.PLAIN, 32));
            startGame.addActionListener(this);
            board.add(startGame);
        }

        board.revalidate();
        board.repaint();

        revalidate();
        repaint();

        setJMenuBar(menuBar);
    }

    private void addPlayer(String name) {

    }

    public void decode(String data) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (data.contains("-playerList: ")) {
                    createBoard();
                }
        
                else if (data.contains("-addPlayer: ")) {
                    addPlayer(data);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nameField) {
            write(nameField.getText());
        }

        else if (e.getSource() == startGame) {
            // Do something
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

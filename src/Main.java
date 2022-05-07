import java.io.*;
import java.net.Socket;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Main extends JFrame implements ActionListener {

    private static final int width  = 800;
    private static final int height = 600;
    private static final int margin = 20;

    private static Main client;
    private Taskbar taskbar;

    private JMenuBar menuBar;
    private JMenu help;
    private JMenuItem rules;
    private JTextField nameField;
    private JTextArea errorMessage;
    private JLabel enterNamePrompt, waiting;
    private JButton startGame;

    private JPanel mainPanel, playerInfo, board, playerHand;
    private JTable playerTable;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private LinkedHashMap<String, Integer> players;

    public Main() {
        super("Uno");
        setBounds(0, 0, width, height);

        taskbar = Taskbar.getTaskbar();

        // Create the Icon Image for this application
        ImageIcon unoLogo = new ImageIcon(getClass().getResource("/images/unologo.png"));
        setIconImage(unoLogo.getImage());

        try {
            socket = new Socket(Server.IP_ADDRESS, Server.PORT_NUM);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            mainPanel = new JPanel(new GridBagLayout());
            enterNamePrompt = new JLabel("Please enter your name:");
            nameField = new JTextField(10);
            nameField.addActionListener(this);
            mainPanel.add(enterNamePrompt);
            mainPanel.add(nameField);
            add(mainPanel);
            players = new LinkedHashMap<String, Integer>();
        } catch (IOException ex) {
            killEverything(socket, in, out);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }


    private void createBoard() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                menuBar = new JMenuBar();
                help = new JMenu("Help");
                rules = new JMenuItem("Rules");

                help.add(rules);
                menuBar.add(help);

                mainPanel.setLayout(new BorderLayout());
                playerInfo = new JPanel(new FlowLayout());
                board = new JPanel(new GridBagLayout());
                playerHand = new JPanel(new FlowLayout());

                String[] columnNames = new String[players.size()];
                String[][] data = new String[1][columnNames.length];
                Iterator<String> playerNames = players.keySet().iterator();
                int i = 0;
                while(playerNames.hasNext()) {
                    String playerName = playerNames.next();
                    columnNames[i] = playerName;
                    data[0][i] = players.get(playerName) + "";
                    i++;
                }

                playerTable = new JTable(data, columnNames);
                playerTable.setEnabled(false);

                playerTable.setGridColor(Color.BLACK);
                playerTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
                playerTable.getTableHeader().setReorderingAllowed(false);

                playerTable.setPreferredScrollableViewportSize(
                    new Dimension(width - margin, (int)playerTable.getMinimumSize().getHeight()));
                playerTable.setFillsViewportHeight(true);
                playerInfo.add(new JScrollPane(playerTable));

                if (players.size() > 1) {
                    waiting = new JLabel("Waiting for the game to start...");
                    waiting.setFont(new Font(waiting.getFont().getName(), Font.PLAIN, 32));
                    board.add(waiting);
                }

                else {
                    startGame = new JButton("Start the game...");
                    startGame.setFont(new Font(startGame.getFont().getName(), Font.PLAIN, 32));
                    startGame.addActionListener(client);
                    board.add(startGame);
                }

                mainPanel.add(playerInfo, BorderLayout.NORTH);
                mainPanel.add(board, BorderLayout.CENTER);
                mainPanel.add(playerHand, BorderLayout.SOUTH);
                mainPanel.revalidate();
                mainPanel.repaint();

                setJMenuBar(menuBar);
                add(mainPanel);
                validate();
                repaint();
                return null;
            }
            
        }.execute();
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

    private void killEverything(Socket socket, DataInputStream in, DataOutputStream out) {
        try {
            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {

        }

        if (mainPanel == null) {
            mainPanel = new JPanel();
        }

        else {
            remove(mainPanel);
        }

        mainPanel.setLayout(new GridBagLayout());
        errorMessage = new JTextArea("Error");
        errorMessage.setEditable(false);
        errorMessage.setOpaque(false);
        mainPanel.add(errorMessage);
        add(mainPanel);
        validate();
    }

    public static void main(String[] args) {
        client = new Main();
        client.listenForMessages();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nameField) {
            String name = nameField.getText();
            write(name);
            mainPanel.removeAll();
            mainPanel.revalidate();
            mainPanel.repaint();
            revalidate();
            repaint();
        }

        else if (e.getSource() == startGame) {
            // Do something
        }
    }

    private String read() {
        try {
            String message = in.readUTF();
            while (message.isEmpty()) {
                message = in.readUTF();
            }

            return message;
        } catch (IOException ex) {
            killEverything(socket, in, out);
            return null;
        }
    }

    private void write(String data) {
        try {
            out.writeUTF(data);
            out.flush();
        } catch (IOException ex) {
            killEverything(socket, in, out);
        }
    }

    private void decode(String data) {
        if (data.contains("-playerList: ")) {
            String strPlayers = data.substring(13);
            String[] arrPlayers = strPlayers.split(" ");
            for (int i = 0; i < arrPlayers.length; i++) {
                players.put(arrPlayers[i], Player.STARTING_HAND_SIZE);
            }
            createBoard();
        }
    }

    private void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data;

                while(socket.isConnected()) {
                    data = read();
                    decode(data);
                }
            }     
        }).start();
    }
}

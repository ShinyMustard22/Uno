import java.io.*;
import java.net.Socket;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Main extends JFrame implements ActionListener {

    private static final int width = 800;
    private static final int height = 600;
    private static final int margin = 20;

    private Taskbar taskbar;

    private JMenuBar menuBar;
    private JMenu help;
    private JMenuItem rules;
    private JTextField nameField;
    private JTextArea errorMessage;
      
    private JPanel mainPanel, playerInfo, board, playerHand;
    private JTable playerTable;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

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
            mainPanel.add(new JLabel("Please enter your name: "));
            nameField = new JTextField(10);
            nameField.addActionListener(this);
            mainPanel.add(nameField);
            add(mainPanel);
        } catch (IOException ex) {
            killEverything(socket, in, out);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); 
        setVisible(true); 
    }

    private void createBoard(java.util.List<Player> players) {
        // TESTING
        players = new LinkedList<Player>();
        players.add(new Player("bruh", new LinkedList<>()));
        players.add(new Player("cringe", new LinkedList<>()));
        players.add(new Player("bruh", new LinkedList<>()));
        players.add(new Player("cringe", new LinkedList<>()));
        players.add(new Player("bruh", new LinkedList<>()));
        players.add(new Player("cringe", new LinkedList<>()));
        players.add(new Player("bruh", new LinkedList<>()));
        players.add(new Player("cringe", new LinkedList<>()));
        players.add(new Player("bruh", new LinkedList<>()));
        players.add(new Player("cringe", new LinkedList<>()));

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
        for (int i = 0; i < columnNames.length; i++) {
            Player player = players.get(i);
            columnNames[i] = player.getUsername();
            data[0][i] = player.getHandSize() + "";
        }

        playerTable = new JTable(data, columnNames);
        playerTable.setEnabled(false);

        playerTable.setGridColor(Color.BLACK);
        playerTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        playerTable.getTableHeader().setReorderingAllowed(false);

        playerTable.setPreferredScrollableViewportSize(new Dimension(width - margin, 
            (int) playerTable.getMinimumSize().getHeight()));
        playerTable.setFillsViewportHeight(true);
        playerInfo.add(new JScrollPane(playerTable));

        mainPanel.add(playerInfo, BorderLayout.NORTH);
        mainPanel.add(board, BorderLayout.CENTER);
        mainPanel.add(playerHand, BorderLayout.SOUTH);

        setJMenuBar(menuBar);
        add(mainPanel);
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
            removeAll();
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
        Main client = new Main();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nameField) {
            String name = nameField.getText();
            write(name);
            mainPanel.removeAll();
            remove(mainPanel);
            createBoard(null);
            validate();
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
}
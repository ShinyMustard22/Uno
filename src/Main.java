import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Main extends JFrame implements ActionListener {

    private static final int width = 800;
    private static final int height = 600;

    private Taskbar taskbar;

    private JMenuBar menuBar;
    private JMenu help;
    private JMenuItem rules;
    private JTextField nameField;
    private JTextArea errorMessage;
      
    private JPanel mainPanel, playerInfo, board, playerHand;

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

        menuBar = new JMenuBar();
        help = new JMenu("Help");
        rules = new JMenuItem("Rules");
        
        help.add(rules);
        menuBar.add(help);

        mainPanel.setLayout(new BorderLayout());
        playerInfo = new JPanel(new FlowLayout());
        board = new JPanel(new GridBagLayout());
        playerHand = new JPanel(new FlowLayout());

        String[] columnNames = {"Names", "Number of Cards"};
        String[][] data = new String[players.size()][columnNames.length];
        for (int row = 0; row < data.length; row++) {
            data[row][0] = players.get(row).getUsername();
            data[row][1] = players.get(row).getHandSize() + "";
        }

        JTable playerTable = new JTable(data, columnNames);
        playerTable.setOpaque(false);
        playerTable.setEnabled(false);
        playerInfo.add(playerTable);

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
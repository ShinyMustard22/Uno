import java.io.*;
import java.net.Socket;
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
      
    private JPanel mainPanel, playerInfo, board, playerHand;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Main() {
        super("Uno"); 
        setBounds(0, 0, width, height);

        taskbar = Taskbar.getTaskbar();

        menuBar = new JMenuBar();
        help = new JMenu("Help");
        rules = new JMenuItem("Rules");
        
        help.add(rules);
        menuBar.add(help);

        mainPanel = new JPanel(new BorderLayout());
        playerInfo = new JPanel(new FlowLayout());
        board = new JPanel(new FlowLayout());
        playerHand = new JPanel(new FlowLayout());

        playerInfo.add(new JLabel("this works"));
        board.add(new JLabel("deck")); 
        board.add(new JLabel("discard"));
        playerHand.add(new JLabel("cringe"));

        mainPanel.add(playerInfo, BorderLayout.NORTH);
        mainPanel.add(board, BorderLayout.CENTER);
        mainPanel.add(playerHand, BorderLayout.SOUTH);

        // Create the Icon Image for this application
        ImageIcon unoLogo = new ImageIcon(getClass().getResource("/images/unologo.png"));
        setIconImage(unoLogo.getImage());

        try {
            socket = new Socket(Server.IP_ADDRESS, Server.PORT_NUM);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException ex) {
            killEverything(socket, in, out);
        }

        setJMenuBar(menuBar);
        add(mainPanel);

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
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main client = new Main();
        client.listenForMessages();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    private void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;

                while(socket.isConnected()) {
                    try {
                        message = in.readUTF();
                        while (message.isEmpty()) {
                            message = in.readUTF();
                        }
                        System.out.println(message);
                    } catch (IOException ex) {

                    }
                }
            }     
        }).start();
    }
}
import java.io.*;
import java.net.Socket;
import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {

    private static final int width = 800;
    private static final int height = 600;

    private Taskbar taskbar;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Main() {
        super("Uno"); 
        setBounds(0, 0, width, height);
        add(new JButton("HI"));
        
        taskbar = Taskbar.getTaskbar();

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
        new Main();
    }
}
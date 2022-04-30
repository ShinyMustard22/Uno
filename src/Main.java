import java.io.*;
import java.net.Socket;
import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {

    private static final int width = 800;
    private static final int height = 600;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Main() {
        super("Uno"); 
        setBounds(0, 0, width, height);
        /*Image icon = Toolkit.getDefaultToolkit().getImage("Uno\\src\\unologo.png");  
        setIconImage(icon);*/

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
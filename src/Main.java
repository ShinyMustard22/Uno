import java.io.*;
import java.net.Socket;

/**
 * Main class which acts as a client for the server. Connects to that server,
 * and then starts the guided user interface.
 * 
 * @author Ritam Chakraborty
 * @version May 23, 2022
 */
public class Main {
    private GUIHandler gui;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    /**
     * Connects its socket to the server and creates an input and output
     * mechanism to it. Starts the GUI.
     */
    public Main() {
        try {
            socket = new Socket(Server.IP_ADDRESS, Server.PORT_NUM);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            gui = new GUIHandler(out);
        } catch (IOException ex) {
            killEverything();
        }
    }

    private void killEverything() {
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

        if (gui == null) {
            gui = new GUIHandler(null);
        }

        else {
            gui.errorScreen();
        }
    }

    /**
     * Creates a new instance of this client. Begins a thread to start
     * listening for incoming messages from the server.
     * @param args
     */
    public static void main(String[] args) {
        Main client = new Main();
        client.listenForMessages();
    }

    private String read() {
        try {
            String message = in.readUTF();
            while (message.isEmpty()) {
                message = in.readUTF();
            }

            return message;
        } catch (IOException ex) {
            killEverything();
            return null;
        }
    }

    private void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data;
                while(socket != null && !socket.isClosed()) {
                    data = read();
                    gui.decode(data);
                }
            }     
        }).start();
    }
}
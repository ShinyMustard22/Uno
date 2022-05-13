import java.io.*;
import java.net.Socket;

public class Main {
    private GUIHandler gui;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

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

        System.out.println("ERROR: Communication with the server was interrupted...");
        System.exit(1);
    }
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

    private void decode(String data) {
        gui.decode(data);
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
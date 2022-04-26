import java.io.*;
import java.net.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String input;

    public Server() {
        try {
            serverSocket = new ServerSocket(ServerInfo.PORT_NUM);
            socket = serverSocket.accept();
            in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(
                new BufferedOutputStream(socket.getOutputStream()));

            while (true) {
                input = ServerInfo.readInput(in);
                // Do something with the input
            }
        } catch (IOException ex) {
            
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}

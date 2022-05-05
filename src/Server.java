import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int PORT_NUM = 6969;
    public static final String IP_ADDRESS = "localhost";

    private ServerSocket serverSocket;
    private Socket socket;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT_NUM);

            while (!serverSocket.isClosed()) {
                socket = serverSocket.accept();
                System.out.println("A player has joined!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException ex) {
            killServer();
        }
    }

    private void killServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}

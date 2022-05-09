import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int PORT_NUM = 6969;
    public static final String IP_ADDRESS = "localhost";

    public static final String INIT_PLAYER_LIST = "playerList: ";
    public static final String ADD_PLAYER = "addPlayer: ";
    public static final String REMOVE_PLAYER = "removePlayer: ";
    public static final String INVALID_USERNAME = "invalidName: ";
    public static final String TAKEN_USERNAME = "takenName: ";

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

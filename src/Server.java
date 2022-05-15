import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int PORT_NUM = 6969;
    public static final String IP_ADDRESS = "localhost";

    public static final String NAME = "username: ";
    public static final String INIT_PLAYER_LIST = "playerList: ";
    public static final String ADD_PLAYER = "addPlayer: ";
    public static final String REMOVE_PLAYER = "removePlayer: ";
    public static final String INVALID_USERNAME = "invalidName: ";
    public static final String TAKEN_USERNAME = "takenName: ";
    public static final String SET_LEADER = "setLeader: ";
    public static final String GAME_STARTED = "startGame: ";
    public static final String INIT_PLAYER_HAND = "hand: ";
    public static final String AM_LEADER = "amLeader?: ";
    public static final String FIRST_CARD = "firstCard: ";
    public static final String PLAY_CARD = "playCard: ";
    public static final String SOMEBODY_PLAYED_CARD = "playerPlayedCard: ";
    public static final String ASK_TO_DRAW = "askToDraw: ";
    public static final String DRAW_CARDS = "drawCards: ";
    public static final String CHOOSE_COLOR = "chooseColor: ";
    public static final String ERROR = "error: ";

    private ServerSocket serverSocket;
    private Socket socket;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT_NUM);

            while (!serverSocket.isClosed()) {
                socket = serverSocket.accept();
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

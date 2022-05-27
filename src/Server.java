import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server class that listens for clients to join. Once they do their input
 * and output are connected a new instance of a client handler that conducts
 * the actual operations regarding the game state and that client.
 * 
 * This server client setup was inspired by WittCode on YouTube.
 * Subscribe to him at: https://www.youtube.com/c/WittCode
 * 
 * @author Ritam Chakraborty
 * @version May 23, 2022
 */
public class Server {

    /**
     * Port number of the server
     */
    public static final int PORT_NUM = 6969;
    /**
     * IP Address of the server
     */
    public static final String IP_ADDRESS = "localhost";

    /**
     * Command to send and recieve username
     */
    public static final String NAME = "username: ";
    /**
     * Command to send the inital list of players to the clients
     */
    public static final String INIT_PLAYER_LIST = "playerList: ";
    /**
     * Command to add a player to the game
     */
    public static final String ADD_PLAYER = "addPlayer: ";
    /**
     * Command to remove a player from the game
     */
    public static final String REMOVE_PLAYER = "removePlayer: ";
    /**
     * Command to send to client if they have an invalid username
     */
    public static final String INVALID_USERNAME = "invalidName: ";
    /**
     * Command to send to client if their username has been taken
     */
    public static final String TAKEN_USERNAME = "takenName: ";
    /**
     * Command to set the new leader of the lobby
     */
    public static final String SET_LEADER = "setLeader: ";
    /**
     * Command client sends to server to start game
     */
    public static final String GAME_STARTED = "startGame: ";
    /**
     * Command server sends to client about what there initial hand is
     */
    public static final String INIT_PLAYER_HAND = "hand: ";
    /**
     * Command client sends to server about whether they are the leader
     */
    public static final String AM_LEADER = "amLeader?: ";
    /**
     * Command server sends to client about the first card on the discard pile
     */
    public static final String FIRST_CARD = "firstCard: ";
    /**
     * Command client sends to server to play a card that was chosen
     */
    public static final String PLAY_CARD = "playCard: ";
    /**
     * Command server sends to client for when someone has played a card
     */
    public static final String SOMEBODY_PLAYED_CARD = "playerPlayedCard: ";
    /**
     * Command server sends to client when someone draws cards
     */
    public static final String DREW_CARDS = "drewXCards: ";
    /**
     * Command client sends to server when they want to draw cards
     */
    public static final String ASK_TO_DRAW = "askToDraw: ";
    /**
     * Command server sends to client when they have to draw cards
     */
    public static final String DRAW_CARDS = "drawCards: ";
    /**
     * Command server sends to client when they chose to play a wild card, and have to choose a color
     */
    public static final String CHOOSE_COLOR = "chooseColor: ";
    /**
     * Command server sends to client when there is an illegal move or "error"
     */
    public static final String ERROR = "error: ";
    /**
     * Command server sends to client when the game is in "uno mode"
     * Client sends this to server when "uno" is said/clicked
     */
    public static final String UNO_TIME = "unoTime: ";
    /**
     * Command server sends to client when "uno mode" is exited
     */
    public static final String END_UNO_TIME = "endUnoTime: ";
    /**
     * Command server sends to client when a player has won
     */
    public static final String PLAYER_WON = "playerWon: ";
    /**
     * Command server sends to client that has won
     */
    public static final String WON = "youWon: ";
    /**
     * Command server sends to client when the game is over
     */
    public static final String END_GAME = "endGame: ";
    /**
     * Command client sends to server when it wants to cancel a decision
     */
    public static final String CANCEL_OPERATION = "cancelOperation: ";
    /**
     * Command server sends to client when its time to set a new players turn
     */
    public static final String SET_TURN = "setTurn: ";
    /**
     * Command server sends to client to tell what "color" the game is on at that moment
     */
    public static final String SET_COLOR = "colorIs: ";
    /**
     * Command server sends to client to set a new card as the top of the discard pile
     * for when the deck runs out.
     */
    public static final String NEW_DISCARD_PILE = "newDiscardPile: ";
    /**
     * Command server sends to client if client wants to play, but game already started
     */
    public static final String GAME_ALREADY_BEGAN = "gameAlreadyBegan: ";

    private ServerSocket serverSocket;
    private Socket socket;

    /**
     * Starts a new thread to handle a client when it connects to this server
     * socket's port number.
     */
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

    /**
     * Main method to start server which begins listening for clients
     * @param args not used
     */
    public static void main(String[] args) {
        new Server();
    }
}
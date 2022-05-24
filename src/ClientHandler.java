import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import cards.Card;
import cards.ColorCard;
import cards.WildCard;

/**
 * A class that is able to talk to the client using input and output streams.
 * Holds a static reference to the game state, and to other ClientHandler
 * instances which handle other clients. Implements the runnable interface
 * which allows it to listen for messsages on a seperate thread. It decodes
 * those messages from the client and then acts based on the conent of the
 * message regarding the clients and the games state.
 * 
 * @author Ritam Chakraborty
 * @version May 23, 2022
 */
public class ClientHandler implements Runnable {
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
    private static GameState board = new GameState();
    private static AtomicBoolean isUno = new AtomicBoolean(false);

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    
    private String username;

    /**
     * Establishes the connection between the client and this handler
     * @param socket the socket that connects to the server that is connected
     * to the client
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException ex) {
            killEverything(socket, in, out);
        }
    }

    private boolean alreadyUsedName() {
        for (ClientHandler clientHandler : clientHandlers) {
            if (username.equals(clientHandler.username)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sends a list of cards to the client with a specific username. Notifies
     * other clients as well.
     * @param username username of the client that has to recieve cards
     * @param newCards the list of new cards the client will add to their hand
     */
    public static void sendCards(String username, List<Card> newCards) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (username.equals(clientHandler.username)) {
                clientHandler.write(Server.DRAW_CARDS + Card.listToString(newCards));
                for (ClientHandler clientHandler2 : clientHandlers) {
                    clientHandler2.write(Server.DREW_CARDS + clientHandler.username + " " + 
                        board.getPlayer(clientHandler.username).getHandSize());
                }

                return;
            }
        }
    }

    /**
     * Notifies client of a new discard pile.
     * @param topCard the card at the top of the new discard pile
     */
    public static void newDiscardPile(Card topCard) {
        String color;

        if (topCard instanceof ColorCard) {
            ColorCard colorCard = (ColorCard) topCard;
            color = colorCard.getColor();
        }

        else {
            WildCard wildCard = (WildCard) topCard;
            color = wildCard.getColor();
        }

        broadcastToAll(Server.NEW_DISCARD_PILE + topCard.toString() + "\n" + Server.SET_COLOR + color);
    }

    private int indexOfPlayersTurn() {
        for (int i = 0; i < clientHandlers.size(); i++) {
            if (board.getPlayer(clientHandlers.get(i).username) == null) {
                continue;
            }

            else if (board.getPlayer(clientHandlers.get(i).username).equals(board.getCurrentPlayer())) {
                return i;
            }
        }

        return -1;
    }

    private String read() {
        try {
            String message = in.readUTF();
            while (message.isEmpty()) {
                message = in.readUTF();
            }

            return message;
        } catch (IOException ex) {
            killEverything(socket, in, out);
            return null;
        }
    }

    private void write(String data) {
        try {
            out.writeUTF(data);
            out.flush();
        } catch (IOException ex) {
            killEverything(socket, in, out);
        }
    }

    private static void broadcastToAll(String data) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.write(data);
        }
    }

    private void broadcastToOthers(String data) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != this) {
                clientHandler.write(data);
            }
        }
    }

    private void killEverything(Socket socket, DataInputStream in, DataOutputStream out) {
        board.removePlayer(username);
        broadcastToOthers(Server.REMOVE_PLAYER + username);

        if (clientHandlers.size() == 1) {
            board = new GameState();
        }

        else if (!board.gameHasStarted() && clientHandlers.indexOf(this) == 0) {
            clientHandlers.get(1).write(Server.SET_LEADER);
        }

        clientHandlers.remove(this);

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

    private void decode(String data) {
        if (data == null) {
            return;
        }

        if (data.contains(Server.NAME)) {
            username = data.substring(Server.NAME.length());
            while (username.contains(" ") || alreadyUsedName()) {
                if (username.contains(" ")) {
                    write(Server.INVALID_USERNAME);
                }
                
                else {
                    write(Server.TAKEN_USERNAME);
                }
                
                username = read();
                username = username.substring(Server.NAME.length());
            }

            if (board.addPlayer(username)) {
                boolean isLeader = clientHandlers.size() == 0;

                write(Server.INIT_PLAYER_LIST + board.getPlayerList() + "\n" +
                    Server.AM_LEADER + isLeader);

                broadcastToOthers(Server.ADD_PLAYER + username);
                clientHandlers.add(this);
            }
        }

        else if (data.contains(Server.GAME_STARTED)) {
            if (board.getTotalPlayers() < 2) {
                write(Server.ERROR);
                return;
            }

            board.startGame();
            ColorCard firstCard = (ColorCard) board.getLastPlayedCard();

            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.write(Server.FIRST_CARD + firstCard.toString() + "\n" +
                Server.INIT_PLAYER_HAND + board.getPlayer(clientHandler.username).getCardList() + "\n" +
                Server.SET_COLOR + firstCard.getColor() + "\n" + Server.SET_TURN + indexOfPlayersTurn());
            }
        }

        else if (isUno.get() && data.contains(Server.UNO_TIME)) {
            board.saidUno(username);
            isUno.set(false);
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.write(Server.END_UNO_TIME);
            }
        }

        else if (!board.getPlayer(username).equals(board.getCurrentPlayer())) {
            write(Server.ERROR);
            return;
        }

        else if (data.contains(Server.PLAY_CARD)) {
            String[] cardData = data.substring(Server.PLAY_CARD.length()).split(" ");
            String strCard = cardData[0];
            int index = Integer.valueOf(cardData[1]);

            Card card = Card.decode(strCard);

            if (card instanceof WildCard) {
                WildCard wildCard = (WildCard) card;

                write(Server.CHOOSE_COLOR);
                String color = read();
                if (color.equals(Server.CANCEL_OPERATION)) {
                    return;
                }

                wildCard.setColor(color);
            }

            if (board.play(card, index)) {
                String color;
                card = board.getLastPlayedCard();

                if (card instanceof ColorCard) {
                    ColorCard colorCard = (ColorCard) card;
                    color = colorCard.getColor();
                }

                else {
                    WildCard wildCard = (WildCard) card;
                    color = wildCard.getColor();
                }

                write(Server.PLAY_CARD + index);
                broadcastToAll(Server.SOMEBODY_PLAYED_CARD + username + " " + strCard + "\n" + Server.SET_COLOR + color);

                if (board.getPlayer(username).getHandSize() == 1) {
                    isUno.set(true);
                    for (ClientHandler clientHandler : clientHandlers) {
                        if (board.getPlayer(clientHandler.username) != null) {
                            clientHandler.write(Server.UNO_TIME);
                        }
                    }
                }

                else if (board.getPlayer(username).getHandSize() == 0) {
                    board.playerWon(username);
                    write(Server.WON + board.getPlaceOfPlayer(username) + "\n" + Server.REMOVE_PLAYER + username);
                    broadcastToOthers(Server.PLAYER_WON + "\n" + Server.REMOVE_PLAYER + username);
                }

                if (board.getTotalPlayers() < 2) {
                    board.endGame();
                    for (ClientHandler clientHandler : clientHandlers) {
                        int place = board.getPlaceOfPlayer(clientHandler.username);
                        clientHandler.write(Server.END_GAME + place);
                    }
                    board = new GameState();
                }

                else {
                    int playerTurnIndex = indexOfPlayersTurn();
                    broadcastToAll(Server.SET_TURN + playerTurnIndex);
                }
            }

            else {
                write(Server.ERROR);
            }
        }

        else if (data.contains(Server.ASK_TO_DRAW)) {
            Card card = board.draw();

            if (card != null) {
                write(Server.DRAW_CARDS + card.toString());
                broadcastToAll(Server.DREW_CARDS + username + " " +
                    board.getPlayer(username).getHandSize() + "\n" + 
                    Server.SET_TURN + indexOfPlayersTurn());
            }

            else {
                write(Server.ERROR);
            }
        }
    }

    @Override
    /**
     * Implemented method from runnable that listens for message from the
     * client, and then decodes and processes that message.
     */
    public void run() {
        String data;

        data = read();
        decode(data);

        while (!socket.isClosed()) {
            data = read();
            decode(data);
        } 
    }
}
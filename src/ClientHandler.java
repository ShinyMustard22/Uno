import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import cards.Card;

public class ClientHandler implements Runnable {
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
    private static GameState board = new GameState();

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    
    private String username;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException ex) {
            killEverything(socket, in, out);
        }
    }

    private boolean alreadyUsedName(String name) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (name.equals(clientHandler.username)) {
                return true;
            }
        }

        return false;
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

    private void broadcastMessage(String data) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != this) {
                clientHandler.write(data);
            }
        }
    }

    private void killEverything(Socket socket, DataInputStream in, DataOutputStream out) {
        board.removePlayer(username);
        clientHandlers.remove(this);
        broadcastMessage(Server.REMOVE_PLAYER + username);
        if (clientHandlers.size() != 0 && !board.gameHasStarted()) {
            clientHandlers.get(0).write(Server.SET_LEADER);
        }

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
            while (username.contains(" ") || alreadyUsedName(username)) {
                if (username.contains(" ")) {
                    write(Server.INVALID_USERNAME);
                }
                
                else {
                    write(Server.TAKEN_USERNAME);
                }
                
                username = read();
                username = username.substring(Server.NAME.length());
            }

            board.addPlayer(username);
            boolean isLeader = clientHandlers.size() == 0;

            write(Server.INIT_PLAYER_LIST + board.getPlayerList() + "\n" +
                Server.AM_LEADER + isLeader + "\n" + Server.UNO_TIME);

            broadcastMessage(Server.ADD_PLAYER + username);
            clientHandlers.add(this);
        }

        else if (data.contains(Server.GAME_STARTED)) {
            board.startGame();
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.write(Server.FIRST_CARD + board.getLastPlayedCard().toString() + "\n" +
                Server.INIT_PLAYER_HAND + board.getPlayer(username).getCardList());
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

            if (board.play(card, index)) {
                write(Server.PLAY_CARD + strCard + "\n" +
                    Server.SOMEBODY_PLAYED_CARD + strCard);
                broadcastMessage(Server.SOMEBODY_PLAYED_CARD + strCard);
            }
        }

        else if (data.contains(Server.ASK_TO_DRAW)) {
            Card card = board.draw(username);

            if (card != null) {
                write(Server.DRAW_CARDS + card.toString());
            }
        }
    }

    @Override
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
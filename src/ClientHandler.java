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
    
    private Player player;

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
            if (name.equals(clientHandler.player.getUsername())) {
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
        board.removePlayer(player);
        clientHandlers.remove(this);
        broadcastMessage(Server.REMOVE_PLAYER + player.getUsername());
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
            killEverything(socket, in, out);
            return;
        }

        if (data.contains(Server.NAME)) {
            String name = data.substring(Server.NAME.length());
            while (name.contains(" ") || alreadyUsedName(name)) {
                if (name.contains(" ")) {
                    write(Server.INVALID_USERNAME);
                }
                
                else {
                    write(Server.TAKEN_USERNAME);
                }
                
                name = read();
                name = name.substring(Server.NAME.length());
            }

            player = board.addPlayer(name);
            boolean isLeader = clientHandlers.size() == 0;

            write(Server.INIT_PLAYER_LIST + board.getPlayerList() + "\n" +
                Server.AM_LEADER + isLeader);

            broadcastMessage(Server.ADD_PLAYER + name);
            clientHandlers.add(this);
        }

        else if (data.contains(Server.GAME_STARTED)) {
            board.startGame();
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.write(Server.FIRST_CARD + board.getLastPlayedCard().toString() + "\n" +
                Server.INIT_PLAYER_HAND + clientHandler.player.getCardList());
            }
        }

        /*
        if (!board.gameHasStarted() || !board.getCurrentPlayer().equals(player)) {
            return;
        }
        */

        else if (data.contains(Server.PLAY_CARD)) {
            String strCard = data.substring(Server.PLAY_CARD.length());
            Card card = Card.decode(strCard);
            if (board.play(card)) {
                write(Server.PLAY_CARD + strCard + "\n" +
                    Server.SOMEBODY_PLAYED_CARD + strCard);
                broadcastMessage(Server.SOMEBODY_PLAYED_CARD + strCard);
            }
        }
    }

    @Override
    public void run() {
        String data;

        data = read();
        decode(data);

        while (socket.isConnected()) {
            data = read();
            decode(data);
        } 
    }
}
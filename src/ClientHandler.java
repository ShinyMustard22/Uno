import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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

    private void write(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException ex) {
            killEverything(socket, in, out);
        }
    }

    private void killEverything(Socket socket, DataInputStream in, DataOutputStream out) {
        board.removePlayer(player);
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
            write(Server.INIT_PLAYER_LIST + board.getPlayerList());

            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.write(Server.ADD_PLAYER + name);
            }
            clientHandlers.add(this);
        }

        else if (data.contains(Server.GAME_STARTED)) {
            write(Server.GAME_STARTED);
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
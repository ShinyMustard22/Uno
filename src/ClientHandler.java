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

            clientHandlers.add(this);
            String name = read();
            player = board.addPlayer(name);
            write(board.getPlayerList());
        } catch (IOException ex) {
            killEverything(socket, in, out);
        }
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

    @Override
    public void run() {
        String input;

        while (socket.isConnected()) {
            while (board.gameHasStarted() && board.getCurrentPlayer().equals(player)) {
                input = read();
                // do something
            }
        } 
    }
}
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
    private static GameBoard board;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Player player;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            player = new Player();
            clientHandlers.add(this);
	    
            if (clientHandlers.size() == 1) {
                board = new GameBoard();
                // Start game
            }

        } catch (IOException ex) {
            killEverything(socket, in, out);
        }
    }

    private void brodcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != this) {
                try {
                    clientHandler.out.writeUTF(message);
                    clientHandler.out.flush();
                } catch (IOException ex) {
                    killEverything(socket, in, out);
                }
            }
        }
    }

    private void playerLeft() {
        clientHandlers.remove(this);
        if (clientHandlers.size() == 0) {
            board = null;
            // End game
        }
    }

    private void killEverything(Socket socket, DataInputStream in, DataOutputStream out) {
        playerLeft();

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
            try {
                input = in.readUTF();
                while(input.isEmpty()) {
                    input = in.readUTF();
                }
                brodcastMessage(input);
            } catch (IOException ex) {
                killEverything(socket, in, out);
                break;
            }
        } 
    }
}

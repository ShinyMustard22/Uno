import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public TestClient() {
        try {
            socket = new Socket(Server.IP_ADDRESS, Server.PORT_NUM);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException ex) {
            
        }
    }

    private void sendMessage() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                out.writeUTF(message);
                out.flush();
            }

            scanner.close();
        } catch (IOException ex) {
            
        }  
    }

    private void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;

                while(socket.isConnected()) {
                    try {
                        message = in.readUTF();
                        while (message.isEmpty()) {
                            message = in.readUTF();
                        }
                        System.out.println(message);
                    } catch (IOException ex) {

                    }
                }
            }     
        }).start();
    }

    public static void main(String[] args) {
        TestClient client = new TestClient();
        client.listenForMessages();
        client.sendMessage();
    }
}

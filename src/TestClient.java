import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", Server.PORT_NUM);
            socket.close();
        } catch (UnknownHostException ex) {

        } catch (IOException ex) {
            
        }
        
    }
}

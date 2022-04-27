package server;
import java.io.*;

public class ServerInfo {
    public static final int PORT_NUM = 9001;

    public static String readInput(DataInputStream in) {
        String input;

        try {
            input = in.readUTF();
            while(input.isEmpty()) {
                input = in.readUTF();
            }
        } catch (IOException ex) {
            return null;
        }

        return input;
    }

    public static void writeOutput(DataOutputStream out, String output) {
        try{
            out.writeUTF(output);
        } catch (IOException ex) {
            System.exit(1);
        }
    }
}

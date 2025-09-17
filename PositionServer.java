import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class PositionServer {
    BufferedReader br = null;
    PrintStream ps = null;
    String line = null;
    String nReq = "";

    ServerSocket conn = null;
    Socket sock = null;
    int port = -1;
    HashMap<String, ArrayList<Position>> positions = new HashMap<>();

    String clientID = "";

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("usage: Server port");
            System.exit(1);
        }

        PositionServer server = new PositionServer();
        server.runServer(args[0]);
    }

    public void runServer(String portInput) {
        try {
            port = Integer.parseInt(portInput);
            conn = new ServerSocket(port);
            System.out.println("Serveur initialisé sur le port " + port);
        } catch (IOException e) {
            System.out.println("problème création socket serveur : " + e.getMessage());
            System.exit(1);
        }

        do {
            try {
                sock = conn.accept(); // attente connexion
                ServerThread serverThread = new ServerThread(sock, positions);
                serverThread.start();
                
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } while (true);
    }
}

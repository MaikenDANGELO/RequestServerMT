import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

public class PositionClient {
    public static void main(String[] args) {
        BufferedReader br = null;
        PrintStream ps = null;
        Socket sock = null;
        int port = -1;
        String clientID = UUID.randomUUID().toString();
        final Scanner scanner = new Scanner(System.in);

        if (args.length != 2) {
            System.out.println("Usage : SERVER_IP SERVER_PORT");
            System.exit(1);
        }

        try {
            port = Integer.parseInt(args[1]);
            sock = new Socket(args[0], port);
        }catch(IOException e) {
            System.out.println("Problème de connexion au serveur : " + e.getMessage());
            System.exit(1);
        }

        try {
            br = new BufferedReader((new InputStreamReader(sock.getInputStream())));
            ps = new PrintStream(sock.getOutputStream());

            // Envoi de l'ID client au serveur
            ps.println(clientID);

            do {
                PositionClient.handleSendRequest(scanner.nextLine(), ps, br, clientID, sock);
            } while(!sock.isClosed());
            
            br.close();
            ps.close();
            System.out.println("Fermeture de la connexion au serveur");
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void handleSendRequest(String userInput, PrintStream ps, BufferedReader br, String clientID, Socket sock) {
        String[] parsed = userInput.split(" ");
        final String req = parsed[0];
        final String[] args = parsed.length > 1 ? parsed[1].split(",") : new String[]{""};

        switch (req.toLowerCase()) {
            case "storepos":
                if(args.length < 3){
                    System.out.println("Req 1 : Not enough arguments (x,y,z)");
                    break;
                }
                ps.println("1");
                ps.println(clientID);
                try{
                    ps.println(String.format(Locale.ROOT, "%3.2f,%3.2f,%3.2f", Float.parseFloat(args[0]),
                            Float.parseFloat(args[1]), Float.parseFloat(args[2])));
                }catch(NumberFormatException e){
                    System.out.println("Req 1 : Arguments must be floats");
                    break;
                }

                try{
                    System.out.println(String.format("Réponse : %s", br.readLine()));
                    System.out.println(String.format("Réponse : %s", br.readLine()));
                }catch(IOException e){
                    System.out.println(String.format("Req 1 : %s", e.getMessage()));
                    break;
                }
                break;
        
            case "pathlen":
                ps.println("2");
                ps.println(clientID);
                ps.println("");

                try {
                    System.out.println(String.format("Réponse : %s", br.readLine()));
                    System.out.println(String.format("Réponse : %s", br.readLine()));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    break;
                }
                break;

            case "findpos":
                if (args.length < 4) {
                    System.out.println("Req 2 : Not enough arguments (f,x,y,z)");
                    break;
                }
                ps.println("3");
                ps.println(clientID);
                try {
                    ps.println(String.format(Locale.ROOT, "%3.2f,%3.2f,%3.2f,%3.2f", Float.parseFloat(args[0]),
                            Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3])));
                } catch (NumberFormatException e) {
                    System.out.println("Req 2 : Arguments must be floats");
                    break;
                }

                try {
                    System.out.println(String.format("Réponse : %s", br.readLine()));
                    System.out.println(String.format("Réponse : %s", br.readLine()));
                } catch (IOException e) {
                    System.out.println(String.format("Req 2 : %s" ,e.getMessage()));
                    break;
                }
                break;

            case "disconnect":
                ps.println("DISCONNECT");
                try {
                    System.out.println(String.format("Réponse : %s", br.readLine()));
                    sock.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    break;
                }
                break;

            default:
                System.out.println("Invalid Command");
                break;
        }
    }
}

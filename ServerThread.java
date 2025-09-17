import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private Socket sock;
    BufferedReader br;
    PrintStream ps;
    private String clientID = "";
    private HashMap<String, ArrayList<Position>> positions = new HashMap<>();
    private String nReq = "";

    public ServerThread(Socket sockComm, HashMap<String, ArrayList<Position>> positions) {
        this.sock = sockComm;
        this.positions = positions;
    }

    @Override
    public void run() {
        try {
            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            ps = new PrintStream(sock.getOutputStream());

            clientID = br.readLine();
            if (clientID == null || clientID.equals("")) {
                clientID = "";
                return;
            }

            if (positions.get(clientID) == null)
                positions.put(clientID, new ArrayList<Position>());
            requestLoop();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void requestLoop() throws IOException {
        while (!sock.isClosed()) {
            nReq = br.readLine();
            if (this.nReq == null) {
                System.out.println(String.format("disconnecting user %s", clientID));
                clientID = "";
                break;
            }
            if (nReq.equals("DISCONNECT")) {
                ps.println("disconnecting...");
                break;
            }
            try {
                Integer.parseInt(nReq);
            } catch (NumberFormatException e) {
                ps.println("ERR_REQ (requête invalide)");
                continue;
            }

            String reqClientId = br.readLine();

            if (!reqClientId.equals(clientID)) {
                ps.println("NOID_ERR identifiant invalide");
                continue;
            }

            String reqParams = br.readLine();
            ps.println(String.format("Requête N°%s bien reçue | Paramètres : %s | Identifiant : %s", nReq, reqParams,
                    reqClientId));

            switch (Integer.parseInt(nReq)) {
                case 1:
                    ps.println(handleReqOne(reqParams) ? "OK" : "ERR_REQ (format x, y, z)");
                    break;

                case 2:
                    ps.println(handleReqTwo());
                    break;

                case 3:
                    ps.println(handleReqThree(reqParams));
                    break;
                default:
                    ps.println("ERR_REQ (requête invalide)");
                    break;
            }
            System.out.println(String.format("%s : Requête_%s(%s) traitée avec succès !", reqClientId, nReq, reqParams));
        }
        br.close();
        ps.close();
        System.out.println("/!\\ Fermeture de la connexion au client");
    }

    boolean handleReqOne(String params) {
        String[] parsedParams = params.replace(" ", "").split(",");
        double x;
        double y;
        double z;

        if (parsedParams.length != 3)
            return false;

        try {
            x = Double.parseDouble(parsedParams[0]);
            y = Double.parseDouble(parsedParams[1]);
            z = Double.parseDouble(parsedParams[2]);
        } catch (NumberFormatException e) {
            return false;
        }

        positions.get(clientID).add(new Position(x, y, z));
        return true;
    }

    public double handleReqTwo() {
        double somme = 0.0;
        for (int i = 0; i < positions.get(clientID).size() - 1; i++) {
            for (int j = i + 1; j < positions.get(clientID).size(); j++) {
                somme += positions.get(clientID).get(i).distanceTo(positions.get(clientID).get(j));
            }
        }
        return somme;
    }

    public String handleReqThree(String params) {
        String[] parsedParams = params.replace(" ", "").split(",");
        double fp;
        double x;
        double y;
        double z;

        if (parsedParams.length != 4) {
            return "ERR_REQ (format fp, x, y, z)";
        }

        try {
            fp = Double.parseDouble(parsedParams[0]);
            x = Double.parseDouble(parsedParams[1]);
            y = Double.parseDouble(parsedParams[2]);
            z = Double.parseDouble(parsedParams[3]);
        } catch (NumberFormatException e) {
            return "ERR_REQ (format fp, x, y, z)";
        }

        for (Position p : positions.get(clientID)) {
            if (new Position(x, y, z).distanceTo(p) <= fp)
                return "TRUE";
        }

        return "FALSE";
    }

}

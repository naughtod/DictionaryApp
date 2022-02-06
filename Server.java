/*
    Student Name: David Naughton
    Student Number: 320479
    Student Email: dna@student.unimelb.edu.au 
*/

import java.io.*;
import java.net.*;

public class Server {
    static Trie t;
    private String pathToCsv;
    private boolean runFlag = true;

    public static void main(String[] args) {
        // reference object
        Server server = new Server();

        // parses command line arguments
        int port=server.parseCLA(args);

        t = new Trie();
        t.readCsv(server.pathToCsv);
        ServerGui sGui = new ServerGui(server);
        
        ServerSocket serverSocket;
        Socket client = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        ObjectOutputStream oot = null;

        try {
            serverSocket = new ServerSocket(port);

            while (server.runFlag) {
                client = serverSocket.accept();
                in = new DataInputStream(client.getInputStream());
                out = new DataOutputStream(client.getOutputStream());
                oot = new ObjectOutputStream(out);

                // Start a new thread for each connection
				new Thread(new Handler(client, in, out, oot, t)).start();
            }

            // close streams 
            in.close();
            oot.close();
            out.close();
            client.close();
            serverSocket.close();

        } catch (IOException e) {
            System.out.println("Server multi-threading error");
        }
    }

    // parser for command line arguments
    private int parseCLA(String[] args) {
        int port=0;

        if (args.length != 2) {
            System.out.println("Incorrect number of command line arguments");
            System.exit(0);
        } else {
            // validate port number
            try {
                port = Integer.parseInt(args[0]);
                if (port<1024 || port> 65353) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number, must be integer between 1024 and 65353");
                System.exit(0);
            }
            
            pathToCsv = args[1];   
        }
        return port;     
    }

    public void closeAndSaveData() {
        t.saveToCsv(pathToCsv);
        runFlag = false;
    }

}

/*
    Student Name: David Naughton
    Student Number: 320479
    Student Email: dna@stud
import java.net.ConnectException;ent.unimelb.edu.au 
*/

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private DataInputStream in;
    private DataOutputStream out;
    private ObjectInputStream oin;
    private Socket s;

    public static void main(String[] args) {
        Client c = new Client();
        
        // parse command line arguments
        int port = c.parseCLA(args);
        String address = args[0];         
        
        Gui gui = null;

        try {
            c.s = new Socket(address, port);

            c.in = new DataInputStream(c.s.getInputStream());
            c.out = new DataOutputStream(c.s.getOutputStream());
            c.oin = new ObjectInputStream(c.in);
            gui = new Gui(c);

        } catch (ConnectException ce) {
            System.out.println("Error connecting to server at the address and port provided");
        } catch (IOException e) {
            System.out.println("Error could not connect to server");
        }
    }

    public void add(String word, String meaning) {
        try {
            out.writeUTF("a," + word + "," + meaning);
            boolean response = in.readBoolean();
            String status = response==true ? "success - word added" : 
                "failed - word already exists";
            Gui.updateStatusBar(status, response);
            Gui.specialStatus = true;
        } catch (IOException e) { 
            System.out.println("Lost connection to server");
            closeStreams();
            System.exit(0);
        }
    }

    public void update(String word, String meaning) {
        try {
            out.writeUTF("u," + word + "," + meaning);
            boolean response = in.readBoolean();
            String status = response==true ? "success - meaning updated" : 
                "failed - word not found";
            Gui.updateStatusBar(status, response);
            Gui.specialStatus = true;
        } catch (IOException e) { 
            System.out.println("Lost connection to server");
            closeStreams();
            System.exit(0);    
        }
    }

    public boolean delete(String word) {
        boolean response = false;
        try {
            out.writeUTF("d," + word);
            response = in.readBoolean();
            String status = response==true ? "success - word deleted" : 
                "failed - word not found";
            Gui.updateStatusBar(status, response);
            Gui.specialStatus=true;
        } catch (IOException e) { 
            System.out.println("Lost connection to server");
            closeStreams();
            System.exit(0);
        }
        
        return response;
    }

    public String completeWords(String word) {
        String wordarr = "";

        try {
            out.writeUTF("w," + word);
            
            wordarr = (String) oin.readObject();

            /* manual byte method, crashing unexpectedly
            int byteCount = in.readInt();
            if (byteCount>0) {
                byte[] buffer = new byte[byteCount];
        
                in.read(buffer);
                wordarr = new String(buffer,"UTF-8");
            }*/

            // if no completions found then update status
            if (wordarr.isEmpty()) {
                Gui.updateStatusBar("failed - word not found",false);
                Gui.specialStatus = true;
            }

        } catch (IOException e) { 
            System.out.println("Lost connection to server");
            closeStreams();
            System.exit(0);
        } catch (ClassNotFoundException e) {
            System.out.println("Error reading word completions, class not found");
        }

        return wordarr;
    }

    public String getMeanings(String word) {
        String meaning="";
        try {
            out.writeUTF("m," + word);
            meaning = in.readUTF();
            
            // if no completions found then update status
            if (meaning.isEmpty()) Gui.updateStatusBar("failed - word not found",false);

        } catch (IOException e) { 
            System.out.println("Lost connection to server");
            closeStreams();
            System.exit(0);
        } 

        return meaning;
    }

    public void closeConnection() {
        try {
            // close handler
            out.writeUTF("close");
            
            // close client
            in.close();
            out.close();
            s.close();
        } catch (IOException e) { 
            System.out.println("Error trying to close client connection");
            System.exit(0);
        } catch (NullPointerException e) {
            // all good, no connection to close;
        }
    }

    public void closeStreams() {
        try {
            in.close();
            out.close();
            s.close();
        } catch (IOException e) { 
            System.out.println("Error trying to close streams");
            System.exit(0);
        } catch (NullPointerException e) {
            // all good, no streams to close;
        }
    }

    // parser for command line arguments
    private int parseCLA(String[] args) {
        int serverPort=0;

        if (args.length != 2) {
            System.out.println("Incorrect number of command line arguments");
            System.exit(0);
        } else {
            // validate port number
            try {
                serverPort = Integer.parseInt(args[1]);
                if (serverPort<1024 || serverPort> 65353) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number, must be integer between 1024 and 65353");
                System.exit(0);
            }
               
        }
        return serverPort;
    }

}

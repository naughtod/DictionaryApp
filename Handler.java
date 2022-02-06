/*
    Student Name: David Naughton
    Student Number: 320479
    Student Email: dna@student.unimelb.edu.au 
*/

import java.io.*;
import java.net.*;
import java.util.*;

public class Handler implements Runnable {
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    private ObjectOutputStream oot;
    private Trie t;
    
    Handler (Socket s, DataInputStream in, DataOutputStream out, ObjectOutputStream oot, Trie t) {
        // pass by reference means all threads share same Trie
        this.t = t;

        this.s = s;
        this.in = in;
        this.out = out;
        this.oot = oot;
    }

    public void run() {

        try {
            while (true) {
                String msg = in.readUTF();           
                if (msg.equals("close")) break;
                processRequest(msg);
            }
            
        } catch (IOException e) {
            System.out.println("Handler read error");
        }
    }

    private void processRequest(String request) {
        // split on first two commas only, comma allowed in meaning
        String[] arr = request.split(",",3);

        try {
            if (arr[0].equals("a")) {
                // add word, if exists return error
                if (t.search(arr[1])) {
                    out.writeBoolean(false); 
                } else{
                    t.insert(arr[1],arr[2]);
                    out.writeBoolean(true);
                }
                               
            } else if (arr[0].equals("u")) {
                // update word, if not in dictionary return error
                if (t.search(arr[1])) {
                    t.insert(arr[1],arr[2]);
                    out.writeBoolean(true); 
                } else{
                    out.writeBoolean(false);
                }
            
            } else if (arr[0].equals("d")) {
                // delete word, if not in dictionary return error
                if (t.search(arr[1])) {
                    t.delete(arr[1]);
                    out.writeBoolean(true); 
                } else {
                    out.writeBoolean(false);
                }
                
            } else if (arr[0].equals("w")) {
                // String word;
                String word = (arr.length<=1) ? "" : arr[1];
                ArrayList<String> words = t.completions(word, false);
                String output = String.join("\n",words);        
        
                // object output stream method, slow but sure
                oot.writeObject(output);

                /* manual bytes method, unexpected crashing issue
                byte[] bytes = output.getBytes();
                out.writeInt(bytes.length);
                out.flush();
                if (bytes.length>0) out.write(bytes);
                */
            } else if (arr[0].equals("m")) {
                String word = (arr.length<=1) ? "" : arr[1];
                String meaning = t.meaning(word);

                out.writeUTF(meaning);
            }
        } catch (IOException e) { 
            System.out.println("Error responding to client request");
        }
        
    }
}

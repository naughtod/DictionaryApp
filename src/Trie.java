/*
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
    Student Name: David Naughton
    Student Number: 320479
    Student Email: dna@student.unimelb.edu.au 
*/

import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.concurrent.locks.*;
import java.io.*;

public class Trie {
    // limit characters to english, required to control space efficiency
    private final int CHARS = 26;
    private Node root = new Node();
    ReadWriteLock lock = new ReentrantReadWriteLock();
    Lock writeLock;
    Lock readLock; 

    // default constructor
    Trie() {
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    // inner class for trie node
    class Node {
        private Node children[] = new Node[CHARS];
        private int childCount=0;
        private String meaning=null;
        private boolean isWord = false;
        
        // initialise children but don't create objects yet or will recurse inf
        Node() {
            for (int i=0;i<CHARS;i++) children[i]=null;
        }

    }
    
    // insert a word into trie
    public void insert(String word, String meaning) {
        int charIndex;
        
        try {
            writeLock.lock();

            Node head = root;
            for (int i=0;i<word.length();i++) {
                charIndex = word.charAt(i) - 'a';

                if (head.children[charIndex] == null) {
                    // letter is not at head, add to head
                    head.children[charIndex]=new Node();
                    head.childCount++;
                } 

                // iterate
                head = head.children[charIndex];
            }

            // head is now at end of word, set isWord to true
            head.isWord = true;
            head.meaning=meaning;
        } finally {
            writeLock.unlock();
        }
    }

    // find meaning of word
    public String meaning(String word) {
        try {
            readLock.lock();

            Node tail = find(word); 
            return (tail.meaning);
        } finally {
            readLock.unlock();
        }
        
    }

    // search for word, return true if found
    public boolean search(String word) {
        try {
            readLock.lock();
        
            Node tail = find(word); 
            return (tail!=null && tail.isWord);    
        } finally {
            readLock.unlock();
        }
    }

    // finds word if exists and returns it's tail
    private Node find(String word) {
        int charIndex;
        Node head = root;
        
        for (int i=0;i<word.length();i++) {
            charIndex = word.charAt(i) - 'a';

            if (head.children[charIndex] == null) {
                return null;
            } else {
                head = head.children[charIndex];
            }
        }
        
        return head;
    }

    // pass in initial variables
    public void delete(String word) {
        if (word.length() == 0) return;

        try {
            writeLock.lock();

            delete(word, root.children[word.charAt(0)-'a'], root, 1);
        } finally {
            writeLock.unlock();
        }
        
    }

    // since we have no way to traverse from leaf to root, use recursion
    private void delete(String word, Node currNode, Node prevNode, int depth) {
        // base case, at tail (n.b. word must exist for this case)
        if (depth==word.length()) {
            if (currNode.isWord) {
                currNode.isWord = false;
                currNode.meaning = null;
            }

            if (currNode.childCount==0) {
                // no other children exist, remove reference & delete node
                prevNode.children[word.charAt(depth-1) - 'a']=null;
                prevNode.childCount--;
                currNode=null;    
            }

            return;
        }
        
        // recurse to tail
        delete(word, currNode.children[word.charAt(depth) - 'a'], 
            currNode, depth+1);

        // on way back up, if no other children and not isWord then delete node
        if (currNode.childCount==0 && !currNode.isWord) {
            prevNode.children[word.charAt(depth-1) - 'a']=null;
            prevNode.childCount--;
            currNode=null;
        }
    }

    public ArrayList<String> completions(String word, boolean meanings) {
        try {
            readLock.lock();

            Node tail = find(word);
            ArrayList<String> comps = new ArrayList<String>();
        
            // early exit
            if (tail==null) return comps;
        
            completions(tail,"", comps, word, meanings);
            return comps;
        } finally {
            readLock.unlock();
        }        
    }

    // recursive completions
    private void completions(Node curr, String path, ArrayList<String> comps, String word, boolean meanings) {
        // if null then finished
        if (curr != null) {
            
            if (curr.isWord) {
                // get path string
                if (!meanings) {
                    comps.add(word+path); 
                } else { 
                    comps.add(curr.meaning);
                }
            }

            // visit children depth first
            for (int i=0;i<CHARS;i++) {
                if(curr.children[i]!=null) {
                    completions(curr.children[i],path+(char)(97+i),comps, word, meanings);
                }
            }
        }
    }

    public void printTrie() {
        // create a queue
        Queue<Node> q = new LinkedList<>();

        // add root to queue
        q.add(root);

        // in order traversal
        while (!q.isEmpty()) {
            Node temp = q.poll();
            for (int i=0;i<CHARS;i++) {
                if (temp.children[i]!=null) {
                    System.out.println((char)(i+97));
                    q.add(temp.children[i]);
                }
            }
            System.out.println("******");
        }
    }

    // build trie from csv
    public void readCsv(String pathToCsv) {
        String row;
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
            
            while ((row = csvReader.readLine()) != null) {
                // build trie from parsed data

               
                // String[] Fields = CSVParser.Split(Test);
                // System.out.println(row);


                String[] word = row.split(",",2); 
                // System.out.println(word[1].replaceAll(",(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))"));

                int i;
                for (i=0;i<word[0].length();i++) {
                    int c = word[0].charAt(i)-'a';
                    if (c<0 || c>26) {
                        // System.out.println(i+":"+word[0].charAt(i));
                        break;
                    }
                }

                // only insert words which contain correct chars
                if (i==word[0].length()) {
                    // decode commas and line breaks
                    word[1] = word[1].replace((char)254,',').replace((char)255,'\n');
                    
                    insert(word[0],word[1]);
                    // System.out.println(word[0]);
                } 
            }

            csvReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("Dictionary load failed, file not found");
        } catch (IOException e) {
            System.out.println("Error reading dictionary into Trie");;
        }
        
    }

    // save trie to csv
    public void saveToCsv(String pathToCsv) {
        try (PrintWriter pw = new PrintWriter(new File(pathToCsv))) {
            StringBuilder sb = new StringBuilder();

            ArrayList<String> words = completions("", false);
            ArrayList<String> meanings = completions("", true);
            
            for (int i=0;i<words.size();i++) {
                sb.append(words.get(i));
                sb.append(",");

                // String encodedMeaning = "\"" + meanings.get(i).replace("\"", "\"\"") + "\"";

                // replace any commas and line breaks with chars not on keyboard
                sb.append(meanings.get(i).replace(',',(char)254).replace('\n',(char)255));
                sb.append('\n');
            }
            pw.write(sb.toString());

            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary save failed, file not found");
        } 
    }

}


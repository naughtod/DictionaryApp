/*
    Student Name: David Naughton
    Student Number: 320479
    Student Email: dna@student.unimelb.edu.au 
*/

import java.util.Date;
import java.text.DateFormat;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.event.DocumentEvent;
import javax.swing.text.PlainDocument;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;  
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Gui extends JFrame {

    private static final long serialVersionUID = 1L;
    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private GridBagLayout gbl= new GridBagLayout();
    private GridBagConstraints gbc= new GridBagConstraints();
    private JPanel mainPanel = new JPanel(gbl);
    private JTextField input;
    private JTextArea output;
    private JTextArea completions;
    static boolean specialStatus;
    static JLabel statusMessage;
    private HighlightPainter painter= new DefaultHighlightPainter(Color.YELLOW);
    JButton add;
    JButton del;
    JButton edit;
    private Client client;
    
    // GUI Constructor
    public Gui(Client c) {

        super("Dictionary App");
        client = c;
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                client.closeConnection();
            }
        });

        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(mainPanel);
        mainPanel.add(welcomeCard());
        pack();
        setVisible(true);
        // requestFocusInWindow();
        // input.selectAll();
        // input.requestFocusInWindow();
        
    }

    private JPanel welcomeCard() {
        /* welcome message at top of page */
        JPanel welcomePanel = new JPanel();
        JLabel welcomeMessage = new JLabel("Dave's Dictionary");
        welcomePanel.add(welcomeMessage);
        welcomePanel.setBackground(new Color(95,158,160));

        /* text output field */
        output = new JTextArea("[meaning(s)]");
        JLabel meaningHeading = new JLabel("Meaning(s)");
        JScrollPane outputScroll = new JScrollPane(output);
        output.setEditable(false);
        outputScroll.setPreferredSize(new Dimension(300,200));
        output.setBackground(new Color(245, 245, 220));
        output.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        output.setWrapStyleWord(true);
        output.setLineWrap(true);
        JPanel outputConsol = new JPanel(new BorderLayout());
        outputConsol.add(outputScroll);
        outputConsol.add(meaningHeading, BorderLayout.NORTH);

        /* text completion field */
        completions = new JTextArea("[completions]");
        JLabel compHeading = new JLabel("Completions");
        JScrollPane completionsScroll = new JScrollPane(completions);
        completions.setEditable(false);
        completionsScroll.setPreferredSize(new Dimension(200,200));
        completions.setBackground(new Color(245, 245, 220));
        completions.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        completions.setWrapStyleWord(true);
        completions.setLineWrap(true);
        JPanel completionConsol = new JPanel(new BorderLayout());
        completionConsol.add(completionsScroll);
        completionConsol.add(compHeading,BorderLayout.NORTH);

        JPanel textSection = new JPanel();
        textSection.add(completionConsol);
        textSection.add(outputConsol);

        /* text input field */
        input = new JTextField("[input]");
        input.setPreferredSize(new Dimension(287,26));
        input.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(input.getText().equals("[input]")) {
                    input.setText("");
                    // repaint();
                    // revalidate();
                }    
            }
        });

        // meaning input field
        JTextArea meaning = new JTextArea("[meaning(s) input]");
        JScrollPane meaningScroll = new JScrollPane(meaning);
        meaningScroll.setPreferredSize(new Dimension(505,52));
        meaning.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        meaning.setWrapStyleWord(true);
        meaning.setLineWrap(true);
        meaning.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(meaning.getText().equals("[meaning(s) input]")) {
                    meaning.setText("");
                    // repaint();
                    // revalidate();
                }
            }
        });

        /* buttons */
        JPanel buttons = new JPanel();
        //JButton query = new JButton("Query a word");
        add = new JButton("Add");
        add.setEnabled(false);
        del = new JButton("Delete");
        del.setEnabled(false);
        edit = new JButton("Edit");
        edit.setEnabled(false);

        ((PlainDocument) input.getDocument()).setDocumentFilter(new DocFilter());

        input.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                // called only for non text changes eg font, size etc.
            }
            public void removeUpdate(DocumentEvent e) {
                // update completions and output
                refresh();
            }
            public void insertUpdate(DocumentEvent e) {
                // update completions and output
                refresh();
            }
            
        });

        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (meaning.getText().isEmpty() || meaning.getText().equals("[meaning(s) input]")) {
                    updateStatusBar("failed, no meaning(s) input",false);    
                } else {
                    client.add(input.getText(),meaning.getText());
                    meaning.setText("");
                    refresh();
                    updateStatusBar("success, " + input.getText() + " added",true);    
                }
            }
        });
        
        del.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (client.delete(input.getText())) {
                    input.setText("");
                }
            }
        });

        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (meaning.getText().isEmpty() || meaning.getText().equals("[meaning(s) input]")) {
                    updateStatusBar("failed, no meaning(s) input",false);    
                } else {
                    client.update(input.getText(),meaning.getText());
                    meaning.setText("");
                    refresh();  
                }
            }
        });

        buttons.add(input);
        buttons.add(add);
        buttons.add(del);
        buttons.add(edit);

        // BorderLayout borderLayout = new BorderLayout();
        JPanel welcomeCard = new JPanel(gbl);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        welcomeCard.add(welcomePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        welcomeCard.add(textSection, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        welcomeCard.add(meaningScroll, gbc);        
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // gbc.gridwidth = 4;  
        welcomeCard.add(buttons, gbc);
       
        gbc.gridx = 0;
        gbc.gridy = 4;
        welcomeCard.add(buildStatusBar(), gbc);

        return welcomeCard;
    }

    public void refresh() {
        String inputText = input.getText();
        String words = client.completeWords(inputText);
        completions.setText(words);
        String first = (words.indexOf('\n')==-1) ? words: 
                                        words.substring(0,words.indexOf('\n'));

        boolean exists = first.equals(inputText);

        add.setEnabled(!exists && inputText.length()>0);
        del.setEnabled(exists);
        edit.setEnabled(exists);
        if (specialStatus) {
            specialStatus = false;
        } else if (exists) {
            updateStatusBar("success, word found", true);
        } else {
            updateStatusBar("",true);
        }   
        

        if (words.equals("")) {
            output.setText("");
        } else {
            output.setText(client.getMeanings(first));

            // if completions then highlight the first row which has 
            // corresponding meaning showing
            try {
                int startIndex = completions.getLineStartOffset(0);
                int endIndex = completions.getLineEndOffset(0);
                completions.getHighlighter().addHighlight(startIndex, endIndex, painter);                         
            } catch (BadLocationException e) {
                updateStatusBar("highlighting error, could not locate", false);
            }
            // set both scroll panes to top
            completions.setCaretPosition(0);
            output.setCaretPosition(0);
        }

    }   

    private JPanel buildStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusMessage = new JLabel("Welcome to Dave's Interactive Dictionary");
        JLabel dateTime = new JLabel();

        dateTime.setOpaque(true);//to set the color for jlabel
        dateTime.setBackground(Color.black);
        dateTime.setForeground(Color.WHITE);
        
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBar.setBackground(Color.LIGHT_GRAY);
        statusBar.add(statusMessage, BorderLayout.WEST);
        statusBar.add(dateTime, BorderLayout.EAST);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date now = new Date();
                String ss = DateFormat.getDateTimeInstance().format(now);
                dateTime.setText(ss);
                dateTime.setToolTipText("Welcome, Today is " + ss);
            }
        });
        timer.start();
        return statusBar;
    }

    static void updateStatusBar(String message, boolean success) {
        statusMessage.setText(message);
        if (success) {
            statusMessage.setForeground(Color.BLUE);
        } else {
            statusMessage.setForeground(Color.RED);
        }
    }
}

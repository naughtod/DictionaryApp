/*
    Student Name: David Naughton
    Student Number: 320479
    Student Email: dna@student.unimelb.edu.au 
*/

import java.awt.*;
import java.awt.event.*;  
import javax.swing.*;
import javax.swing.Timer;
import java.util.*;
import java.lang.Thread;
import javax.swing.JButton;

public class ServerGui extends JFrame {
    private Server server;
    private final int WIDTH = 180;
    private final int HEIGHT = 150;
    private GridBagLayout gbl= new GridBagLayout();
    private GridBagConstraints gbc= new GridBagConstraints();
    private JPanel mainPanel = new JPanel(gbl);

    ServerGui(Server s) {
        
        super("Dictionary Server");

        server = s;
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                server.closeAndSaveData();
                System.exit(0);
            }
        });

        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JButton killSwitch = new JButton("Kill Server");
        killSwitch.setForeground(Color.magenta);
        killSwitch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                server.closeAndSaveData();
                setVisible(false);
                dispose();
                System.exit(0);
            }
        });

        JLabel serverInfo = new JLabel("Clients Connected: 0");
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // minus 2 because Server is one thread and server gui is another
                // every other thread is a client
                serverInfo.setText("Clients Connected: " + (Thread.activeCount()-2));
            }
        });
        timer.start();

        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeMessage = new JLabel("Dave's Dictionary");
        welcomePanel.add(welcomeMessage, BorderLayout.NORTH);
        welcomePanel.setBackground(new Color(95,158,160));

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(welcomePanel, gbc);

        gbc.insets = new Insets(10,5,5,5);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(serverInfo, gbc);

        gbc.insets = new Insets(20,5,5,5);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(killSwitch, gbc);

        add(mainPanel);

        pack();
        setVisible(true);

    }

}

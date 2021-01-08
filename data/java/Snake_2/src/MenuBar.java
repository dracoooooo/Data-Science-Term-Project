// Represents the MenuBar and it's functionaltiy
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class MenuBar implements ActionListener {
    
    private JMenuBar menuBar;
    private JMenu file, /*options, */help;
    private JMenuItem exit, about;
    
    // Creates the menu bar for the frame
    public JMenuBar createMenuBar() {
        
        // Menu Bar
        menuBar = new JMenuBar();
        
        // File Menu
        file = new JMenu("File");
        exit = new JMenuItem("Exit");
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.META_MASK));
        exit.addActionListener(this);
        menuBar.add(file);
        file.add(exit);
        
        // Help Menu
        help = new JMenu("Help");
        about = new JMenuItem("About");
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.META_MASK));
        about.addActionListener(this);
        menuBar.add(help);
        help.add(about);
        
        return menuBar;
    }
    
    
    // Exits or displays the about window
    public void actionPerformed (ActionEvent event) {
        
        if (event.getSource() == exit)
            System.exit(0);
        
        else if (event.getSource() == about){
            
            JOptionPane.showMessageDialog(menuBar,  "Author: David Sawyer\n\n"+
                                                    "Press P or the spacebar to pause gameplay.", "About Snake", JOptionPane.PLAIN_MESSAGE);
        }
        
    }
}
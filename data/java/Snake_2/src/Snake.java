//This class contains the main method and ties everything together and packs it up.
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Snake extends JFrame {

    public Snake() {

        SnakePanel panel = new SnakePanel();
        add(panel);
        
        // Moves menu bar to the mac menu bar
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        
        MenuBar menuBar = new MenuBar();
        setJMenuBar(menuBar.createMenuBar());
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(602, 648);
        setLocationRelativeTo(null);
        setTitle("Snake");
           
        setResizable(false);
        setVisible(true);

        panel.initGameplay();
    }
    
    public static void main(String[] args) {
    
        new Snake();
    }
}